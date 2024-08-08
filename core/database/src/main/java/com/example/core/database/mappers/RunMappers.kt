package com.example.core.database.mappers

import com.example.core.database.entity.RunEntity
import com.example.core.domain.location.Location
import com.example.core.domain.run.Run
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUTC = Instant.parse(dateTimeUtc)
            .atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(
            latitude = latitude,
            longitude = longitude
        ),
        maxSpeedKmh = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl,

    )

}

fun Run.toRunEntity(): RunEntity {
    return RunEntity(
        id = id ?: ObjectId().toHexString(),
        durationMillis = duration.inWholeMilliseconds,
        distanceMeters = distanceMeters,
        dateTimeUtc = dateTimeUTC.toInstant().toString(),
        latitude = location.latitude,
        longitude = location.longitude,
        maxSpeedKmh = maxSpeedKmh,
        avgSpeedKm = avgSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}