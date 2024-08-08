package com.example.run.network.model

import kotlinx.serialization.Serializable

@Serializable
data class RunDto(
    val id: String,
    val durationMillis: Long,
    val distanceMeters: Int,
    val dateTimeUtc: String,
    val lat: Double,
    val long: Double,
    val totalElevationMeters: Int,
    val avgSpeedKmh: Double,
    val maxSpeedKmh: Double,
    val mapPictureUrl: String,
)
