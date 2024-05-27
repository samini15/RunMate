package com.example.auth.presentation.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.presentation.R
import com.example.core.presentation.designsystem.LocalSpacing
import com.example.core.presentation.designsystem.LogoIcon
import com.example.core.presentation.designsystem.RunmateTheme
import com.example.core.presentation.designsystem.components.GradientBackground
import com.example.core.presentation.designsystem.components.RunmateActionButton
import com.example.core.presentation.designsystem.components.RunmateOutlinedActionButton

@Composable
fun WelcomeScreenRoot(
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    WelcomeScreen(onAction = { action ->
        when (action) {
            WelcomeAction.OnSignInClick -> onSignInClick()
            WelcomeAction.OnSignUpAction -> onSignUpClick()
        }
    })
}
@Composable
fun WelcomeScreen(
    onAction: (WelcomeAction) -> Unit
) {
    val spacing = LocalSpacing.current
    GradientBackground(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            RunmateLogoVertical()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.spaceMedium)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = stringResource(id = R.string.welcome_to_runmate),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(spacing.spaceSmall))

            Text(
                text = stringResource(id = R.string.runmate_description),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(spacing.spaceLarge))

            RunmateOutlinedActionButton(
                text = stringResource(id = R.string.sign_in),
                isLoading = false,
                modifier = Modifier.fillMaxWidth()
            ) {
                onAction(WelcomeAction.OnSignInClick)
            }

            Spacer(modifier = Modifier.height(spacing.spaceMedium))
            
            RunmateActionButton(
                text = stringResource(id = R.string.sign_up),
                isLoading = false,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                onAction(WelcomeAction.OnSignUpAction)
            }
        }
    }
}

@Composable
private fun RunmateLogoVertical(
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = LogoIcon,
            contentDescription = "Logo",
            tint = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(spacing.spaceMedium))

        Text(
            text = stringResource(id = R.string.runmate),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    RunmateTheme {
        WelcomeScreen {

        }
    }
}