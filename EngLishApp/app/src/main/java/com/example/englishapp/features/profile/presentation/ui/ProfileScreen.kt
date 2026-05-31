package com.example.englishapp.features.profile.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.englishapp.R
import com.example.englishapp.core.ui.components.MainBottomBar
import com.example.englishapp.core.ui.components.NavItem
import com.example.englishapp.features.profile.presentation.viewmodel.ProfileViewModel

// =============================================================================
// LỚP DỮ LIỆU ĐỊNH NGHĨA ITEM CHO MENU TÀI KHOẢN
// =============================================================================

// Định nghĩa cấu trúc của một dòng tùy chọn trong mục Tài khoản
data class AccountMenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit = {}
)

// =============================================================================
// MÀN HÌNH HỒ SƠ CHÍNH (PROFILESCREEN COMPOSE)
// =============================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavItemClick: (Int) -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    // 1. Lấy trạng thái UI từ ViewModel bằng collectAsState
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    // Khai báo launcher để chọn ảnh đại diện từ thư viện
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                viewModel.updateAvatar(it.toString())
            }
        }
    )

    // 2. Định nghĩa các biến hiển thị thông tin cơ bản của người dùng
    val userName = user?.name ?: "Người dùng"
    val userEmail = user?.email ?: ""
    val userLevel = "Level ${user?.level ?: "Mới"}"
    val userGoal = "Mục tiêu: ${user?.goal ?: "Chưa đặt"}"
    val avatarUrl = user?.avatar

    // 3. Sử dụng remember để lưu trạng thái cục bộ cho các mục thiết lập (nhờ đó UI sẽ tự thay đổi khi cập nhật)
    // Thanh trượt chọn số từ học mỗi ngày
    var dailyGoal by remember(user) { mutableFloatStateOf(user?.dailyGoal?.toFloat() ?: 20f) }
    // Giờ thông báo học tập hằng ngày
    var notificationTime by remember(user) { mutableStateOf(user?.reminderTime ?: "20:00") }
    // Bật/tắt thông báo đẩy trên điện thoại
    var pushEnabled by remember(user) { mutableStateOf(user?.pushEnabled ?: true) }
    // Bật/tắt thông báo qua email gửi về hộp thư
    var emailEnabled by remember { mutableStateOf(false) }
    // Trạng thái hiển thị hộp thoại chọn giờ thông báo
    var showTimePicker by remember { mutableStateOf(false) }

    // 4. Lắng nghe trạng thái đăng xuất thành công để chuyển màn hình đăng nhập
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    // 5. Chuẩn bị danh sách các nút điều hướng ở thanh Bottom Bar
    val navItems = listOf(
        NavItem(Icons.Outlined.Home, stringResource(R.string.nav_home)),
        NavItem(Icons.AutoMirrored.Outlined.MenuBook, stringResource(R.string.nav_library)),
        NavItem(Icons.Outlined.BarChart, stringResource(R.string.nav_progress)),
        NavItem(Icons.Outlined.Person, stringResource(R.string.nav_profile))
    )

    // Chuẩn bị các nút tính năng trong phần Cài đặt Tài khoản
    val accountItems = listOf(
        AccountMenuItem(Icons.Outlined.Lock, "Đổi mật khẩu", onClick = onSettingsClick),
        AccountMenuItem(Icons.Outlined.Flag, "Mục tiêu học tập"),
        AccountMenuItem(Icons.Outlined.Star, "Trình độ hiện tại"),
        AccountMenuItem(Icons.Outlined.Download, "Xuất dữ liệu"),
        AccountMenuItem(Icons.Outlined.Science, "Tạo dữ liệu Test", onClick = { viewModel.generateTestData() })
    )

    // 6. Dựng bố cục màn hình Profile bằng Scaffold
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Hàm vẽ thanh tiêu đề trên cùng
            ProfileTopBar(
                onMenuClick = onMenuClick,
                onSettingsClick = onSettingsClick
            )
        },
        bottomBar = {
            // Hàm vẽ thanh điều hướng dưới cùng (với tab Hồ sơ được chọn sẵn ở vị trí số 4)
            MainBottomBar(
                navItems = navItems,
                selectedIndex = 3,
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Nội dung chính cuộn dọc bằng Column và verticalScroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()) // Bật tính năng cuộn
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Khu vực 1: Hiển thị Avatar, Tên, Email và các Chip cấp độ/mục tiêu
                UserInfoSection(
                    userName = userName,
                    userEmail = userEmail,
                    userLevel = userLevel,
                    userGoal = userGoal,
                    avatarUrl = avatarUrl,
                    onEditAvatarClick = { 
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Khu vực 2: Khối cấu hình thiết lập học tập (số từ/ngày, thời gian nhắc nhở)
                StudySettingsSection(
                    dailyGoal = dailyGoal,
                    onGoalChange = { dailyGoal = it },
                    notificationTime = notificationTime,
                    onTimeClick = { showTimePicker = true },
                    pushEnabled = pushEnabled,
                    onPushToggle = { pushEnabled = it },
                    emailEnabled = emailEnabled,
                    onEmailToggle = { emailEnabled = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Khu vực 3: Khối chứa các tùy chọn thông tin tài khoản (như đổi mật khẩu)
                AccountSection(items = accountItems)

                Spacer(modifier = Modifier.height(16.dp))

                // Khu vực 4: Nút màu đỏ thực hiện Đăng xuất tài khoản
                LogoutButton(onClick = { viewModel.logout() })
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f))
                        .clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    // 7. Hiển thị hộp thoại chọn giờ thông báo (TimePicker giả lập cho người mới học dễ tiếp cận)
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text(text = "Chọn giờ thông báo") },
            text = { Text(text = "Tính năng chọn giờ học hằng ngày sẽ được tích hợp thông qua TimePickerDialog của Android.") },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(text = "Đồng ý")
                }
            }
        )
    }
}

// =============================================================================
// CÁC HÀM COMPOSE THÀNH PHẦN CHI TIẾT (ĐƠN GIẢN HÓA & GHI CHÚ RÕ RÀNG)
// =============================================================================

/**
 * Vẽ thanh tiêu đề trên cùng (TopBar) cho màn hình Hồ sơ
 */
@Composable
private fun ProfileTopBar(
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp // Đổ bóng nhẹ 2dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding() // Tránh đè lên thanh trạng thái điện thoại
                .height(64.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nút Menu và Tên App bên trái
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "MinLish",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp
                )
            }

            // Nhãn "Hồ sơ" và nút cài đặt bên phải
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hồ sơ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 4.dp)
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Cài đặt",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Khu vực hiển thị thông tin cá nhân (Avatar tròn, Tên, Email, Trình độ, Mục tiêu)
 */
@Composable
private fun UserInfoSection(
    userName: String,
    userEmail: String,
    userLevel: String,
    userGoal: String,
    avatarUrl: String?,
    onEditAvatarClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hộp chứa ảnh đại diện hình tròn và biểu tượng bút chì để chỉnh sửa
        Box(contentAlignment = Alignment.BottomEnd) {
            if (avatarUrl != null) {
                // Tải ảnh từ Internet bằng AsyncImage của thư viện Coil
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Ảnh đại diện",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                )
            } else {
                // Nếu chưa có ảnh, hiển thị vòng tròn màu chứa chữ cái đầu tiên của tên người dùng
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(4.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.take(1).uppercase(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Biểu tượng nút chỉnh sửa nhỏ (hình bút chì) đặt đè lên góc dưới avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onEditAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Sửa ảnh đại diện",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hiển thị tên người dùng (In đậm)
        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Hiển thị hòm thư email người dùng
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Dòng chứa 2 Chip trạng thái: Trình độ hiện tại và mục tiêu học tập
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Chip trình độ (Ví dụ: Level B1)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = userLevel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }

            // Chip mục tiêu học tập (Ví dụ: Mục tiêu IELTS)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = userGoal,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Khối Thẻ Thiết lập Học tập (Slider số từ, Nút chọn thời gian nhắc nhở, Bật tắt thông báo)
 */
@Composable
private fun StudySettingsSection(
    dailyGoal: Float,
    onGoalChange: (Float) -> Unit,
    notificationTime: String,
    onTimeClick: () -> Unit,
    pushEnabled: Boolean,
    onPushToggle: (Boolean) -> Unit,
    emailEnabled: Boolean,
    onEmailToggle: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp), // Bo góc 12dp chuẩn
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Tiêu đề khối cài đặt
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoStories,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Thiết lập học tập",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Dòng hiển thị mục tiêu số từ
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mục tiêu từ mới mỗi ngày",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${dailyGoal.toInt()} từ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Thanh trượt Slider thay đổi số từ mục tiêu học từ 5 tới 50 từ
            Slider(
                value = dailyGoal,
                onValueChange = onGoalChange,
                valueRange = 5f..50f,
                steps = 44, // 44 bước tương ứng nhảy từng đơn vị
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Dòng lựa chọn Thời gian thông báo học
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Thời gian thông báo",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                // Khối nút bấm chọn giờ
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .clickable { onTimeClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = notificationTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // Dòng Switch: Bật tắt thông báo Push trên màn hình khoá
            SettingsSwitchRow(
                label = "Thông báo đẩy (Push)",
                checked = pushEnabled,
                onCheckedChange = onPushToggle
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dòng Switch: Bật tắt nhận email nhắc học hằng tuần/ngày
            SettingsSwitchRow(
                label = "Thông báo qua Email",
                checked = emailEnabled,
                onCheckedChange = onEmailToggle
            )
        }
    }
}

/**
 * Hàm vẽ dòng công tắc Switch bật/tắt cài đặt
 */
@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

/**
 * Khối các tuỳ chọn cài đặt của Tài khoản (Đổi mật khẩu, Trình độ, v.v.)
 */
@Composable
private fun AccountSection(items: List<AccountMenuItem>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            // Header đề mục nhỏ "Tài khoản"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Tài khoản",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            // Duyệt danh sách để vẽ từng dòng tuỳ chọn một
            items.forEachIndexed { index, item ->
                AccountMenuRow(item = item)
                
                // Thêm đường gạch phân tách ngang nếu không phải là phần tử cuối
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Một dòng tùy chọn chứa biểu tượng, chữ và dấu chevron chỉ sang phải
 */
@Composable
private fun AccountMenuRow(item: AccountMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = item.label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        
        // Chevron trỏ sang phải báo hiệu nút bấm chuyển hướng
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Nút Đăng xuất màu đỏ ở cuối màn hình
 */
@Composable
private fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.Logout, // Dùng phiên bản hỗ trợ xoay tự động chuẩn
            contentDescription = "Đăng xuất",
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Đăng xuất",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}


