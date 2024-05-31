package com.example.runmate

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.auth.presentation.register.RegisterScreenRoot
import com.example.auth.presentation.welcome.WelcomeScreenRoot

@Composable
fun NavigationRoot(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "auth") {
        authGraph(navController)
    }
}

private fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(startDestination = "welcome", route = "auth") {
        composable(route = "welcome") {
            WelcomeScreenRoot(
                onSignUpClick = {
                    navController.navigate("register")
                },
                onSignInClick = {
                    navController.navigate("login")
                }
            )
        }
        composable(route = "register") {
            RegisterScreenRoot(
                onSignInClick = {
                    navController.navigate("login") {
                        popUpTo(route = "register") {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                },
                onSuccessfulRegistration = {
                    navController.navigate("login")
                }
            )
        }
        composable(route = "login") {
            Text(text = "Login")
        }
    }
}