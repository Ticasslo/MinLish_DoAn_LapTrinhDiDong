package com.example.englishapp

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.englishapp.core.data.sync.SyncWorker
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class EngLishApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        
        // 1. Đặt lịch chạy ngầm định kỳ (Background Fetch)
        SyncWorker.schedule(this)
        
        // 2. Đồng bộ khi khởi động
        SyncWorker.startImmediate(this)

        // 3. Đồng bộ mỗi khi người dùng quay lại App (Foreground Sync)
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                // Chạy mỗi khi App chuyển từ Background lên Foreground
                SyncWorker.startImmediate(this@EngLishApp)
            }
        })
    }
}
