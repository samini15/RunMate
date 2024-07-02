@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.run.presentation.active_run

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.presentation.designsystem.LocalSpacing
import com.example.core.presentation.designsystem.RunmateTheme
import com.example.core.presentation.designsystem.StartIcon
import com.example.core.presentation.designsystem.StopIcon
import com.example.core.presentation.designsystem.components.RunmateDialog
import com.example.core.presentation.designsystem.components.RunmateFAB
import com.example.core.presentation.designsystem.components.RunmateOutlinedActionButton
import com.example.core.presentation.designsystem.components.RunmateScaffold
import com.example.core.presentation.designsystem.components.RunmateToolbar
import com.example.run.presentation.R
import com.example.run.presentation.active_run.components.RunDataCard
import com.example.run.presentation.active_run.maps.TrackerMap
import com.example.run.presentation.util.hasLocationPermission
import com.example.run.presentation.util.hasNotificationPermission
import com.example.run.presentation.util.requestRunmatePermissions
import com.example.run.presentation.util.shouldShowLocationPermissionRationale
import com.example.run.presentation.util.shouldShowNotificationPermissionRationale
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
    val context = LocalContext.current

    // region Permission handling
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasCourseLocationPermission = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val hasFineLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions[Manifest.permission.POST_NOTIFICATIONS] == true
        } else true

        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        // Location
        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = hasCourseLocationPermission && hasFineLocationPermission,
                showLocationRationale = showLocationRationale
            )
        )
        // Notification
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotificationPermission,
                showNotificationRationale = showNotificationRationale
            )
        )
    }
    // endregion Permission Handling

    // LaunchedEffect = true --> The first launch of the screen
    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        // Location
        onAction(
            ActiveRunAction.SubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission(),
                showLocationRationale = showLocationRationale
            )
        )
        // Notification
        onAction(
            ActiveRunAction.SubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationRationale = showNotificationRationale
            )
        )

        if (!showLocationRationale && !showNotificationRationale) {
            permissionLauncher.requestRunmatePermissions(context)
        }
    }

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
            TrackerMap(
                modifier = Modifier.fillMaxSize(),
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = {}
            )

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

    if (state.showLocationRationale || state.showNotificationRationale) {
        RunmateDialog(
            title = stringResource(id = R.string.permission_required),
            description = when {
                state.showLocationRationale && state.showNotificationRationale -> {
                    stringResource(id = R.string.notification_location_rationale)
                }
                state.showLocationRationale -> {
                    stringResource(id = R.string.location_rationale)
                }
                else -> stringResource(id = R.string.notification_rationale)
                 },
            onDismiss = { /* Dismiss not allowed */ },
            primaryButton = { 
                RunmateOutlinedActionButton(text = stringResource(id = R.string.okay), isLoading = false) {
                    onAction(ActiveRunAction.DismissRationaleDialog)

                    permissionLauncher.requestRunmatePermissions(context)
                }
            }
        )
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    RunmateTheme {
        ActiveRunScreen(state = ActiveRunState(), onAction = {})
    }
}