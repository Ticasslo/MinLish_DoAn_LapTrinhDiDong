package com.example.englishapp.features.auth.data.repository

import android.content.Context
import com.example.englishapp.core.data.local.AppDatabase
import com.example.englishapp.core.data.local.dao.UserDao
import com.example.englishapp.core.data.mapper.toDomain
import com.example.englishapp.core.data.mapper.toEntity
import com.example.englishapp.core.data.model.User
import com.example.englishapp.core.data.remote.FirebaseService
import com.example.englishapp.core.data.sync.SyncWorker
import com.example.englishapp.core.util.NetworkUtil
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.domain.repository.IAuthRepository
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseService: FirebaseService,
    private val userDao: UserDao,
    private val appDatabase: AppDatabase,
    private val networkUtil: NetworkUtil,
    @ApplicationContext private val context: Context
) : IAuthRepository {

    override fun register(user: User, password: String): Flow<AuthResult<User>> = flow {
        if (!networkUtil.isOnline()) {
            emit(AuthResult.Error("Không có kết nối internet"))
            return@flow
        }
        emit(AuthResult.Loading)
        try {
            val authResult = firebaseService.auth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = authResult.user?.uid ?: ""
            val newUser = user.copy(userId = uid)
            
            // Lưu lên Firestore
            firebaseService.saveUser(newUser)
            // Lưu cache vào Local
            userDao.upsertUser(newUser.toEntity().copy(isSynced = true))
            
            // Kích hoạt đồng bộ ngay lập tức để lấy dữ liệu liên quan khác (nếu có)
            SyncWorker.startImmediate(context)
            
            emit(AuthResult.Success(newUser))
        } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
            emit(AuthResult.Error("Email đã tồn tại. Hãy Đăng nhập hoặc Quên mật khẩu."))
        } catch (e: Exception) {
            emit(AuthResult.Error(e.message ?: "Đăng ký thất bại, vui lòng thử lại sau"))
        }
    }

    override fun login(email: String, password: String): Flow<AuthResult<User>> = flow {
        if (!networkUtil.isOnline()) {
            emit(AuthResult.Error("Không có kết nối internet"))
            return@flow
        }
        emit(AuthResult.Loading)
        try {
            val authResult = firebaseService.auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: ""
            
            val document = firebaseService.usersCollection.document(uid).get().await()
            val userData = document.toObject(User::class.java)
            
            if (userData != null) {
                // Lưu cache vào Local
                userDao.upsertUser(userData.toEntity().copy(isSynced = true))
                
                // Kích hoạt đồng bộ dữ liệu toàn cục ngay sau khi đăng nhập thành công
                SyncWorker.startImmediate(context)
                
                emit(AuthResult.Success(userData))
            } else {
                emit(AuthResult.Error("Thông tin người dùng không tồn tại trên máy chủ"))
            }
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
            emit(AuthResult.Error("Email này chưa được đăng ký tài khoản"))
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            // Khi tính năng bảo vệ dò tìm email được bật, Firebase trả về lỗi này cho cả sai email và sai mật khẩu
            emit(AuthResult.Error("Email hoặc mật khẩu không chính xác, vui lòng thử lại"))
        } catch (e: Exception) {
            emit(AuthResult.Error("Lỗi đăng nhập: ${e.localizedMessage ?: "Vui lòng thử lại sau"}"))
        }
    }

    override suspend fun logout() {
        firebaseService.auth.signOut()
        appDatabase.clearAllTables()
    }

    override fun observeCurrentUser(): Flow<User?> {
        val uid = firebaseService.currentUserId ?: return flow { emit(null) }
        return userDao.observeUserById(uid).map { it?.toDomain() }
    }

    override fun getCurrentUser(): User? {
        return firebaseService.auth.currentUser?.toDomain()
    }

    override fun sendPasswordResetEmail(email: String): Flow<AuthResult<Unit>> = flow {
        if (!networkUtil.isOnline()) {
            emit(AuthResult.Error("Không có kết nối internet"))
            return@flow
        }
        emit(AuthResult.Loading)
        try {
            firebaseService.auth.sendPasswordResetEmail(email).await()
            emit(AuthResult.Success(Unit))
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidUserException) {
            emit(AuthResult.Error("Email này chưa được đăng ký trong hệ thống"))
        } catch (e: Exception) {
            emit(AuthResult.Error("Không thể gửi email khôi phục, vui lòng kiểm tra lại email hoặc kết nối mạng"))
        }
    }

    override fun updateUserProfile(goal: String, level: String): Flow<AuthResult<Unit>> = flow {
        emit(AuthResult.Loading)
        try {
            val uid = firebaseService.currentUserId
            if (uid != null) {
                // 1. Update Local trước (Offline-first)
                val localUser = userDao.getCurrentUser()
                if (localUser != null) {
                    val updatedEntity = localUser.copy(
                        goal = goal, 
                        level = level, 
                        isSynced = false,
                        updatedAt = System.currentTimeMillis()
                    )
                    userDao.upsertUser(updatedEntity)
                    
                    // 2. Nếu có mạng thì đẩy lên Firestore nguyên object để đảm bảo nhất quán
                    if (networkUtil.isOnline()) {
                        firebaseService.saveUser(updatedEntity.toDomain())
                        userDao.markUserAsSynced(uid)
                    } else {
                        // Nếu không có mạng, SyncWorker sẽ tự động làm việc này khi có mạng lại
                        SyncWorker.startImmediate(context)
                    }
                }
                
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
        // Lấy từ Local Cache trước
        val localUser = userDao.getUserById(uid).first()
        if (localUser != null) {
            emit(AuthResult.Success(localUser.toDomain()))
        }

        // Fetch mới từ server
        if (networkUtil.isOnline()) {
            try {
                val document = firebaseService.usersCollection.document(uid).get().await()
                val userData = document.toObject(User::class.java)
                if (userData != null) {
                    userDao.upsertUser(userData.toEntity().copy(isSynced = true))
                    emit(AuthResult.Success(userData))
                }
            } catch (e: Exception) {
                if (localUser == null) emit(AuthResult.Error("Không thể lấy dữ liệu từ máy chủ, vui lòng kiểm tra kết nối"))
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseService.auth.signInWithCredential(credential).await()
            val user = authResult.user
            if (user != null) {
                val uid = user.uid
                val doc = firebaseService.usersCollection.document(uid).get().await()
                var userData = doc.toObject(User::class.java)
                
                if (userData == null) {
                    userData = User(userId = uid, email = user.email ?: "", name = user.displayName ?: "User")
                    if (networkUtil.isOnline()) firebaseService.saveUser(userData)
                }
                
                userDao.upsertUser(userData.toEntity().copy(isSynced = true))
                
                // Kích hoạt đồng bộ ngay lập tức
                SyncWorker.startImmediate(context)

                AuthResult.Success(userData)
            } else {
                AuthResult.Error("Đăng nhập Google thất bại")
            }
        } catch (e: Exception) {
            AuthResult.Error("Lỗi xác thực Google hoặc tài khoản không hợp lệ")
        }
    }

    override fun changePassword(currentPassword: String, newPassword: String): Flow<AuthResult<Unit>> = flow {
        if (!networkUtil.isOnline()) {
            emit(AuthResult.Error("Không có kết nối internet"))
            return@flow
        }
        emit(AuthResult.Loading)
        try {
            val user = firebaseService.auth.currentUser
            if (user != null && user.email != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                
                // 1. Re-authenticate
                user.reauthenticate(credential).await()
                
                // 2. Update password
                user.updatePassword(newPassword).await()
                
                emit(AuthResult.Success(Unit))
            } else {
                emit(AuthResult.Error("Người dùng chưa đăng nhập hoặc không hợp lệ"))
            }
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            emit(AuthResult.Error("Mật khẩu hiện tại không chính xác"))
        } catch (e: Exception) {
            emit(AuthResult.Error(e.localizedMessage ?: "Đổi mật khẩu thất bại, vui lòng thử lại sau"))
        }
    }
}
