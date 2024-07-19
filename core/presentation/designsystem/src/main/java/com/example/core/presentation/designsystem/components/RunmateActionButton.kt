package com.example.core.presentation.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.presentation.designsystem.LocalSpacing
import com.example.core.presentation.designsystem.RunmateBlack
import com.example.core.presentation.designsystem.RunmateGray
import com.example.core.presentation.designsystem.RunmateTheme

@Composable
fun RunmateActionButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = RunmateGray,
            disabledContentColor = RunmateBlack
        ),
        shape = RoundedCornerShape(100f),
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.spaceSmall),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(spacing.spaceMedium)
                    .alpha(if (isLoading) 1f else 0f),
                strokeWidth = 1.5.dp,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                modifier = Modifier
                    .alpha(if (isLoading) 0f else 1f),
                text = text,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RunmateOutlinedActionButton(
    modifier: Modifier = Modifier,
    text: String,
    isLoading: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val spacing = LocalSpacing.current
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        border = BorderStroke(
            width = 0.5.dp,
            color = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(100f),
        modifier = modifier.height(IntrinsicSize.Min)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = spacing.spaceSmall),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(spacing.spaceMedium)
                    .alpha(if (isLoading) 1f else 0f),
                strokeWidth = 1.5.dp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                modifier = Modifier
                    .alpha(if (isLoading) 0f else 1f),
                text = text,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview
@Composable
private fun RunmateActionButtonPreview() {
    RunmateTheme {
        RunmateActionButton(text = "Sign up", isLoading = false) {

        }
    }
}

@Preview
@Composable
private fun RunmateOutlinedActionButtonPreview() {
    RunmateTheme {
        RunmateOutlinedActionButton(text = "Sign up", isLoading = false) {

        }
    }
}