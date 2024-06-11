package com.example.core.presentation.ui

import android.annotation.SuppressLint
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration

@SuppressLint("DefaultLocale")
fun Duration.formatted(): String {
    val totalSeconds = inWholeSeconds
    val minutes = String.format("%02d", (totalSeconds % 3600) / 60)
    val hours = String.format("%02d", totalSeconds / 3600)
    val seconds = String.format("%02d", (totalSeconds % 60))

    return "$hours:$minutes:$seconds"
}

fun Double.toFormattedKm(): String {
    val distanceKm = this / 1000.0
    return "${distanceKm.roundToDecimals(1)} km"
}

@SuppressLint("DefaultLocale")
fun Duration.toFormattedPace(distanceMeters: Double): String {
    val distanceKm = distanceMeters / 1000.0
    if (this == Duration.ZERO || distanceKm <= 0.0) {
        return "-"
    }
    val secondsPerKm = (this.inWholeSeconds / distanceKm).roundToInt()
    val averagePaceMinutes = secondsPerKm / 60
    val averagePaceSeconds = String.format("%02d", secondsPerKm % 60)
    return "$averagePaceMinutes:$averagePaceSeconds / km"
}


// Example: 5.367.roundToDecimals(2) -> 5.36
// Detail ==> factor = 10^2(decimalCount) = 100
// return round(5.367 * 100 (factor)) / 100 = 5.36
private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}