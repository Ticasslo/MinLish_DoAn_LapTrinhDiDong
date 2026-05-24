package com.example.englishapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.englishapp.navigation.AppNavigation
import com.example.englishapp.core.ui.theme.EngLishAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EngLishAppTheme {
                // Kích hoạt sơ đồ điều hướng tổng
                AppNavigation()
            }
        }
    }
}