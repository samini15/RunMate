package com.example.run.network.mappers

import com.example.core.domain.location.Location
import com.example.core.domain.run.Run
import com.example.run.network.model.CreateRunRequest
import com.example.run.network.model.RunDto
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunDto.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        distanceMeters = distanceMeters,
        dateTimeUTC = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        location = Location(lat, long),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}

fun Run.toCreateRunRequest(): CreateRunRequest {
    return CreateRunRequest(
        id = id!!,
        durationMillis = duration.inWholeMilliseconds,
        epochMillis = dateTimeUTC.toEpochSecond() * 1000L,
        distanceMeters = distanceMeters,
        lat = location.latitude,
        long = location.longitude,
        totalElevationMeters = totalElevationMeters,
        avgSpeedKmh = avgSpeedKmh,
        maxSpeedKmh = maxSpeedKmh
    )
}