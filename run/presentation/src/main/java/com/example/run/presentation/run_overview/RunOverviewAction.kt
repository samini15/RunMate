package com.example.run.presentation.run_overview

import com.example.run.presentation.run_overview.model.RunUI

sealed interface RunOverviewAction {
    data object OnStartClick: RunOverviewAction
    data object OnLogoutClick: RunOverviewAction
    data object OnAnalyticsClick: RunOverviewAction
    data class DeleteRun(val runUi: RunUI): RunOverviewAction
}