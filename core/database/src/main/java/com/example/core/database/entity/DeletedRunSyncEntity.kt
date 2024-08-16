package com.example.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entity that represents a locally deleted run that has not been synced with the server
@Entity
data class DeletedRunSyncEntity(
    @PrimaryKey(autoGenerate = false)
    val runId: String,
    val userId: String
)
