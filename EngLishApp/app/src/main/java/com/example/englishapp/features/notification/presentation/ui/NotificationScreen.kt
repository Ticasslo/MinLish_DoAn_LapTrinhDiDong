package com.example.englishapp.features.notification.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.features.notification.domain.model.Notification
import com.example.englishapp.features.notification.domain.model.NotificationType
import com.example.englishapp.features.notification.presentation.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    onActionClick: (String?) -> Unit = {},
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.markAllAsRead() }) {
                        Text("Đã đọc tất cả", color = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (notifications.isEmpty()) {
            EmptyNotificationView()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onClick = { viewModel.markAsRead(notification.id) },
                        onActionClick = { onActionClick(notification.actionData) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit,
    onActionClick: () -> Unit
) {
    // Nếu đã đọc thì màu nhạt hẳn đi
    val contentAlpha = if (notification.isRead) 0.6f else 1.0f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon với chấm (Unread)
            Box {
                NotificationIcon(notification.type)
                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }

            // Nội dung chính
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = formatTimestamp(notification.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (notification.type == NotificationType.REVIEW_REMINDER && !notification.isRead) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onActionClick,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Học ngay", fontSize = 14.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}

@Composable
fun NotificationIcon(type: NotificationType) {
    val (icon, color) = when (type) {
        NotificationType.REVIEW_REMINDER -> Icons.Default.NotificationsActive to MaterialTheme.colorScheme.primary
        NotificationType.SYSTEM -> Icons.Default.Notifications to MaterialTheme.colorScheme.outline
        NotificationType.ACHIEVEMENT -> Icons.Default.EmojiEvents to Color(0xFFFFB300) // Warning gold
        NotificationType.UPDATE -> Icons.Default.Campaign to MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun EmptyNotificationView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.NotificationsOff,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bạn không có thông báo mới",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Khi có cập nhật hoặc nhắc nhở, chúng sẽ xuất hiện ở đây.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
        )
    }
}

fun formatTimestamp(date: Date): String {
    val now = System.currentTimeMillis()
    val diff = now - date.time
    
    return when {
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} phút trước"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)} giờ trước"
        diff < 48 * 60 * 60 * 1000 -> "Hôm qua"
        else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }
}