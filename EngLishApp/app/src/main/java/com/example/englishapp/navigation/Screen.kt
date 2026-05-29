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
    object Progress : Screen("progress_screen")
    object ChangePassword : Screen("change_password_screen")
    object Notification : Screen("notification_screen")

    // Các màn hình thuộc tính năng quản lý từ vựng (Vocabulary)
    object MySets : Screen("my_sets_screen")
    object CreateSet : Screen("create_set_screen")
    object VocabList : Screen("vocab_list_screen/{setId}") {
        fun createRoute(setId: String) = "vocab_list_screen/$setId"
    }
    object Flashcard : Screen("flashcard_screen/{setId}/{mode}") {
        fun createRoute(setId: String, mode: String) = "flashcard_screen/$setId/$mode"
    }
    object SessionComplete : Screen("session_complete_screen")
}

