package com.example.core.domain.run

import com.example.core.domain.run.Run
import kotlin.time.Duration

interface SyncRunScheduler {

    suspend fun scheduleSync(syncType: SyncType)

    suspend fun cancelAllSyncs()

    sealed interface SyncType {
        data class FetchRuns(val interval: Duration): SyncType
        data class DeleteRun(val runId: String): SyncType
        class CreateRun(val run: Run, val mapPictureBytes: ByteArray): SyncType
    }
}