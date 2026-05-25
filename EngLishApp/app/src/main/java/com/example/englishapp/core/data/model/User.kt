package com.example.englishapp.core.data.model

data class User(
    // PK - do Firebase Auth tạo tự động khi đăng ký
    // KHÔNG được null, unique
    // Ví dụ: "uid_abc123"
    val userId: String = "",

    // Người dùng nhập khi đăng ký
    // KHÔNG được null, 1-50 ký tự
    // Ví dụ: "Nguyễn Minh"
    val name: String = "",

    // Người dùng nhập khi đăng ký, unique
    // KHÔNG được null, đúng format email
    // Ví dụ: "minh@gmail.com"
    val email: String = "",

    // Lấy từ Google account hoặc để trống
    // Có thể null
    // Ví dụ: "https://lh3.googleusercontent.com/..."
    val avatar: String? = null,

    // Người dùng chọn khi đăng ký
    // KHÔNG được null, chỉ được 1 trong: "IELTS", "TOEIC", "Business", "Travel", "Communication"
    // Ví dụ: "IELTS"
    val goal: String = "",

    // Người dùng chọn khi đăng ký
    // KHÔNG được null, chỉ được 1 trong: "A1","A2","B1","B2","C1","C2"
    // Ví dụ: "B1"
    val level: String = "",

    // Hệ thống tự tính dựa trên accuracy + số từ đã thuộc
    // Có thể null nếu chưa học đủ để ước tính
    // Chỉ được 1 trong: "Beginner", "Intermediate", "Advanced"
    // Ví dụ: "Intermediate"
    val estimatedLevel: String? = null,

    // Người dùng thiết lập ở InitialSetupFragment hoặc ProfileFragment
    // KHÔNG được null, từ 5 đến 50, bước nhảy 5
    // Ví dụ: 10
    val dailyGoal: Int = 10,

    // Người dùng thiết lập ở InitialSetupFragment hoặc ProfileFragment
    // KHÔNG được null, format "HH:mm"
    // Ví dụ: "20:00"
    val reminderTime: String = "20:00",

    // Người dùng bật/tắt ở InitialSetupFragment hoặc ProfileFragment
    // KHÔNG được null, default true
    // Ví dụ: true
    val pushEnabled: Boolean = true,

    // Hệ thống tự tạo khi đăng ký thành công
    // KHÔNG được null
    // Ví dụ: 1716825600000
    val createdAt: Long = System.currentTimeMillis()
)