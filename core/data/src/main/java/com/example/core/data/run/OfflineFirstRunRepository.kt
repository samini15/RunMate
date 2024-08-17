package com.example.core.data.run

import com.example.core.database.dao.RunPendingSyncDao
import com.example.core.database.mappers.toRun
import com.example.core.domain.SessionStorage
import com.example.core.domain.run.LocalRunDataSource
import com.example.core.domain.run.RemoteRunDataSource
import com.example.core.domain.run.Run
import com.example.core.domain.run.RunId
import com.example.core.domain.run.RunRepository
import com.example.core.domain.run.SyncRunScheduler
import com.example.core.domain.util.DataError
import com.example.core.domain.util.EmptyResult
import com.example.core.domain.util.Result
import com.example.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class OfflineFirstRunRepository(
    private val localDataSource: LocalRunDataSource,
    private val remoteDataSource: RemoteRunDataSource,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val syncRunScheduler: SyncRunScheduler,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
): RunRepository {

    override fun getRuns(): Flow<List<Run>> = localDataSource.getRuns()

    // Fetch remote data and save it locally
    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when(val result = remoteDataSource.getRuns()) {
            is Result.Failure -> result.asEmptyDataResult()
            is Result.Success -> {
                // Different Scope which lives as long as the application
                // Avoid cancellation when saving runs locally
                applicationScope.async {
                    localDataSource.upsertRuns(runs = result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localDataSource.upsertRun(run)

        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }

        val runWithId = run.copy(id = localResult.data)
        return when (val remoteResult = remoteDataSource.postRun(runWithId, mapPicture)) {
            is Result.Failure -> {
                Timber.tag("OfflineFirstRunRepository").d("upsertRun: %s", remoteResult.error)
                applicationScope.launch {
                    syncRunScheduler.scheduleSync(SyncRunScheduler.SyncType.CreateRun(runWithId, mapPicture))
                }.join()
                Result.Success(Unit)
            }
            is Result.Success -> {
                applicationScope.async {
                    localDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localDataSource.deleteRun(id)


        // Edge case where the run is created in offline-mode
        // And then deleted in offline-mode as well. In that case
        // We don't need to sync anything
        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(id) != null
        if (isPendingSync) {
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }

        // Avoid cancellation when deleting runs remotely
        val remoteResult = applicationScope.async {
            remoteDataSource.deleteRun(id)
        }.await()

        if (remoteResult is Result.Failure) {
            applicationScope.launch {
                syncRunScheduler.scheduleSync(SyncRunScheduler.SyncType.DeleteRun(id))
            }.join()
        }
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            // Created locally but failed to sync with server
            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            // Deleted locally but failed to sync with server
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId)
            }
            val createJobs = createdRuns
                .await()
                .map {
                    launch {
                        val run = it.run.toRun()
                        when (remoteDataSource.postRun(run, it.mapPictureBytes)) {
                            is Result.Failure -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteRunPendingSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            val deletedJobs = deletedRuns
                .await()
                .map {
                    launch {
                        when (remoteDataSource.deleteRun(it.runId)) {
                            is Result.Failure -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteDeletedRunSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            // Join = Wait for all jobs to finish
            createJobs.forEach { it.join() }
            deletedJobs.forEach { it.join() }
        }
    }
}