package com.example.englishapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.englishapp.R

// 1. Hệ thống bộ font Nunito (Dành cho tiêu đề lớn và nút bấm)
val NunitoFontFamily = FontFamily(
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extrabold, FontWeight.ExtraBold)
)

// 2. Hệ thống bộ font Inter (Ánh xạ chính xác theo các file bạn vừa cấu hình)
val InterFontFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal), // Ăn theo file inter_regular.ttf (Bản 18pt Regular)
    Font(R.font.inter_medium, FontWeight.Medium)   // Ăn theo file inter_medium.ttf (Bản 18pt Medium xịn)
)

// 3. Cấu hình các tầng chữ chuẩn theo tài liệu DESIGN.md của MinLish
val Typography = Typography(
    // Word Display - Dành riêng cho mặt trước của thẻ học từ vựng Flashcard
    displayLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.02).sp
    ),
    // Headline Lg - Dùng cho tên ứng dụng ở màn hình Splash, Onboarding
    headlineLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    // Headline Md - Tương đương Heading 1 (Ví dụ: "Chào mừng trở lại!")
    headlineMedium = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    // Headline Sm - Tương đương Heading 2 (Tiêu đề các mục nhỏ hoặc tên Card)
    headlineSmall = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    // Body Lg - Tương đương Body Medium (Dùng font Inter Medium cho layout dày đặc, ô nhập liệu)
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Body Md - Tương đương Body Base (Dùng font Inter Regular cho chữ thường, giải thích nghĩa)
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    // Label Lg - Cấu hình font hiển thị riêng trên các nút bấm hành động (Button)
    labelLarge = TextStyle(
        fontFamily = NunitoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.01.sp
    ),
    // Label Sm - Caption (Chữ chú thích nhỏ nhất như mốc thời gian, ghi chú dưới nút...)
    labelSmall = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    )
)