package com.example.runmate.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.auth.presentation.login.LoginScreenRoot
import com.example.auth.presentation.register.RegisterScreenRoot
import com.example.auth.presentation.welcome.WelcomeScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController,
    isLoggedIn: Boolean
) {
    NavHost(navController = navController, startDestination = if (isLoggedIn) Route.RUN else Route.AUTH) {
        authGraph(navController)
        runGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = Route.WELCOME, route = Route.AUTH) {
        composable(route = Route.WELCOME) {
            WelcomeScreenRoot(
                onSignUpClick = {
                    navController.navigate(Route.REGISTER)
                },
                onSignInClick = {
                    navController.navigate(Route.LOGIN)
                }
            )
        }
        composable(route = Route.REGISTER) {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate(Route.LOGIN) {
                        popUpTo(route = Route.REGISTER) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate(Route.LOGIN)
                }
            )
        }
        composable(route = Route.LOGIN) {
            LoginScreenRoot(
                onSignUpClick = {
                    navController.navigate(Route.REGISTER) {
                        popUpTo(route = Route.LOGIN) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onLoginSuccess = {
                    navController.navigate(Route.RUN) {
                        popUpTo(route = Route.AUTH) { // Pop backStack from Auth screens
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

private fun NavGraphBuilder.runGraph(navController: NavHostController) {
    navigation(startDestination = Route.RUN_OVERVIEW, route = Route.RUN) {
        composable(route = Route.RUN_OVERVIEW) {
            Text(text = "Run overview screen!")
        }
    }
}