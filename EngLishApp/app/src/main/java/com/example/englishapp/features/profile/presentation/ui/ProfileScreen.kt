package com.example.englishapp.features.profile.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.filled.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.englishapp.R
import com.example.englishapp.features.profile.presentation.viewmodel.ProfileViewModel

data class AccountMenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavItemClick: (Int) -> Unit = {},
    onMenuClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onEditAvatarClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    val userName = user?.name ?: "Người dùng"
    val userEmail = user?.email ?: ""
    val userLevel = "Level ${user?.level ?: "Mới"}"
    val userGoal = "Mục tiêu: ${user?.goal ?: "Chưa đặt"}"
    val avatarUrl = user?.avatar

    var dailyGoal by remember(user) { mutableFloatStateOf(user?.dailyGoal?.toFloat() ?: 20f) }
    var notificationTime by remember(user) { mutableStateOf(user?.reminderTime ?: "20:00") }
    var pushEnabled by remember(user) { mutableStateOf(user?.pushEnabled ?: true) }
    var emailEnabled by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    val navItems = listOf(
        Icons.Outlined.Home to stringResource(R.string.nav_home),
        Icons.AutoMirrored.Outlined.MenuBook to stringResource(R.string.nav_library),
        Icons.Outlined.School to stringResource(R.string.nav_learn),
        Icons.Outlined.BarChart to stringResource(R.string.nav_progress),
        Icons.Filled.Person to stringResource(R.string.nav_profile),
    )

    val accountItems = listOf(
        AccountMenuItem(Icons.Outlined.Lock, "Đổi mật khẩu", onClick = onSettingsClick),
        AccountMenuItem(Icons.Outlined.Flag, "Mục tiêu học tập"),
        AccountMenuItem(Icons.Outlined.Star, "Trình độ hiện tại"),
        AccountMenuItem(Icons.Outlined.Download, "Xuất dữ liệu"),
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ProfileTopBar(
                onMenuClick = onMenuClick,
                onSettingsClick = onSettingsClick,
            )
        },
        bottomBar = {
            ProfileBottomBar(
                navItems = navItems,
                selectedIndex = 4,
                onItemClick = onNavItemClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp),
        ) {
            Spacer(Modifier.height(24.dp))

            UserInfoSection(
                userName = userName,
                userEmail = userEmail,
                userLevel = userLevel,
                userGoal = userGoal,
                avatarUrl = avatarUrl,
                onEditAvatarClick = onEditAvatarClick,
            )

            Spacer(Modifier.height(32.dp))

            StudySettingsSection(
                dailyGoal = dailyGoal,
                onGoalChange = { dailyGoal = it },
                notificationTime = notificationTime,
                onTimeClick = { showTimePicker = true },
                pushEnabled = pushEnabled,
                onPushToggle = { pushEnabled = it },
                emailEnabled = emailEnabled,
                onEmailToggle = { emailEnabled = it },
            )

            Spacer(Modifier.height(24.dp))

            AccountSection(items = accountItems)

            Spacer(Modifier.height(16.dp))

            LogoutButton(onClick = { viewModel.logout() })
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Chọn giờ thông báo") },
            text = { Text("Tính năng chọn giờ sẽ được tích hợp với TimePickerDialog.") },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("OK") }
            },
        )
    }
}

@Composable
private fun ProfileTopBar(
    onMenuClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        Icons.Outlined.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "MinLish",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = (-0.5).sp,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Hồ sơ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 4.dp),
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        Icons.Outlined.Settings,
                        contentDescription = "Cài đặt",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun UserInfoSection(
    userName: String,
    userEmail: String,
    userLevel: String,
    userGoal: String,
    avatarUrl: String?,
    onEditAvatarClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            if (avatarUrl != null) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .border(4.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape),
                )
            } else {
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
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onEditAvatarClick() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Sửa avatar",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(16.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = userEmail,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    Icons.AutoMirrored.Outlined.TrendingUp,
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

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(
                    Icons.Outlined.EmojiEvents,
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

@Composable
private fun StudySettingsSection(
    dailyGoal: Float,
    onGoalChange: (Float) -> Unit,
    notificationTime: String,
    onTimeClick: () -> Unit,
    pushEnabled: Boolean,
    onPushToggle: (Boolean) -> Unit,
    emailEnabled: Boolean,
    onEmailToggle: (Boolean) -> Unit,
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    Icons.Outlined.AutoStories,
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

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
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
            Slider(
                value = dailyGoal,
                onValueChange = onGoalChange,
                valueRange = 5f..50f,
                steps = 44,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            )

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Icon(
                        Icons.Outlined.Schedule,
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
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                        .clickable { onTimeClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = notificationTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(16.dp))

            SettingsSwitchRow(
                label = "Thông báo đẩy (Push)",
                checked = pushEnabled,
                onCheckedChange = onPushToggle,
            )

            Spacer(Modifier.height(16.dp))

            SettingsSwitchRow(
                label = "Thông báo qua Email",
                checked = emailEnabled,
                onCheckedChange = onEmailToggle,
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
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
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
    }
}

@Composable
private fun AccountSection(items: List<AccountMenuItem>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
            ) {
                Text(
                    text = "Tài khoản",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

            items.forEachIndexed { index, item ->
                AccountMenuRow(item = item)
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

@Composable
private fun AccountMenuRow(item: AccountMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
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
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.error),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
    ) {
        Icon(
            Icons.Outlined.Logout,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Đăng xuất",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun ProfileBottomBar(
    navItems: List<Pair<ImageVector, String>>,
    selectedIndex: Int,
    onItemClick: (Int) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            navItems.forEachIndexed { index, (icon, label) ->
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
