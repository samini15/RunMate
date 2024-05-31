package com.example.auth.presentation.register

import com.example.core.presentation.ui.UiText

sealed interface RegisterEvent {
    data object RegistrationSuccess: RegisterEvent
    data class Failure(val error: UiText): RegisterEvent
}