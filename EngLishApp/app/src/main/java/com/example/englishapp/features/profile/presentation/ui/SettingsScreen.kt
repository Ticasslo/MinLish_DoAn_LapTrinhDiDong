package com.example.englishapp.features.profile.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.features.profile.presentation.viewmodel.ProfileViewModel

// ─────────────────────────────────────────────
// SettingsScreen
// ─────────────────────────────────────────────
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    // ── Study settings state (đọc từ User thật) ──────────
    var dailyGoal by remember(user) { mutableFloatStateOf(user?.dailyGoal?.toFloat() ?: 20f) }
    var notificationTime by remember(user) { mutableStateOf(user?.reminderTime ?: "20:00") }
    var pushEnabled by remember(user) { mutableStateOf(user?.pushEnabled ?: true) }
    var emailEnabled by remember { mutableStateOf(false) }

    // ── Appearance & sound state (local-only, chưa có persistence) ──
    val darkMode by viewModel.isDarkMode.collectAsState()
    var autoPronounce by remember { mutableStateOf(true) }

    // ── Dialog states ──────────────────────────
    var showTimePicker by remember { mutableStateOf(false) }
    var showLogoutConfirm by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showFontSizeDialog by remember { mutableStateOf(false) }
    var showLearningGoalDialog by remember { mutableStateOf(false) }
    var showCurrentLevelDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Lắng nghe trạng thái đã lưu thành công ──
    LaunchedEffect(uiState.settingsSaved) {
        if (uiState.settingsSaved) {
            snackbarHostState.showSnackbar("Đã lưu cài đặt thành công!")
            viewModel.resetSettingsSavedState()
        }
    }

    // ── Lắng nghe trạng thái lỗi ──
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar("Lỗi: $it")
        }
    }

    // ── Lắng nghe trạng thái đăng xuất ──
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogoutClick()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { SettingsTopBar(onBackClick = onBackClick) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Spacer(Modifier.height(4.dp))

            // ── Cài đặt học tập ───────────────────────
            SettingsSectionHeader("Cài đặt học tập")
            StudySettingsCard(
                dailyGoal = dailyGoal,
                onGoalChange = { dailyGoal = it },
                onGoalChangeFinished = {
                    // Lưu ngay khi người dùng thả tay khỏi slider
                    viewModel.updateSettings(dailyGoal.toInt(), notificationTime, pushEnabled)
                },
                notificationTime = notificationTime,
                onNotificationTimeClick = { showTimePicker = true },
                pushEnabled = pushEnabled,
                onPushToggle = { newValue ->
                    pushEnabled = newValue
                    viewModel.updateSettings(dailyGoal.toInt(), notificationTime, newValue)
                },
                emailEnabled = emailEnabled,
                onEmailToggle = { emailEnabled = it },
            )

            // ── Giao diện & Âm thanh ──────────────────
            SettingsSectionHeader("Giao diện & Âm thanh")
            AppearanceSoundCard(
                darkMode = darkMode,
                onDarkModeToggle = { viewModel.setDarkMode(it) },
                onLanguageClick = { showLanguageDialog = true },
                autoPronounce = autoPronounce,
                onAutoPronounceToggle = { autoPronounce = it },
                onFontSizeClick = { showFontSizeDialog = true },
            )

            // ── Tài khoản ─────────────────────────────
            SettingsSectionHeader("Tài khoản")
            AccountCard(
                onChangePasswordClick = onChangePasswordClick,
                onLearningGoalClick = { showLearningGoalDialog = true },
                onCurrentLevelClick = { showCurrentLevelDialog = true },
                onExportDataClick = { showExportDialog = true },
            )

            // ── Logout ────────────────────────────────
            LogoutButton2(onClick = { showLogoutConfirm = true })
        }
    }

    // ═════════════════════════════════════════════
    // DIALOGS
    // ═════════════════════════════════════════════

    // 1. Dialog chọn giờ thông báo
    if (showTimePicker) {
        TimePickerDialog(
            currentTime = notificationTime,
            onDismiss = { showTimePicker = false },
            onConfirm = { hour, minute ->
                val newTime = String.format("%02d:%02d", hour, minute)
                notificationTime = newTime
                showTimePicker = false
                viewModel.updateSettings(dailyGoal.toInt(), newTime, pushEnabled)
            }
        )
    }

    // 2. Dialog xác nhận đăng xuất
    if (showLogoutConfirm) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirm = false },
            icon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
            title = { Text("Xác nhận đăng xuất", fontWeight = FontWeight.Bold) },
            text = { Text("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản? Dữ liệu học tập của bạn vẫn được lưu trữ an toàn.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirm = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Đăng xuất", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirm = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // 3. Dialog chọn ngôn ngữ
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            icon = { Icon(Icons.Outlined.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Ngôn ngữ ứng dụng", fontWeight = FontWeight.Bold) },
            text = { Text("Hiện tại ứng dụng chỉ hỗ trợ Tiếng Việt. Các ngôn ngữ khác sẽ được cập nhật trong phiên bản sau.") },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text("Đồng ý")
                }
            }
        )
    }

    // 4. Dialog cỡ chữ
    if (showFontSizeDialog) {
        AlertDialog(
            onDismissRequest = { showFontSizeDialog = false },
            icon = { Icon(Icons.Outlined.TextFields, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Cỡ chữ", fontWeight = FontWeight.Bold) },
            text = { Text("Tính năng tùy chỉnh cỡ chữ sẽ được cập nhật trong phiên bản tiếp theo.") },
            confirmButton = {
                TextButton(onClick = { showFontSizeDialog = false }) {
                    Text("Đồng ý")
                }
            }
        )
    }

    // 5. Dialog mục tiêu học
    if (showLearningGoalDialog) {
        val goalOptions = listOf("IELTS", "TOEIC", "Business", "Travel", "Communication")
        val currentGoal = user?.goal ?: ""
        var selectedGoal by remember(currentGoal) { mutableStateOf(currentGoal) }

        AlertDialog(
            onDismissRequest = { showLearningGoalDialog = false },
            icon = { Icon(Icons.Outlined.Flag, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Mục tiêu học tập", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    goalOptions.forEach { goal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedGoal = goal }
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = selectedGoal == goal,
                                onClick = { selectedGoal = goal }
                            )
                            Text(goal, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLearningGoalDialog = false }) {
                    Text("Đồng ý")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLearningGoalDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // 6. Dialog trình độ hiện tại
    if (showCurrentLevelDialog) {
        val levelOptions = listOf("A1", "A2", "B1", "B2", "C1", "C2")
        val currentLevel = user?.level ?: ""
        var selectedLevel by remember(currentLevel) { mutableStateOf(currentLevel) }

        AlertDialog(
            onDismissRequest = { showCurrentLevelDialog = false },
            icon = { Icon(Icons.AutoMirrored.Outlined.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Trình độ hiện tại", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    levelOptions.forEach { level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedLevel = level }
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            RadioButton(
                                selected = selectedLevel == level,
                                onClick = { selectedLevel = level }
                            )
                            Text(level, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrentLevelDialog = false }) {
                    Text("Đồng ý")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCurrentLevelDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }

    // 7. Dialog xuất dữ liệu
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            icon = { Icon(Icons.Outlined.Download, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text("Xuất dữ liệu", fontWeight = FontWeight.Bold) },
            text = { Text("Tính năng xuất dữ liệu học tập (CSV/PDF) sẽ được cập nhật trong phiên bản tiếp theo.") },
            confirmButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Đồng ý")
                }
            }
        )
    }
}

// ─────────────────────────────────────────────
// Time Picker Dialog (giả lập chọn giờ/phút)
// ─────────────────────────────────────────────
@Composable
private fun TimePickerDialog(
    currentTime: String,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val parts = currentTime.split(":")
    var hour by remember { mutableIntStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 20) }
    var minute by remember { mutableIntStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
        title = { Text("Chọn giờ thông báo", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hiển thị giờ đang chọn
                Text(
                    text = String.format("%02d:%02d", hour, minute),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                // Slider chọn giờ (0-23)
                Column {
                    Text("Giờ: $hour", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    Slider(
                        value = hour.toFloat(),
                        onValueChange = { hour = it.toInt() },
                        valueRange = 0f..23f,
                        steps = 22,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    )
                }

                // Slider chọn phút (0-55, bước nhảy 5)
                Column {
                    Text("Phút: ${String.format("%02d", minute)}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    Slider(
                        value = minute.toFloat(),
                        onValueChange = { minute = (it.toInt() / 5) * 5 },
                        valueRange = 0f..55f,
                        steps = 10,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(hour, minute) }) {
                Text("Xác nhận", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}

// ─────────────────────────────────────────────
// TopAppBar
// ─────────────────────────────────────────────
@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Quay lại", tint = MaterialTheme.colorScheme.primary)
                }
            }
            Text(
                text = "Cài đặt",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp),
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = (-0.3).sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

// ─────────────────────────────────────────────
// Section Header
// ─────────────────────────────────────────────
@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontSize = 18.sp),
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp),
    )
}

// ─────────────────────────────────────────────
// Study Settings Card
// ─────────────────────────────────────────────
@Composable
private fun StudySettingsCard(
    dailyGoal: Float,
    onGoalChange: (Float) -> Unit,
    onGoalChangeFinished: () -> Unit,
    notificationTime: String,
    onNotificationTimeClick: () -> Unit,
    pushEnabled: Boolean,
    onPushToggle: (Boolean) -> Unit,
    emailEnabled: Boolean,
    onEmailToggle: (Boolean) -> Unit,
) {
    SettingsCard {
        // Daily goal slider
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Mục tiêu từ mới mỗi ngày",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(
                        "${dailyGoal.toInt()} từ",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text("5", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Slider(
                    value = dailyGoal,
                    onValueChange = onGoalChange,
                    onValueChangeFinished = onGoalChangeFinished,
                    valueRange = 5f..50f,
                    steps = 44,
                    modifier = Modifier.weight(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                )
                Text("50", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        SettingsDivider()

        // Notification time
        SettingsClickRow(
            icon = Icons.Outlined.Schedule,
            iconTint = MaterialTheme.colorScheme.primary,
            label = "Thời gian thông báo",
            trailingText = notificationTime,
            onClick = onNotificationTimeClick,
        )

        SettingsDivider()

        // Push notifications
        SettingsSwitchRow2(
            icon = Icons.Outlined.NotificationsActive,
            iconTint = MaterialTheme.colorScheme.primary,
            label = "Thông báo đẩy (Push)",
            checked = pushEnabled,
            onCheckedChange = onPushToggle,
        )

        SettingsDivider()

        // Email notifications
        SettingsSwitchRow2(
            icon = Icons.Outlined.Mail,
            iconTint = MaterialTheme.colorScheme.outline,
            label = "Thông báo qua Email",
            checked = emailEnabled,
            onCheckedChange = onEmailToggle,
        )
    }
}

// ─────────────────────────────────────────────
// Appearance & Sound Card
// ─────────────────────────────────────────────
@Composable
private fun AppearanceSoundCard(
    darkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onLanguageClick: () -> Unit,
    autoPronounce: Boolean,
    onAutoPronounceToggle: (Boolean) -> Unit,
    onFontSizeClick: () -> Unit,
) {
    SettingsCard {
        SettingsSwitchRow2(
            icon = Icons.Outlined.DarkMode,
            iconTint = MaterialTheme.colorScheme.outline,
            label = "Chế độ tối",
            checked = darkMode,
            onCheckedChange = onDarkModeToggle,
        )

        SettingsDivider()

        SettingsClickRow(
            icon = Icons.Outlined.Language,
            iconTint = MaterialTheme.colorScheme.outline,
            label = "Ngôn ngữ ứng dụng",
            trailingText = "Tiếng Việt",
            onClick = onLanguageClick,
        )

        SettingsDivider()

        SettingsSwitchRow2(
            icon = Icons.AutoMirrored.Outlined.VolumeUp,
            iconTint = MaterialTheme.colorScheme.outline,
            label = "Tự động phát âm",
            checked = autoPronounce,
            onCheckedChange = onAutoPronounceToggle,
        )

        SettingsDivider()

        SettingsClickRow(
            icon = Icons.Outlined.TextFields,
            iconTint = MaterialTheme.colorScheme.outline,
            label = "Cỡ chữ",
            trailingText = "Mặc định",
            onClick = onFontSizeClick,
        )
    }
}

// ─────────────────────────────────────────────
// Account Card
// ─────────────────────────────────────────────
@Composable
private fun AccountCard(
    onChangePasswordClick: () -> Unit,
    onLearningGoalClick: () -> Unit,
    onCurrentLevelClick: () -> Unit,
    onExportDataClick: () -> Unit,
) {
    SettingsCard {
        SettingsNavRow(icon = Icons.Outlined.Lock, label = "Đổi mật khẩu", onClick = onChangePasswordClick)
        SettingsDivider()
        SettingsNavRow(icon = Icons.Outlined.Flag, label = "Mục tiêu học", onClick = onLearningGoalClick)
        SettingsDivider()
        SettingsNavRow(icon = Icons.AutoMirrored.Outlined.TrendingUp, label = "Trình độ hiện tại", onClick = onCurrentLevelClick)
        SettingsDivider()
        SettingsNavRow(icon = Icons.Outlined.Download, label = "Xuất dữ liệu", onClick = onExportDataClick, isLast = true)
    }
}

// ─────────────────────────────────────────────
// Logout Button
// ─────────────────────────────────────────────
@Composable
private fun LogoutButton2(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.error),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
    ) {
        Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null, tint = MaterialTheme.colorScheme.error)
        Spacer(Modifier.width(8.dp))
        Text(
            text = "Đăng xuất",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

// ─────────────────────────────────────────────
// Reusable row components
// ─────────────────────────────────────────────

/** Card wrapper với bo góc + border */
@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(content = content)
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

/** Row với switch toggle */
@Composable
private fun SettingsSwitchRow2(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
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

/** Row dạng click với trailing text + chevron */
@Composable
private fun SettingsClickRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    trailingText: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(trailingText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
        }
    }
}

/** Row dạng navigate (icon + label + chevron) */
@Composable
private fun SettingsNavRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isLast: Boolean = false,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(22.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(20.dp))
    }
}
