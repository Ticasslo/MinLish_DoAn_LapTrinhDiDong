package com.example.englishapp.core.data.repository

import com.example.englishapp.core.util.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

abstract class BaseRepository(private val networkUtil: NetworkUtil) {

    // Thực hiện một network call và wrap kết quả vào Result.
    // Tự động kiểm tra kết nối mạng trước khi gọi
    protected suspend fun <T> safeNetworkCall(
        call: suspend () -> T
    ): Result<T> = withContext(Dispatchers.IO) {
        if (!networkUtil.isOnline()) {
            return@withContext Result.failure(IOException("No internet connection"))
        }

        try {
            Result.success(call())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Thực hiện lưu local trước, sau đó thử sync lên remote.
    // Nếu sync thất bại, item vẫn ở local với isSynced = false
    protected suspend fun <T> syncItem(
        localOp: suspend () -> Unit,
        remoteOp: suspend () -> T,
        onSyncSuccess: suspend (T) -> Unit
    ) {
        // 1. Luôn thực hiện local trước
        localOp()

        // 2. Thử sync nếu có mạng
        if (networkUtil.isOnline()) {
            try {
                val result = remoteOp()
                onSyncSuccess(result)
            } catch (e: Exception) {
                // Sync thất bại, không sao cả vì local đã lưu với isSynced = false
                e.printStackTrace()
            }
        }
    }
}
