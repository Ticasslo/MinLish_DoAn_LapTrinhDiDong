package com.example.englishapp.features.auth.data.repository

import com.example.englishapp.core.data.model.User
import com.example.englishapp.core.util.NetworkUtil
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val networkUtil: NetworkUtil
) : IAuthRepository {

    override fun register(user: User, password: String): Flow<AuthResult<User>> = flow {
        if (!networkUtil.isOnline()) {
            emit(AuthResult.Error("Không có kết nối internet"))
            return@flow
        }
        emit(AuthResult.Loading)
        try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = authResult.user?.uid ?: ""
            val newUser = user.copy(userId = uid)
            
            firestore.collection("users").document(uid).set(newUser).await()
            emit(AuthResult.Success(newUser))
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Đăng ký thất bại"))
        }
    }

    override fun login(email: String, password: String): Flow<AuthResult<User>> = flow {
        if (!networkUtil.isOnline()) {
            emit(AuthResult.Error("Không có kết nối internet"))
            return@flow
        }
        emit(AuthResult.Loading)
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: ""
            
            val document = firestore.collection("users").document(uid).get().await()
            val userData = document.toObject(User::class.java)
            
            if (userData != null) {
                emit(AuthResult.Success(userData))
            } else {
                emit(AuthResult.Error("Không tìm thấy dữ liệu người dùng"))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Sai email hoặc mật khẩu"))
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser?.let {
            User(userId = it.uid, email = it.email ?: "")
        }
    }

    override fun sendPasswordResetEmail(email: String): Flow<AuthResult<Unit>> = flow {
        if (!networkUtil.isOnline()) {
            emit(AuthResult.Error("Không có kết nối internet"))
            return@flow
        }
        emit(AuthResult.Loading)
        try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            emit(AuthResult.Success(Unit))
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Không thể gửi email khôi phục"))
        }
    }

    override fun updateUserProfile(goal: String, level: String): Flow<AuthResult<Unit>> = flow {
        if (!networkUtil.isOnline()) {
            emit(AuthResult.Error("Không có kết nối internet"))
            return@flow
        }
        emit(AuthResult.Loading)
        try {
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                firestore.collection("users").document(uid).update(
                    mapOf(
                        "goal" to goal,
                        "level" to level
                    )
                ).await()
                emit(AuthResult.Success(Unit))
            } else {
                emit(AuthResult.Error("Người dùng chưa đăng nhập"))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Cập nhật profile thất bại"))
        }
    }

    override fun getUserData(uid: String): Flow<AuthResult<User>> = flow {
        emit(AuthResult.Loading)
        try {
            val document = firestore.collection("users").document(uid).get().await()
            val userData = document.toObject(User::class.java)
            if (userData != null) {
                emit(AuthResult.Success(userData))
            } else {
                emit(AuthResult.Error("Không tìm thấy dữ liệu người dùng"))
            }
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Lỗi lấy dữ liệu người dùng"))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                val uid = user.uid
                val doc = firestore.collection("users").document(uid).get().await()
                val userData = doc.toObject(User::class.java)
                if (userData != null) {
                    AuthResult.Success(userData)
                } else {
                    // Nếu user mới chưa có profile trong Firestore
                    val newUser = User(
                        userId = uid,
                        email = user.email ?: "",
                        name = user.displayName ?: "Người dùng mới"
                    )
                    firestore.collection("users").document(uid).set(newUser).await()
                    AuthResult.Success(newUser)
                }
            } else {
                AuthResult.Error("Đăng nhập Google thất bại")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Lỗi đăng nhập Google")
        }
    }
}
