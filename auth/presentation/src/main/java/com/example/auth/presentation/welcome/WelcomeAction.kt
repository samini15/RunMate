package com.example.auth.presentation.welcome

sealed interface WelcomeAction {
    data object OnSignInClick: WelcomeAction
    data object OnSignUpAction: WelcomeAction
}