package com.example.englishapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Text // Tạm thời dùng Text làm màn hình giả lập cho Onboarding
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.englishapp.features.auth.LoginScreen
import com.example.englishapp.features.auth.RegisterScreen
import com.example.englishapp.features.auth.ForgotPasswordScreen
import com.example.englishapp.features.onboarding.OnboardingScreen
import com.example.englishapp.features.splash.SplashScreen

@Composable
fun AppNavigation() {
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
                    // Khi hết 1.5 giây, điều hướng ngay sang màn hình Onboarding
                    // popUpTo dùng để xóa màn hình Splash khỏi bộ nhớ (bấm nút quay lại không bị dính màn Splash nữa)
                    navController.navigate(Screen.Onboarding.route) {
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

        // 3. Màn hình Đăng nhập (Dùng FILE THẬT vừa viết)
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Khi đăng nhập thành công, nhảy thẳng vào Home và xóa cụm Login khỏi bộ nhớ
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
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

        // 4. Màn hình Dashboard chính giả lập (Chờ làm ở bước tiếp theo)
        composable(route = Screen.Home.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "🎉 Tuyệt vời! Bạn đã vào màn hình Dashboard Chính (HomeScreen)!")
            }
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Đăng ký xong, đẩy thẳng vào Home, xóa sạch stack auth cũ đi
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLoginClick = {
                    // Muốn quay lại login thì chỉ cần popBackStack là xong
                    navController.popBackStack()
                }
            )
        }

        // 2. Tìm và thay thế khối ForgotPassword cũ:
        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onSendEmailSuccess = {
                    // Gửi email xong quay lại màn Đăng nhập kèm thông báo (sau này làm)
                    navController.popBackStack()
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

    }
}