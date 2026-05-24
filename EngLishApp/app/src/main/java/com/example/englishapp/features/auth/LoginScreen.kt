package com.example.englishapp.features.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,      // Lệnh khi đăng nhập thành công -> Nhảy vào HomeScreen
    onRegisterClick: () -> Unit,     // Lệnh khi bấm đăng ký -> Nhảy sang RegisterScreen
    onForgotPasswordClick: () -> Unit // Lệnh khi bấm quên mật khẩu -> Nhảy sang ForgotPasswordScreen
) {
    // 1. Tạo các biến để hứng dữ liệu người dùng nhập vào ô Text
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Biến quản lý việc Ẩn hoặc Hiện mật khẩu (mặc định là ẩn = false)
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 2. Tiêu đề chào mừng (Sử dụng đúng style hệ thống font Nunito Bold)
        Text(
            text = "Chào mừng",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Đăng nhập để tiếp tục hành trình học từ vựng của bạn",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Ô nhập EMAIL (Dạng OutlinedField đúng chuẩn thiết kế DESIGN.md)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it }, // Cập nhật chữ liên tục khi người dùng gõ
            label = { Text("Email") },
            placeholder = { Text("example@gmail.com") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp), // Bo góc 12px đồng bộ
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Ô nhập MẬT KHẨU (Có nút bật tắt con mắt 👁️)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            // Lệnh mã hóa ký tự thành dấu chấm tròn đen nếu đang ẩn
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            // Nút Icon con mắt đặt ở cuối ô nhập liệu
            trailingIcon = {
                val icon = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = icon, contentDescription = "Ẩn/Hiện mật khẩu")
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 5. Dòng chữ "Quên mật khẩu?" đặt lệch về bên phải
        Text(
            text = "Quên mật khẩu?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary, // Màu xanh thương hiệu
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onForgotPasswordClick() } // Kích hoạt lệnh quên mật khẩu
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 6. Nút bấm ĐĂNG NHẬP lớn
        Button(
            onClick = {
                // Tạm thời click một cái là cho đăng nhập thành công luôn để test luồng
                onLoginSuccess()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp), // Bo góc chuẩn 12px theo tài liệu DESIGN.md
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer // Màu nền xanh đậm đà
            )
        ) {
            Text(
                text = "Đăng nhập",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 7. Dòng chữ điều hướng chuyển tài khoản dưới đáy
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Chưa có tài khoản? ", color = Color.Gray)
            Text(
                text = "Đăng ký ngay",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onRegisterClick() } // Kích hoạt lệnh đăng ký
            )
        }
    }
}