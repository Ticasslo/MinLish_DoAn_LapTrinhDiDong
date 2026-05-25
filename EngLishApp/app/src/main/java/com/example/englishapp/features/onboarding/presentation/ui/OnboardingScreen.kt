package com.example.englishapp.features.onboarding.presentation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    // 1. Tạo "trạng thái bộ lướt" quản lý 3 trang, mặc định bắt đầu từ trang 0
    val pagerState = rememberPagerState(pageCount = { 3 })

    // Khởi tạo scope để chạy hiệu ứng cuộn trang khi người dùng bấm nút
    val scope = rememberCoroutineScope()

    // Dữ liệu hiển thị cho từng trang
    val images = listOf("🧠", "📊", "🎯")
    val titles = listOf(
        "Lặp lại ngắt quãng",
        "Theo dõi tiến độ",
        "Đạt mục tiêu nhanh chóng"
    )
    val descriptions = listOf(
        "Học từ vựng hiệu quả hơn nhờ thuật toán khoa học tự động nhắc nhở đúng thời điểm vàng.",
        "Xem biểu đồ thống kê chi tiết số từ đã thuộc và tiến trình rèn luyện mỗi ngày của bạn.",
        "Tập trung vào những từ cốt lõi, nâng cao vốn từ vựng rõ rệt chỉ với 15 phút mỗi ngày."
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Nền sáng theo thiết kế
            .padding(24.dp)
    ) {
        // Nút "Bỏ qua" đặt ở góc trên bên phải
        if (pagerState.currentPage < 2) {
            Text(
                text = "Bỏ qua",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp)
                    .clickable { onFinished() }
            )
        }

        // 2. Dùng HorizontalPager để kích hoạt tính năng vuốt qua lại bằng tay
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
        ) { page -> // Biến 'page' này sẽ tự thay đổi từ 0 đến 2 tương ứng với trang đang hiển thị

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Hình ảnh đại diện
                Text(
                    text = images[page],
                    fontSize = 80.sp,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Tiêu đề lớn
                Text(
                    text = titles[page],
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground, // Màu chữ chuẩn tương phản
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mô tả chi tiết
                Text(
                    text = descriptions[page],
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Cụm điều hướng ở sát đáy màn hình
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 3. Các dấu chấm chỉ số trang (Page Indicator) di chuyển tự động khi vuốt
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 40.dp)
            ) {
                for (i in 0..2) {
                    val isSelected = i == pagerState.currentPage // Lấy vị trí trang hiện tại đang vuốt tới
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(width = if (isSelected) 24.dp else 8.dp, height = 8.dp) // Chỉ chỉ số trang hiện tại dài ra
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, // Đổi màu xanh hoặc outline
                                shape = CircleShape
                            )
                    )
                }
            }

            // 4. Nút bấm hành động cuối cùng
            Button(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        // Nếu chưa phải trang cuối, khi bấm nút ta dùng animateScrollToPage để cuộn mượt sang trang tiếp theo
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinished() // Đã lướt đến cuối cùng thì hoàn thành chuyển màn hình
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp), // Bo góc chuẩn 12px theo tài liệu DESIGN.md
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer // Màu xanh đặc trưng thương hiệu
                )
            ) {
                Text(
                    text = if (pagerState.currentPage == 2) "Bắt đầu ngay" else "Tiếp theo",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}