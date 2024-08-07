package com.example.run.presentation.run_overview.model

data class RunUI(
    val id: String,
    val duration: String,
    val distance: String,
    val dateTime: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val pace: String,
    val totalElevation: String,
    val mapPictureUrl: String
)
