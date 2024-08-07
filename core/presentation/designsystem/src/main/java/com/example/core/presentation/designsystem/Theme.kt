package com.example.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = RunmateGreen,
    background = RunmateBlack,
    surface = RunmateDarkGray,
    secondary = RunmateWhite,
    tertiary = RunmateWhite,
    primaryContainer = RunmateGreen30,
    onPrimary = RunmateBlack,
    onBackground = RunmateWhite,
    onSurface = RunmateWhite,
    onSurfaceVariant = RunmateGray,
    error = RunmateDarkRed,
    errorContainer = RunmateDarkRed5
)

@Composable
fun RunmateTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}