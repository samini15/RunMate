package com.example.run.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.example.core.database.dao.RunPendingSyncDao
import com.example.core.database.entity.DeletedRunSyncEntity
import com.example.core.database.entity.RunPendingSyncEntity
import com.example.core.database.mappers.toRunEntity
import com.example.core.domain.SessionStorage
import com.example.core.domain.run.Run
import com.example.core.domain.run.RunId
import com.example.core.domain.run.SyncRunScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerScheduler(
    context: Context,
    private val pendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
): SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)

    override suspend fun scheduleSync(syncType: SyncRunScheduler.SyncType) {
        when (syncType) {
            is SyncRunScheduler.SyncType.FetchRuns -> {
                scheduleFetchRunsWorker(syncType.interval)
            }
            is SyncRunScheduler.SyncType.DeleteRun -> {
                scheduleDeleteRunWorker(syncType.runId)
            }
            is SyncRunScheduler.SyncType.CreateRun -> {
                scheduleCreateRunWorker(syncType.run, syncType.mapPictureBytes)
            }
        }
    }

    private suspend fun scheduleCreateRunWorker(run: Run, mapPictureBytes: ByteArray) {
        val userId = sessionStorage.get()?.userId ?: return

        val pendingEntity = RunPendingSyncEntity(
            run = run.toRunEntity(),
            mapPictureBytes = mapPictureBytes,
            userId = userId
        )

        pendingSyncDao.upsertRunPendingSyncEntity(pendingEntity)

        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(CreateRunWorker.RUN_ID, pendingEntity.run.id)
                    .build()
            )
            .addTag(CREATE_WORK)
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    private suspend fun scheduleFetchRunsWorker(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager.getWorkInfosByTag(SYNC_WORK).get().isNotEmpty()
        }

        if (isSyncScheduled) {
            return
        }

        // Config WorkRequest
        val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(repeatInterval = interval.toJavaDuration())
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInitialDelay(30, TimeUnit.MINUTES)
            .addTag(SYNC_WORK)
            .build()

        workManager.enqueue(workRequest).await()
    }

    private suspend fun scheduleDeleteRunWorker(runId: RunId) {
        val userId = sessionStorage.get()?.userId ?: return

        val entity = DeletedRunSyncEntity(runId, userId)

        pendingSyncDao.upsertDeletedRunSyncEntity(entity)

        val workRequest = OneTimeWorkRequestBuilder<DeleteRunWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                Data.Builder()
                    .putString(DeleteRunWorker.RUN_ID, entity.runId)
                    .build()
            )
            .addTag(DELETE_WORK)
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    override suspend fun cancelAllSyncs() {
        workManager
            .cancelAllWork()
            .await()
    }

    companion object {
        const val SYNC_WORK = "sync_work"
        const val CREATE_WORK = "create_work"
        const val DELETE_WORK = "delete_work"
    }
}