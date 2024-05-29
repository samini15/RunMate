package com.example.auth.presentation.register

sealed interface RegisterAction {
    data object OnTogglePasswordVisibility: RegisterAction
    data object OnLoginClick: RegisterAction
    data object OnRegisterClick: RegisterAction
}