package com.example.englishapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text // Tạm thời dùng Text làm màn hình giả lập cho Onboarding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.presentation.login.LoginScreen
import com.example.englishapp.features.auth.presentation.register.RegisterScreen
import com.example.englishapp.features.auth.presentation.forgot_password.ForgotPasswordScreen
import com.example.englishapp.features.auth.presentation.setup.InitialSetupScreen
import com.example.englishapp.features.auth.presentation.viewmodel.AuthViewModel
import com.example.englishapp.features.home.presentation.ui.HomeScreen
import com.example.englishapp.features.profile.presentation.ui.ProfileScreen
import com.example.englishapp.features.profile.presentation.ui.ChangePasswordScreen
import com.example.englishapp.features.onboarding.presentation.ui.OnboardingScreen
import com.example.englishapp.features.splash.presentation.ui.SplashScreen
import com.example.englishapp.features.vocab.presentation.mysets.MySetsScreen
import com.example.englishapp.features.vocab.presentation.create_edit.CreateSetScreen
import androidx.compose.runtime.remember
import com.example.englishapp.features.learn.presentation.viewmodel.LearnViewModel
import com.example.englishapp.features.vocab.presentation.vocab_list.VocabListScreen
import com.example.englishapp.features.learn.presentation.flashcard.FlashcardScreen
import com.example.englishapp.features.learn.presentation.complete.SessionCompleteScreen
import android.widget.Toast


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
                    // SỬA CHỖ NÀY: Ra lệnh nhảy thẳng sang màn hình Login
                    // popUpTo giúp xóa luôn màn Onboarding khỏi bộ nhớ để bấm nút Back không bị quay lại nữa
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
                onNotificationClick = { /* TODO */ },
                onReviewClick = { _ -> navController.navigate(Screen.MySets.route) },
                onLearnClick = { _ -> navController.navigate(Screen.MySets.route) },
                onAddClick = { navController.navigate(Screen.CreateSet.route) },
                onNavItemClick = { index ->
                    when (index) {
                        1 -> navController.navigate(Screen.MySets.route)
                        4 -> navController.navigate(Screen.Profile.route)
                    }
                }
            )
        }

        // 4b. Màn hình Hồ sơ (Profile)
        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavItemClick = { index ->
                    when (index) {
                        0 -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        1 -> navController.navigate(Screen.MySets.route)
                    }
                },
                onLogoutSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSettingsClick = {
                    navController.navigate(Screen.ChangePassword.route)
                }
            )
        }

        // 4c. Đổi mật khẩu
        composable(route = Screen.ChangePassword.route) {
            ChangePasswordScreen(
                onBackClick = { navController.popBackStack() },
                onNavItemClick = { index ->
                    when (index) {
                        0 -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        4 -> navController.popBackStack()
                    }
                }
            )
        }

        // 5. Màn hình Đăng ký
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

        // 6. Màn hình Thiết lập (Setup)
        composable(route = Screen.Setup.route) {
            InitialSetupScreen(
                onSetupComplete = {
                    // Thiết lập xong -> Vào Home
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        // 7. Màn hình Quên mật khẩu
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

        // 8. Các màn hình thuộc module Vocabulary (Thư viện từ vựng)
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
                onNavItemClick = { index ->
                    when (index) {
                        0 -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                        4 -> navController.navigate(Screen.Profile.route)
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
            VocabListScreen(
                setId = setId,
                onBackClick = { navController.popBackStack() },
                onLearnClick = { id ->
                    navController.navigate(Screen.Flashcard.createRoute(id, "learn"))
                },
                onReviewClick = { id ->
                    navController.navigate(Screen.Flashcard.createRoute(id, "review"))
                }
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
                    // SỬA: Không popUpTo ở đây để SessionComplete vẫn có thể truy cập ViewModel
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

    }
}