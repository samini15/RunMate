@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.run.presentation.active_run

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.presentation.designsystem.LocalSpacing
import com.example.core.presentation.designsystem.RunmateTheme
import com.example.core.presentation.designsystem.StartIcon
import com.example.core.presentation.designsystem.StopIcon
import com.example.core.presentation.designsystem.components.RunmateFAB
import com.example.core.presentation.designsystem.components.RunmateScaffold
import com.example.core.presentation.designsystem.components.RunmateToolbar
import com.example.run.presentation.R
import com.example.run.presentation.active_run.components.RunDataCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun ActiveRunScreenRoot(
    viewModel: ActiveRunViewModel = koinViewModel()
) {
    ActiveRunScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onAction: (ActiveRunAction) -> Unit
) {
    val spacing = LocalSpacing.current
    RunmateScaffold(
        withGradient = false,
        topAppBar = {
            RunmateToolbar(
                showBackButton = true,
                title = stringResource(id = R.string.active_run),
                onBackClick = {
                    onAction(ActiveRunAction.OnBackClick)
                }
            )
        },
        floatingActionButton = {
            RunmateFAB(icon = if (state.shouldTrack) {
                StopIcon
            } else {
                StartIcon
            }, iconSize = 20.dp,
                contentDescription = if (state.shouldTrack) {
                    stringResource(id = R.string.pause_run)
                } else {
                    stringResource(id = R.string.start_run)
                }
            ) {
                onAction(ActiveRunAction.OnToggleRunClick)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            RunDataCard(
                modifier = Modifier
                    .padding(spacing.spaceSmall)
                    .padding(padding)
                    .fillMaxWidth(),
                elapsedTime = state.elapsedTime,
                runData = state.runData
            )
        }
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunmateTheme {
        ActiveRunScreen(state = ActiveRunState(), onAction = {})
    }
}