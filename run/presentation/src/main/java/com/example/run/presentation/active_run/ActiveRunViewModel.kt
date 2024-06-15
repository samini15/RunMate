package com.example.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.run.domain.RunningTracker
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ActiveRunViewModel(
    private val runningTracker: RunningTracker
): ViewModel() {

    var state by mutableStateOf(ActiveRunState())
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private val shouldTrack = snapshotFlow { state.shouldTrack } // React to changes of the state...
        .stateIn(viewModelScope, SharingStarted.Lazily, state.shouldTrack)
    private val hasLocationPermission = MutableStateFlow(false)

    private val isTracking = combine(shouldTrack, hasLocationPermission) { shouldTrack, hasPermission ->
        shouldTrack && hasPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        hasLocationPermission.onEach { hasPermission ->
            if (hasPermission) {
                runningTracker.startObservingLocation()
            } else {
                runningTracker.stopObservingLocation()
            }
        }.launchIn(viewModelScope)

        isTracking.onEach { isTracking ->
            runningTracker.setIsTracking(isTracking)
        }.launchIn(viewModelScope)

        // Listen to changes of CurrentLocation and update state
        runningTracker
            .currentLocation
            .onEach {
                state = state.copy(currentLocation = it?.location)
            }.launchIn(viewModelScope)

        // Listen to changes of RunData and update state
        runningTracker
            .runData
            .onEach {
                state = state.copy(runData = it)
            }.launchIn(viewModelScope)

        // Listen to changes of ElapsedTime and update state
        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(elapsedTime = it)
            }.launchIn(viewModelScope)
    }

    fun onAction(action: ActiveRunAction) {
        when (action) {
            ActiveRunAction.OnFinishRunClick -> {

            }
            ActiveRunAction.OnResumeRunClick -> {
                state = state.copy(shouldTrack = true)
            }
            ActiveRunAction.OnToggleRunClick -> {
                state = state.copy(
                    hasStartedRunning = true,
                    shouldTrack = !state.shouldTrack
                )
            }
            ActiveRunAction.OnBackClick -> {
                state = state.copy(shouldTrack = false)
            }
            is ActiveRunAction.SubmitLocationPermissionInfo -> {
                hasLocationPermission.update { action.acceptedLocationPermission }
                state = state.copy(
                    showLocationRationale = action.showLocationRationale
                )
            }
            is ActiveRunAction.SubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showLocationRationale = action.showNotificationRationale
                )
            }
            ActiveRunAction.DismissRationaleDialog -> {
                state = state.copy(
                    showLocationRationale = false,
                    showNotificationRationale = false
                )
            }
        }
    }
}