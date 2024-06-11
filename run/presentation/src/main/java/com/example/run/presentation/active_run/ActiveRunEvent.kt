package com.example.run.presentation.active_run

import com.example.core.presentation.ui.UiText

sealed interface ActiveRunEvent {
    data class Failure(val error: UiText): ActiveRunEvent
    data object RunSaved: ActiveRunEvent
}