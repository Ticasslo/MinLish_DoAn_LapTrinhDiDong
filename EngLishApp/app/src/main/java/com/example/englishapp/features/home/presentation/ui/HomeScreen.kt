package com.example.englishapp.features.home.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.englishapp.R
import com.example.englishapp.features.home.presentation.viewmodel.HomeViewModel

// =============================================================================
// CÁC LỚP DỮ LIỆU ĐỊNH NGHĨA THÔNG TIN HIỂN THỊ TRÊN MÀN HÌNH HOME
// =============================================================================

// Thẻ đại diện cho các bộ từ vựng cần ôn tập lại (SRS)
data class ReviewDeck(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val isUrgent: Boolean = false
)

// Thẻ đại diện cho các bộ từ vựng mới học hôm nay
data class NewWordDeck(
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

// Thẻ đại diện cho hoạt động hoặc học phần đã học gần đây
data class RecentDeck(
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

// Đối tượng đại diện cho một nút bấm trên thanh điều hướng dưới cùng (Bottom Nav)
data class NavItem(val icon: ImageVector, val label: String)


// =============================================================================
// MÀN HÌNH CHÍNH (HOMESCREEN COMOSABLE)
// =============================================================================
@Composable
fun HomeScreen(
    onNotificationClick: () -> Unit = {},
    onReviewClick: (ReviewDeck) -> Unit = {},
    onLearnClick: (NewWordDeck) -> Unit = {},
    onDetailClick: () -> Unit = {},
    onSeeAllClick: () -> Unit = {},
    onNavItemClick: (Int) -> Unit = {},
    onAddClick: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    // 1. Thu thập trạng thái UI từ ViewModel bằng collectAsState
    val uiState by viewModel.uiState.collectAsState()

    // 2. Trích xuất các biến cần thiết từ uiState để code ngắn gọn, dễ đọc hơn
    val userName = uiState.user?.name ?: "Người dùng"
    val avatarUrl = uiState.user?.avatar
    
    // Tính toán tiến trình học từ vựng hôm nay (tránh chia cho 0 bằng cách dùng coerceAtLeast)
    val progress = (uiState.wordsToday.toFloat() / uiState.wordGoal.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
    
    // Hiệu ứng chuyển động mượt mà cho thanh tiến độ
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 700),
        label = "home_progress"
    )

    // Tính toán số từ ôn tập phân bổ tạm thời vào 2 bộ thẻ
    val deck1Count = (uiState.dueWordsCount + 1) / 2
    val deck2Count = uiState.dueWordsCount / 2

    // 3. Chuẩn bị danh sách dữ liệu để hiển thị lên giao diện
    val reviewDecks = listOf(
        ReviewDeck(Icons.Outlined.Work, "Business English", "$deck1Count từ cần ôn tập lại", "KHẨN CẤP", true),
        ReviewDeck(Icons.Outlined.Flight, "Travel Essentials", "$deck2Count từ cần ôn tập lại", "ĐẾN HẠN")
    )
    val newWordDecks = listOf(
        NewWordDeck(Icons.Outlined.Psychology, "IELTS Vocab", "10 từ mới đề xuất"),
        NewWordDeck(Icons.Outlined.Restaurant, "Daily Life", "5 từ mới đề xuất")
    )
    val recentDecks = listOf(
        RecentDeck(Icons.AutoMirrored.Outlined.MenuBook, "Oxford 3000 Keywords", "Lần cuối: 2 giờ trước • 85% thuộc"),
        RecentDeck(Icons.Outlined.Movie, "Movie Phrases", "Lần cuối: 1 ngày trước • 42% thuộc")
    )
    val navItems = listOf(
        NavItem(Icons.Filled.Home, stringResource(R.string.nav_home)),
        NavItem(Icons.AutoMirrored.Outlined.MenuBook, stringResource(R.string.nav_library)),
        NavItem(Icons.Outlined.School, stringResource(R.string.nav_learn)),
        NavItem(Icons.Outlined.BarChart, stringResource(R.string.nav_progress)),
        NavItem(Icons.Outlined.Person, stringResource(R.string.nav_profile))
    )

    // Khởi tạo trạng thái nút điều hướng đang được chọn (mặc định là trang chủ - vị trí 0)
    var selectedNav by remember { mutableIntStateOf(0) }

    // 4. Scaffold dựng bố cục chuẩn: Thanh tiêu đề trên, Thanh điều hướng dưới, Nút FAB nổi bật
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Màu nền từ hệ thống Theme
        topBar = { 
            // Gọi hàm vẽ Header ứng dụng ở trên cùng
            HomeTopBar(
                userName = userName,
                avatarUrl = avatarUrl,
                onNotificationClick = onNotificationClick
            ) 
        },
        bottomBar = {
            // Gọi hàm vẽ thanh điều hướng dưới cùng của màn hình
            HomeBottomBar(
                navItems = navItems,
                selectedIndex = selectedNav,
                onItemClick = { index ->
                    selectedNav = index
                    onNavItemClick(index)
                }
            )
        },
        floatingActionButton = {
            // Nút bấm tròn (FAB) thêm từ vựng mới nổi ở góc dưới bên phải
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape, // Nút tròn xoe
                containerColor = MaterialTheme.colorScheme.primary, // Màu xanh thương hiệu
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Thêm từ mới",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        // 5. Nội dung chính của màn hình chứa trong một Column cuộn dọc (verticalScroll)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // Kích hoạt chức năng cuộn dọc
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp) // Khoảng cách giữa các phần lớn là 28dp
        ) {
            // Mục 1: Khối tóm tắt tiến độ học tập hôm nay
            TodaySummarySection(
                streakDays = uiState.streakDays,
                wordsToday = uiState.wordsToday,
                wordGoal = uiState.wordGoal,
                animatedProgress = animatedProgress,
                onDetailClick = onDetailClick
            )

            // Mục 2: Phần danh sách các thẻ từ vựng cần ôn tập ngay
            ReviewSection(
                dueCount = uiState.dueWordsCount,
                decks = reviewDecks,
                onReviewClick = onReviewClick
            )

            // Mục 3: Phần gợi ý các bộ từ mới ngày hôm nay
            NewWordsSection(
                decks = newWordDecks,
                onLearnClick = onLearnClick
            )

            // Mục 4: Phần danh sách lịch sử học tập gần đây
            RecentSection(
                decks = recentDecks,
                onSeeAllClick = onSeeAllClick
            )
        }
    }
}


// =============================================================================
// CÁC HÀM COMPOSE THÀNH PHẦN CHI TIẾT (DỄ HIỂU, DỄ QUẢN LÝ)
// =============================================================================

/**
 * Hàm vẽ Thanh tiêu đề trên cùng (TopBar)
 */
@Composable
private fun HomeTopBar(
    userName: String,
    avatarUrl: String?,
    onNotificationClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp // Đổ bóng nhẹ 2dp theo thiết kế
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Tránh đè lên thanh trạng thái hệ thống
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Khối hiển thị ảnh đại diện và lời chào bên trái
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Khối tròn chứa Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!avatarUrl.isNullOrBlank()) {
                        // Nếu có ảnh đại diện, tải ảnh bằng AsyncImage của thư viện Coil
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Ảnh đại diện",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Nếu không có ảnh, lấy chữ cái đầu tiên của tên người dùng hiển thị
                        Text(
                            text = userName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Khối chữ xin chào
                Column {
                    Text(
                        text = "Xin chào,",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$userName!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Nút bấm chuông thông báo bên phải
            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Thông báo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * Khối hiển thị tiến trình ngày học hiện tại (Streak, thanh tiến độ, chi tiết)
 */
@Composable
private fun TodaySummarySection(
    streakDays: Int,
    wordsToday: Int,
    wordGoal: Int,
    animatedProgress: Float,
    onDetailClick: () -> Unit
) {
    // Chuyển tiến độ từ dạng số thập phân (0.0 -> 1.0) sang phần trăm (0 -> 100%)
    val percent = (animatedProgress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(2.dp, RoundedCornerShape(12.dp)), // Đổ bóng nhẹ 2dp, bo góc 12dp chuẩn
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Dòng đầu: Badge số ngày học liên tiếp (Streak) và mục tiêu ngày
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hộp đựng ngọn lửa streak
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "$streakDays ngày liên tiếp",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                // Chữ hiển thị số từ đã học / mục tiêu từ
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Mục tiêu hôm nay",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$wordsToday/$wordGoal từ",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dòng hai: Thanh tiến trình học tập
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50)) // Bo tròn dẹt hai đầu thanh
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Hộp vẽ phần tiến độ màu xanh primary chạy từ trái qua phải
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress) // Chiếm bề ngang theo phần trăm
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dòng ba: Hiển thị % hoàn thành và nút xem chi tiết
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$percent% hoàn thành",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Nút "Chi tiết" có biểu tượng chevron chỉ sang phải
                TextButton(
                    onClick = onDetailClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Chi tiết",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Mục "Cần ôn ngay" (Danh sách cuộn ngang chứa các thẻ ôn tập SRS)
 */
@Composable
private fun ReviewSection(
    dueCount: Int,
    decks: List<ReviewDeck>,
    onReviewClick: (ReviewDeck) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dòng tiêu đề mục và badge đếm tổng số từ cần ôn
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Cần ôn ngay",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            // Nhãn hiển thị số từ đến hạn màu đỏ nổi bật
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$dueCount từ đến hạn",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // Danh sách cuộn ngang chứa thẻ từng bộ từ ôn tập
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.width(16.dp)) // Tạo khoảng cách lề trái cho thẻ đầu
            
            // Vẽ các thẻ bộ ôn tập
            decks.forEach { deck ->
                Card(
                    modifier = Modifier
                        .width(256.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Đầu thẻ: Chứa Icon bộ học và Badge mức độ ưu tiên (Khẩn cấp / Đến hạn)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            // Hộp chứa Icon bộ từ
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (deck.isUrgent) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = deck.icon,
                                    contentDescription = null,
                                    tint = if (deck.isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            
                            // Badge mức độ
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (deck.isUrgent) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = deck.badgeText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (deck.isUrgent) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        // Thân thẻ: Tiêu đề bộ từ và mô tả số lượng từ
                        Column {
                            Text(
                                text = deck.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = deck.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Đuôi thẻ: Nút bấm bắt đầu ôn tập
                        Button(
                            onClick = { onReviewClick(deck) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Ôn ngay",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp)) // Tạo khoảng cách lề phải cho thẻ cuối
        }
    }
}

/**
 * Mục "Từ mới hôm nay" (Danh sách các thẻ học đề xuất cuộn ngang có viền đứt)
 */
@Composable
private fun NewWordsSection(
    decks: List<NewWordDeck>,
    onLearnClick: (NewWordDeck) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tiêu đề mục
        Text(
            text = "Từ mới hôm nay",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Vùng cuộn ngang các thẻ từ mới đề xuất
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            
            decks.forEach { deck ->
                Column(
                    modifier = Modifier
                        .width(192.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Biểu tượng bộ từ đặt trên vòng tròn trắng đổ bóng nhẹ
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .shadow(1.dp, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = deck.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // Tên bộ từ và số lượng từ đề xuất
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = deck.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = deck.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Nút bấm "Học ngay" dạng OutlinedButton viền xanh
                    OutlinedButton(
                        onClick = { onLearnClick(deck) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = "Học ngay",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

/**
 * Mục "Gần đây" (Danh sách dọc các bộ học đã tương tác gần nhất)
 */
@Composable
private fun RecentSection(
    decks: List<RecentDeck>,
    onSeeAllClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tiêu đề và nút "Xem tất cả"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gần đây",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            TextButton(
                onClick = onSeeAllClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Xem tất cả",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Cột dọc hiển thị các mục gần đây
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            decks.forEach { deck ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                        .clickable { /* Xử lý sự kiện bấm xem chi tiết khi cần */ }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Biểu tượng nằm trong hộp màu nền nhạt
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = deck.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Tên và thông tin học tập gần đây
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = deck.title,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = deck.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Biểu tượng 3 chấm tùy chọn ở góc phải
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "Tùy chọn",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Thanh điều hướng dưới cùng (Bottom Navigation Bar)
 */
@Composable
private fun HomeBottomBar(
    navItems: List<NavItem>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp, // Đổ bóng cao để tách biệt khỏi nội dung phía sau
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp) // Bo tròn nhẹ 2 góc trên
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding() // Tránh đè lên thanh điều hướng của hệ điều hành Android
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navItems.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (isSelected) Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                            else Modifier
                        )
                        .clickable { onItemClick(index) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
