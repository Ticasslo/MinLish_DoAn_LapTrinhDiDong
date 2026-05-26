package com.example.englishapp.features.home.presentation.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.englishapp.R
import com.example.englishapp.features.home.presentation.viewmodel.HomeViewModel

data class ReviewDeck(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val badgeText: String,
    val isUrgent: Boolean = false
)

data class NewWordDeck(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
)

data class RecentDeck(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
)

data class NavItem(
    val icon: ImageVector,
    val label: String,
)

@OptIn(ExperimentalMaterial3Api::class)
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
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user

    val userName = user?.name ?: "Người dùng"
    val avatarUrl = user?.avatar
    val streakDays = uiState.streakDays
    val wordsToday = uiState.wordsToday
    val wordGoal = uiState.wordGoal

    val progress = wordsToday.toFloat() / wordGoal.toFloat().coerceAtLeast(1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 700),
        label = "progress"
    )

    val reviewDecks = listOf(
        ReviewDeck(
            icon = Icons.Outlined.Work,
            title = "Business English",
            subtitle = "18 từ cần ôn tập lại",
            badgeText = "KHẨN CẤP",
            isUrgent = true
        ),
        ReviewDeck(
            icon = Icons.Outlined.Flight,
            title = "Travel Essentials",
            subtitle = "27 từ cần ôn tập lại",
            badgeText = "ĐẾN HẠN",
            isUrgent = false
        ),
    )

    val newWordDecks = listOf(
        NewWordDeck(Icons.Outlined.Psychology, "IELTS Vocab", "10 từ mới đề xuất"),
        NewWordDeck(Icons.Outlined.Restaurant, "Daily Life", "5 từ mới đề xuất"),
    )

    val recentDecks = listOf(
        RecentDeck(
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            title = "Oxford 3000 Keywords",
            subtitle = "Lần cuối: 2 giờ trước • 85% thuộc",
        ),
        RecentDeck(
            icon = Icons.Outlined.Movie,
            title = "Movie Phrases",
            subtitle = "Lần cuối: 1 ngày trước • 42% thuộc",
        ),
    )

    val navItems = listOf(
        NavItem(Icons.Filled.Home, stringResource(R.string.nav_home)),
        NavItem(Icons.AutoMirrored.Outlined.MenuBook, stringResource(R.string.nav_library)),
        NavItem(Icons.Outlined.School, stringResource(R.string.nav_learn)),
        NavItem(Icons.Outlined.BarChart, stringResource(R.string.nav_progress)),
        NavItem(Icons.Outlined.Person, stringResource(R.string.nav_profile)),
    )

    var selectedNav by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HomeTopBar(
                userName = userName,
                avatarUrl = avatarUrl,
                onNotificationClick = onNotificationClick,
            )
        },
        bottomBar = {
            HomeBottomBar(
                navItems = navItems,
                selectedIndex = selectedNav,
                onItemClick = { idx ->
                    selectedNav = idx
                    onNavItemClick(idx)
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(56.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Thêm từ mới", modifier = Modifier.size(30.dp))
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            TodaySummarySection(
                streakDays = streakDays,
                wordsToday = wordsToday,
                wordGoal = wordGoal,
                animatedProgress = animatedProgress,
                onDetailClick = onDetailClick,
            )

            ReviewSection(
                dueCount = 45,
                decks = reviewDecks,
                onReviewClick = onReviewClick,
            )

            NewWordsSection(
                decks = newWordDecks,
                onLearnClick = onLearnClick,
            )

            RecentSection(
                decks = recentDecks,
                onSeeAllClick = onSeeAllClick,
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    userName: String,
    avatarUrl: String?,
    onNotificationClick: () -> Unit,
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
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUrl != null) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = userName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Column {
                    Text(
                        text = "Xin chào,",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "$userName!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            IconButton(onClick = onNotificationClick) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Thông báo",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun TodaySummarySection(
    streakDays: Int,
    wordsToday: Int,
    wordGoal: Int,
    animatedProgress: Float,
    onDetailClick: () -> Unit,
) {
    val percent = (animatedProgress * 100).toInt()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "$streakDays ngày liên tiếp",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }

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
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(animatedProgress)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary),
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$percent% hoàn thành",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = onDetailClick, contentPadding = PaddingValues(0.dp)) {
                    Text(
                        text = "Chi tiết",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ReviewSection(
    dueCount: Int,
    decks: List<ReviewDeck>,
    onReviewClick: (ReviewDeck) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Cần ôn ngay",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = "$dueCount từ đến hạn",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            decks.forEach { deck ->
                ReviewDeckCard(deck = deck, onClick = { onReviewClick(deck) })
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
private fun ReviewDeckCard(deck: ReviewDeck, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(256.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (deck.isUrgent) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = deck.icon,
                        contentDescription = null,
                        tint = if (deck.isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (deck.isUrgent) MaterialTheme.colorScheme.errorContainer
                            else MaterialTheme.colorScheme.secondaryContainer
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = deck.badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (deck.isUrgent) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Column {
                Text(
                    text = deck.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = deck.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
            ) {
                Text("Ôn ngay", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun NewWordsSection(
    decks: List<NewWordDeck>,
    onLearnClick: (NewWordDeck) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Từ mới hôm nay",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Spacer(modifier = Modifier.width(16.dp))
            decks.forEach { deck ->
                NewWordDeckCard(deck = deck, onClick = { onLearnClick(deck) })
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
    }
}

@Composable
private fun NewWordDeckCard(deck: NewWordDeck, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(192.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .shadow(1.dp, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = deck.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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

        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
        ) {
            Text("Học ngay", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RecentSection(
    decks: List<RecentDeck>,
    onSeeAllClick: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Gần đây",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            TextButton(onClick = onSeeAllClick, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = "Xem tất cả",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            decks.forEach { deck -> RecentDeckItem(deck = deck) }
        }
    }
}

@Composable
private fun RecentDeckItem(deck: RecentDeck) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable { }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = deck.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = deck.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = deck.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Thêm",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun HomeBottomBar(
    navItems: List<NavItem>,
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
            navItems.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (isSelected)
                                Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                            else
                                Modifier
                        )
                        .clickable { onItemClick(index) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
