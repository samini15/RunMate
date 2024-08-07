package com.example.core.domain.run

import com.example.core.domain.location.Location
import java.time.ZonedDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class Run(
    val id: String?, // null if new run
    val duration: Duration,
    val distanceMeters: Int,
    val dateTimeUTC: ZonedDateTime,
    val location: Location, // TODO use PLaces API of Google
    val maxSpeedKmh: Double,
    val totalElevationMeters: Int,
    val mapPictureUrl: String
) {
    val avgSpeedKmh: Double get() = (distanceMeters / 1000.0) / duration.toDouble(DurationUnit.HOURS)
}
