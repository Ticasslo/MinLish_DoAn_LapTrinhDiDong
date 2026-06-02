package com.example.englishapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.features.auth.presentation.login.LoginScreen
import com.example.englishapp.features.auth.presentation.register.RegisterScreen
import com.example.englishapp.features.auth.presentation.forgot_password.ForgotPasswordScreen
import com.example.englishapp.features.auth.presentation.setup.InitialSetupScreen
import com.example.englishapp.features.auth.presentation.viewmodel.AuthViewModel
import com.example.englishapp.features.home.presentation.ui.HomeScreen
import com.example.englishapp.features.profile.presentation.ui.ProfileScreen
import com.example.englishapp.features.profile.presentation.ui.ChangePasswordScreen
import com.example.englishapp.features.progress.presentation.ui.ProgressScreen
import com.example.englishapp.features.onboarding.presentation.ui.OnboardingScreen
import com.example.englishapp.features.splash.presentation.ui.SplashScreen
import com.example.englishapp.features.vocab.presentation.mysets.MySetsScreen
import com.example.englishapp.features.notification.presentation.ui.NotificationScreen
import com.example.englishapp.features.vocab.presentation.create_edit.CreateSetScreen
import androidx.compose.runtime.remember
import com.example.englishapp.features.learn.presentation.viewmodel.LearnViewModel
import com.example.englishapp.features.vocab.presentation.vocab_list.VocabListScreen
import com.example.englishapp.features.learn.presentation.flashcard.FlashcardScreen
import com.example.englishapp.features.learn.presentation.complete.SessionCompleteScreen
import com.example.englishapp.features.vocab.presentation.dictionary.DictionaryScreen


@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    // 1. Tạo bộ điều khiển hướng đi (NavController)
    val navController = rememberNavController()

    // 2. Thiết lập sơ đồ đường đi, đặt điểm xuất phát (startDestination) là màn Splash
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // Cấu hình Màn hình số 1: Splash
        composable(route = Screen.Splash.route) {
            SplashScreen(
                onTimeout = {
                    val destination = authViewModel.getStartDestination()
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Màn hình Hướng dẫn (Onboarding)
        composable(route = Screen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Màn hình Đăng nhập
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { user ->
                    // Kiểm tra xem User đã có mục tiêu (goal) chưa
                    if (user.goal.isNullOrEmpty()) {
                        // Nếu chưa có goal -> Bắt buộc vào Setup
                        navController.navigate(Screen.Setup.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        // Nếu đã có goal -> Nhảy thẳng vào Home
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                onRegisterClick = {
                    // Khi bấm Đăng ký ngay, nhảy sang màn Register
                    navController.navigate(Screen.Register.route)
                },
                onForgotPasswordClick = {
                    // Khi bấm Quên mật khẩu, nhảy sang màn ForgotPassword
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        // 4. Màn hình Dashboard chính
        composable(route = Screen.Home.route) {
            HomeScreen(
                onNotificationClick = { navController.navigate(Screen.Notification.route) },
                onReviewClick = { deck -> navController.navigate(Screen.VocabList.createRoute(deck.setId)) },
                onLearnClick = { deck -> navController.navigate(Screen.VocabList.createRoute(deck.setId)) },
                onAddClick = { navController.navigate(Screen.CreateSet.route) },
                onDetailClick = { navController.navigate(Screen.Progress.route) },
                onRecentClick = { deck -> navController.navigate(Screen.VocabList.createRoute(deck.setId)) },
                onSeeAllClick = { navController.navigate(Screen.MySets.route) },
                onNavItemClick = { index ->
                    val route = when (index) {
                        1 -> Screen.MySets.route
                        2 -> Screen.Progress.route
                        3 -> Screen.Profile.route
                        else -> null
                    }
                    route?.let {
                        navController.navigate(it) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        // 4b. Màn hình Tiến độ (Progress)
        composable(route = Screen.Progress.route) {
            ProgressScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationClick = { navController.navigate(Screen.Notification.route) },
                onNavItemClick = { index ->
                    val route = when (index) {
                        0 -> Screen.Home.route
                        1 -> Screen.MySets.route
                        3 -> Screen.Profile.route
                        else -> null
                    }
                    route?.let {
                        navController.navigate(it) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        // 5. Màn hình Thông báo
        composable(route = Screen.Notification.route) {
            NotificationScreen(
                onBackClick = { navController.popBackStack() },
                onActionClick = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // 6. Màn hình Hồ sơ (Profile)
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavItemClick = { index ->
                    val route = when (index) {
                        0 -> Screen.Home.route
                        1 -> Screen.MySets.route
                        2 -> Screen.Progress.route
                        else -> null
                    }
                    route?.let {
                        navController.navigate(it) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // 7. Màn hình Cài đặt
        composable(route = Screen.Settings.route) {
            com.example.englishapp.features.profile.presentation.ui.SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onChangePasswordClick = { navController.navigate(Screen.ChangePassword.route) },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // 8. Đổi mật khẩu
        composable(route = Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onNavItemClick = { index ->
                    val route = when (index) {
                        0 -> Screen.Home.route
                        1 -> Screen.MySets.route
                        2 -> Screen.Progress.route
                        else -> null
                    }
                    route?.let {
                        navController.navigate(it) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        // 9. Màn hình Đăng ký
        composable(route = Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Đăng ký xong -> Sang màn Setup để chọn Goal/Level
                    navController.navigate(Screen.Setup.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // 10. Màn hình Thiết lập ban đầu (Setup)
        composable(route = Screen.Setup.route) {
            InitialSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        // 11. Màn hình Quên mật khẩu
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onSendEmailSuccess = {
                    navController.popBackStack()
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        // 12. Quản lý Bộ từ vựng (Vocabulary)
        composable(route = Screen.MySets.route) {
            MySetsScreen(
                onSetClick = { setId ->
                    navController.navigate(Screen.VocabList.createRoute(setId))
                },
                onLearnClick = { setId ->
                    navController.navigate(Screen.VocabList.createRoute(setId))
                },
                onCreateSetClick = {
                    navController.navigate(Screen.CreateSet.route)
                },
                onNotificationClick = { 
                    navController.navigate(Screen.Notification.route) 
                },
                onNavItemClick = { index ->
                    val route = when (index) {
                        0 -> Screen.Home.route
                        2 -> Screen.Progress.route
                        3 -> Screen.Profile.route
                        else -> null
                    }
                    route?.let {
                        navController.navigate(it) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }

        composable(route = Screen.CreateSet.route) {
            CreateSetScreen(
                onBackClick = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable(route = Screen.VocabList.route) { backStackEntry ->
            val setId = backStackEntry.arguments?.getString("setId") ?: ""

            // Nhận dữ liệu prefill từ màn hình Dictionary (qua savedStateHandle)
            val prefillWord by backStackEntry.savedStateHandle.getStateFlow("prefill_word", "").collectAsState()
            val prefillPhonetic by backStackEntry.savedStateHandle.getStateFlow("prefill_phonetic", "").collectAsState()
            val prefillMeaning by backStackEntry.savedStateHandle.getStateFlow("prefill_meaning", "").collectAsState()
            val prefillDescription by backStackEntry.savedStateHandle.getStateFlow("prefill_description", "").collectAsState()
            val prefillExample by backStackEntry.savedStateHandle.getStateFlow("prefill_example", "").collectAsState()

            VocabListScreen(
                setId = setId,
                onBackClick = { navController.popBackStack() },
                onLearnClick = { id ->
                    navController.navigate(Screen.Flashcard.createRoute(id, "learn"))
                },
                onReviewClick = { id ->
                    navController.navigate(Screen.Flashcard.createRoute(id, "review"))
                },
                onLookupOnlineClick = {
                    navController.navigate(Screen.Dictionary.route)
                },
                prefillWord = prefillWord.ifBlank { null },
                prefillPhonetic = prefillPhonetic.ifBlank { null },
                prefillMeaning = prefillMeaning.ifBlank { null },
                prefillDescription = prefillDescription.ifBlank { null },
                prefillExample = prefillExample.ifBlank { null }
            )
        }

        composable(route = Screen.Flashcard.route) { backStackEntry ->
            val setId = backStackEntry.arguments?.getString("setId") ?: ""
            val mode = backStackEntry.arguments?.getString("mode") ?: "learn"

            FlashcardScreen(
                setId = setId,
                mode = mode,
                onBackClick = { navController.popBackStack() },
                onSessionComplete = {
                    // Không popUpTo ở đây để SessionComplete vẫn có thể truy cập ViewModel
                    navController.navigate(Screen.SessionComplete.route)
                }
            )
        }

        composable(route = Screen.SessionComplete.route) {
            val flashcardBackStackEntry = remember(it) {
                try {
                    // Lấy entry của Flashcard để dùng chung ViewModel
                    navController.getBackStackEntry(Screen.Flashcard.route)
                } catch (e: Exception) {
                    null
                }
            }

            if (flashcardBackStackEntry != null) {
                val learnViewModel: LearnViewModel = hiltViewModel(flashcardBackStackEntry)
                val uiState by learnViewModel.uiState.collectAsState()

                SessionCompleteScreen(
                    stats = uiState.sessionStats,
                    onContinueClick = {
                        // Khi tiếp tục, quay về và dọn dẹp flashcard cũ
                        navController.popBackStack(Screen.Flashcard.route, inclusive = true)
                    },
                    onHomeClick = {
                        // Về Home và dọn dẹp toàn bộ luồng học
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            } else {
                // Fallback nếu không tìm thấy entry (ví dụ: truy cập trực tiếp bằng route)
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        }

        composable(route = Screen.Dictionary.route) {
            DictionaryScreen(
                onBackClick = { navController.popBackStack() },
                onSaveWord = { word, phonetic, vietnameseMeaning, englishDefinition, example ->
                    // Pop khỏi Dictionary và quay lại VocabList với dữ liệu đã điền sẵn
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("prefill_word", word)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("prefill_phonetic", phonetic)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("prefill_meaning", vietnameseMeaning)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("prefill_description", englishDefinition)
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("prefill_example", example)
                    navController.popBackStack()
                }
            )
        }

    }
}