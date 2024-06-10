package com.example.core.presentation.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.core.presentation.designsystem.LocalSpacing
import com.example.core.presentation.designsystem.RunIcon
import com.example.core.presentation.designsystem.RunmateTheme

@Composable
fun RunmateFAB(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    iconSize: Dp = 25.dp,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    // Outer Box
    Box(
        modifier = Modifier
            .size(75.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Inner Box
        Box(
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(spacing.spaceSmall),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(iconSize),
                imageVector = icon,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview
@Composable
private fun RunmateFABPreview() {
    RunmateTheme {
        RunmateFAB(icon = RunIcon) {
            
        }
    }
}