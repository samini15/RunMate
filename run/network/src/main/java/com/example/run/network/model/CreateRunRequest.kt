package com.example.run.network.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateRunRequest(
    val id: String,
    val durationMillis: Long,
    val distanceMeters: Int,
    val epochMillis: Long,
    val lat: Double,
    val long: Double,
    val totalElevationMeters: Int,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
)
