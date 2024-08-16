package com.example.core.data.run

import android.util.Log
import com.example.core.domain.run.LocalRunDataSource
import com.example.core.domain.run.RemoteRunDataSource
import com.example.core.domain.run.Run
import com.example.core.domain.run.RunId
import com.example.core.domain.run.RunRepository
import com.example.core.domain.util.DataError
import com.example.core.domain.util.EmptyResult
import com.example.core.domain.util.Result
import com.example.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class OfflineFirstRunRepository(
    private val localDataSource: LocalRunDataSource,
    private val remoteDataSource: RemoteRunDataSource,
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

        // Avoid cancellation when deleting runs remotely
        val remoteResult = applicationScope.async {
            remoteDataSource.deleteRun(id)
        }.await()
    }
}