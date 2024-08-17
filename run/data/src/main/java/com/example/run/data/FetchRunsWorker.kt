package com.example.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.core.domain.run.RunRepository
import com.example.core.domain.util.DataError

class FetchRunsWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val runRepository: RunRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) {
            return Result.failure()
        }

        return when (val result = runRepository.fetchRuns()) {
            is com.example.core.domain.util.Result.Failure -> {
                result.error.toWorkerResult()
            }
            is com.example.core.domain.util.Result.Success -> Result.success()

        }
    }

}