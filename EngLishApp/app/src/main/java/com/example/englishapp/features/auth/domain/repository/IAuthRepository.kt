package com.example.englishapp.features.auth.domain.repository

import com.example.englishapp.core.data.model.User
import com.example.englishapp.features.auth.domain.model.AuthResult
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    // Đăng ký tài khoản mới kèm thông tin User
    fun register(user: User, password: String): Flow<AuthResult<User>>

    // Đăng nhập bằng Email/Password
    fun login(email: String, password: String): Flow<AuthResult<User>>

    // Đăng xuất
    suspend fun logout()

    // Lấy thông tin User hiện tại (nếu đã đăng nhập)
    fun observeCurrentUser(): Flow<User?>

    // Lấy thông tin User hiện tại (non-reactive)
    fun getCurrentUser(): User?

    // Quên mật khẩu
    fun sendPasswordResetEmail(email: String): Flow<AuthResult<Unit>>

    // Cập nhật thông tin profile (Goal, Level, Notifications)
    fun updateUserProfile(goal: String, level: String, pushEnabled: Boolean): Flow<AuthResult<Unit>>

    // Lấy đầy đủ thông tin User từ Firestore
    fun getUserData(uid: String): Flow<AuthResult<User>>

    // Đăng nhập bằng Google
    suspend fun signInWithGoogle(idToken: String): AuthResult<User>

    // Đổi mật khẩu
    fun changePassword(currentPassword: String, newPassword: String): Flow<AuthResult<Unit>>

    // Cập nhật cài đặt học tập (dailyGoal, reminderTime, pushEnabled)
    fun updateUserSettings(dailyGoal: Int, reminderTime: String, pushEnabled: Boolean): Flow<AuthResult<Unit>>

    // Cập nhật ảnh đại diện
    fun updateUserAvatar(avatarUrl: String): Flow<AuthResult<Unit>>
}
