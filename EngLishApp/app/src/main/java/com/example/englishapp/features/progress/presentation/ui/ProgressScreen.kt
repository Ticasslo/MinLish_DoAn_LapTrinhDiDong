package com.example.englishapp.features.progress.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.R
import com.example.englishapp.core.ui.components.MainBottomBar
import com.example.englishapp.core.ui.components.NavItem
import com.example.englishapp.core.ui.theme.*
import com.example.englishapp.features.progress.domain.model.DailyActivity
import com.example.englishapp.features.progress.domain.model.SetRetention
import com.example.englishapp.features.progress.presentation.viewmodel.ProgressViewModel
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onNavItemClick: (Int) -> Unit = {},
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Danh sách nút điều hướng đồng bộ với HomeScreen và ProfileScreen
    val navItems = listOf(
        NavItem(Icons.Outlined.Home, stringResource(R.string.nav_home)),
        NavItem(Icons.AutoMirrored.Outlined.MenuBook, stringResource(R.string.nav_library)),
        NavItem(Icons.Outlined.BarChart, stringResource(R.string.nav_progress)),
        NavItem(Icons.Outlined.Person, stringResource(R.string.nav_profile))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tiến độ của bạn",
                        style = Typography.headlineMedium.copy(fontSize = 20.sp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            MainBottomBar(
                navItems = navItems,
                selectedIndex = 2, // Vị trí Tiến độ là index 2 trong danh sách 4 item
                onItemClick = onNavItemClick
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedCorner(32.dp)
        ) {
            Spacer(modifier = Modifier.height(1.dp))

            // Level Section
            LevelCard(
                level = uiState.stats.level,
                progress = uiState.stats.levelProgress
            )

            // Overview Grid
            OverviewGrid(
                streak = uiState.stats.streak,
                vocabulary = uiState.stats.totalWords,
                accuracy = uiState.stats.accuracy
            )

            // 7-Day Activity Chart
            ActivityChart(activities = uiState.weeklyActivity)

            // Word Status Pie Chart
            WordStatusSection(
                total = uiState.wordStatus.total,
                mastered = uiState.wordStatus.mastered,
                learning = uiState.wordStatus.learning,
                new = uiState.wordStatus.new
            )

            // Retention Rate List
            RetentionRateSection(retentions = uiState.retentionRates)
        }
    }
}

// Helper for spaced arrangement in Column (Custom if not available in standard)
@Composable
fun Arrangement.spacedCorner(space: androidx.compose.ui.unit.Dp): Arrangement.Vertical = Arrangement.spacedBy(space)

@Composable
fun LevelCard(level: String, progress: Float) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardLight),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.padding(24.dp)) {
            // Background Icon decoration (simulated)
            Icon(
                imageVector = Icons.Default.MilitaryTech,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-20).dp),
                tint = PrimaryLight.copy(alpha = 0.1f)
            )

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(PrimaryContainerLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            tint = OnPrimaryContainerLight,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Cấp độ hiện tại",
                            style = Typography.labelSmall,
                            color = ColorSubtleText
                        )
                        Text(
                            text = level,
                            style = Typography.headlineLarge,
                            color = PrimaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Lộ trình A1 - C2",
                        style = Typography.bodyLarge,
                        color = OnSurfaceVariantLight
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryLight
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),
                    color = PrimaryLight,
                    trackColor = SurfaceVariantLight,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("A1", "A2", "B1", "B2", "C1", "C2").forEach { label ->
                        Text(
                            text = label,
                            style = Typography.labelSmall,
                            color = if (label == "B1") PrimaryLight else ColorSubtleText,
                            fontWeight = if (label == "B1") FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewGrid(streak: Int, vocabulary: Int, accuracy: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.LocalFireDepartment,
            iconColor = ColorWarning,
            label = "Chuỗi",
            value = streak.toString()
        )
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.MenuBook,
            iconColor = PrimaryLight,
            label = "Từ vựng",
            value = vocabulary.toString()
        )
        StatItem(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Verified,
            iconColor = ColorSuccess,
            label = "Chính xác",
            value = "$accuracy%"
        )
    }
}

@Composable
fun StatItem(modifier: Modifier, icon: ImageVector, iconColor: Color, label: String, value: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardLight),
        border = BorderStroke(1.dp, ColorDivider)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
            Text(text = label, style = Typography.labelSmall, color = ColorSubtleText)
            Text(text = value, style = Typography.headlineSmall, color = OnSurfaceLight)
        }
    }
}

@Composable
fun ActivityChart(activities: List<DailyActivity>) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Hoạt động 7 ngày", style = Typography.headlineSmall)
            Text(
                text = "Tháng 10", // Should be dynamic
                style = Typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = PrimaryLight
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = ColorCardLight),
            border = BorderStroke(1.dp, ColorDivider)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                if (activities.isEmpty()) {
                    // Fallback or empty state
                    Text(
                        "Không có dữ liệu",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    activities.forEach { activity ->
                        ActivityBar(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp),
                            activity = activity
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityBar(modifier: Modifier, activity: DailyActivity) {
    val animatedHeight by animateFloatAsState(
        targetValue = activity.activityLevel,
        animationSpec = tween(durationMillis = 1000),
        label = "bar_height"
    )

    Column(
        modifier = modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(animatedHeight.coerceAtLeast(0.05f))
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(if (activity.isToday) PrimaryLight else SurfaceVariantLight)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = activity.dayName,
            style = Typography.labelSmall.copy(
                fontWeight = if (activity.isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (activity.isToday) OnSurfaceLight else ColorSubtleText
            )
        )
    }
}

@Composable
fun WordStatusSection(total: Int, mastered: Int, learning: Int, new: Int) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(text = "Trạng thái từ vựng", style = Typography.headlineSmall)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = ColorCardLight),
            border = BorderStroke(1.dp, ColorDivider)
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(modifier = Modifier.size(128.dp), contentAlignment = Alignment.Center) {
                    DonutChart(
                        mastered = mastered.toFloat() / total.coerceAtLeast(1),
                        learning = learning.toFloat() / total.coerceAtLeast(1)
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = total.toString(), style = Typography.headlineSmall)
                        Text(text = "Tổng", style = Typography.labelSmall, color = ColorSubtleText)
                    }
                }

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusRow(color = ColorSuccess, label = "Thành thạo", count = mastered)
                    StatusRow(color = PrimaryLight, label = "Đang học", count = learning)
                    StatusRow(color = SurfaceVariantLight, label = "Từ mới", count = new)
                }
            }
        }
    }
}

@Composable
fun DonutChart(mastered: Float, learning: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 16.dp.toPx()
        // Background track
        drawCircle(
            color = SurfaceVariantLight,
            style = Stroke(width = strokeWidth)
        )
        // Mastered segment
        drawArc(
            color = ColorSuccess,
            startAngle = -90f,
            sweepAngle = mastered * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
        // Learning segment
        drawArc(
            color = PrimaryLight,
            startAngle = -90f + (mastered * 360f),
            sweepAngle = learning * 360f,
            useCenter = false,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun StatusRow(color: Color, label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(12.dp).clip(CircleShape).background(color))
            Text(text = label, style = Typography.bodyLarge, color = OnSurfaceVariantLight)
        }
        Text(text = count.toString(), style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun RetentionRateSection(retentions: List<SetRetention>) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Text(text = "Tỷ lệ ghi nhớ theo bộ thẻ", style = Typography.headlineSmall)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            retentions.forEach { retention ->
                RetentionItem(retention = retention)
            }
        }
    }
}

@Composable
fun RetentionItem(retention: SetRetention) {
    val icon = when (retention.iconType) {
        "business" -> Icons.Default.BusinessCenter
        "travel" -> Icons.Default.FlightTakeoff
        else -> Icons.Default.Psychology
    }
    
    val tintColor = when (retention.iconType) {
        "business" -> PrimaryLight
        "travel" -> TertiaryLight
        else -> SecondaryLight
    }

    val rateColor = when {
        retention.retentionRate >= 80 -> ColorSuccess
        retention.retentionRate >= 50 -> ColorWarning
        else -> ColorError
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ColorCardLight),
        border = BorderStroke(1.dp, ColorDivider)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = tintColor)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(text = retention.setName, style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                    Text(
                        text = "${retention.retentionRate}%",
                        style = Typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = rateColor
                    )
                }
                LinearProgressIndicator(
                    progress = { retention.retentionRate / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                    color = rateColor,
                    trackColor = ColorDivider,
                    strokeCap = StrokeCap.Round
                )
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun ProgressScreenPreview() {
    EngLishAppTheme {
        ProgressScreen()
    }
}
