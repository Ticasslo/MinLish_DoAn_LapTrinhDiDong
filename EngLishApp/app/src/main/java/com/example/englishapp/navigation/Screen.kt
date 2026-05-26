package com.example.englishapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash_screen")
    object Onboarding : Screen("onboarding_screen")
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object ForgotPassword : Screen("forgot_password_screen")
    object Setup : Screen("setup_screen")
    object Home : Screen("home_screen")
    object Profile : Screen("profile_screen")
    object ChangePassword : Screen("change_password_screen")
}