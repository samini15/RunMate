package com.example.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.domain.SessionStorage
import com.example.core.domain.run.RunRepository
import com.example.core.domain.run.SyncRunScheduler
import com.example.run.presentation.run_overview.model.toRunUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
): ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(SyncRunScheduler.SyncType.FetchRuns(interval = 30.minutes))
        }

        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunUI() }
            state = state.copy(runs = runsUi)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }
    fun onAction(action: RunOverviewAction) {
        when (action) {
            RunOverviewAction.OnLogoutClick -> logout()
            RunOverviewAction.OnStartClick -> Unit
            is RunOverviewAction.DeleteRun -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            else -> Unit
        }
    }

    private fun logout() {
        // Avoid cancellation when for example the viewModel is cleared
        applicationScope.launch {
            syncRunScheduler.cancelAllSyncs()
            runRepository.deleteAllRuns()
            runRepository.logout()
            sessionStorage.set(null)
        }
    }
}