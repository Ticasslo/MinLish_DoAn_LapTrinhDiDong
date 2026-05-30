package com.example.englishapp.features.learn.presentation.complete

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.englishapp.R
import com.airbnb.lottie.compose.*
import com.example.englishapp.features.learn.presentation.viewmodel.SessionStats
import kotlin.math.roundToInt

@Composable
fun SessionCompleteScreen(
    stats: SessionStats,
    onContinueClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    val durationMinutes = ((System.currentTimeMillis() - stats.startTime) / 60000.0).roundToInt().coerceAtLeast(1)
    val accuracy = if (stats.totalStudied > 0) {
        (stats.correctCount.toFloat() / stats.totalStudied * 100).roundToInt()
    } else 0

    // Load hiệu ứng pháo hoa từ file của bạn
    val compositionResult = rememberLottieComposition(LottieCompositionSpec.RawRes(com.example.englishapp.R.raw.animation))
    val progress by animateLottieCompositionAsState(
        composition = compositionResult.value,
        iterations = LottieConstants.IterateForever
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF3D5AFE), Color(0xFF0031CA))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Hiển thị pháo hoa nền (full screen)
        if (compositionResult.isSuccess) {
            LottieAnimation(
                composition = compositionResult.value,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 2. Hiển thị Icon lồng trong Animation
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                // Pháo hoa làm nền cho Icon
                if (compositionResult.isSuccess) {
                    LottieAnimation(
                        composition = compositionResult.value,
                        progress = { progress },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                // Icon của bạn nằm chính giữa pháo hoa
                Image(
                    painter = painterResource(id = com.example.englishapp.R.drawable.ic_trophy_custom),
                    contentDescription = "Trophy Icon",
                    modifier = Modifier.size(350.dp) // Icon nhỏ hơn animation để đẹp hơn
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Phiên học hoàn thành! 🎉",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "Bạn đang làm rất tốt, hãy duy trì đà này nhé!",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Thẻ kết quả hiển thị thông số thực tế
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(label = "Từ đã học", value = "${stats.totalStudied}")
                    StatItem(label = "Chính xác", value = "$accuracy%")
                    StatItem(label = "Phút", value = "$durationMinutes")
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onContinueClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Tiếp tục học", color = Color(0xFF3D5AFE), fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onHomeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(Color.White, Color.White))),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Về trang chủ", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1D3B))
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}
