package com.example.auth.presentation.login

import com.example.core.presentation.ui.UiText

sealed interface LoginEvent {
    data object LoginSuccess: LoginEvent
    data class Failure(val error: UiText): LoginEvent
}