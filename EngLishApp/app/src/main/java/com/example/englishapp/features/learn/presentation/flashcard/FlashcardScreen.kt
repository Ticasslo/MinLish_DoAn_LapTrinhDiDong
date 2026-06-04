package com.example.englishapp.features.learn.presentation.flashcard

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.widget.Toast
import com.example.englishapp.core.util.AudioUtils
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.core.data.model.Word
import com.example.englishapp.features.learn.presentation.viewmodel.LearnViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    setId: String,
    mode: String,
    onBackClick: () -> Unit,
    onSessionComplete: () -> Unit,
    viewModel: LearnViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }

    // Giải phóng MediaPlayer khi rời màn hình
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    // Hàm phát âm sử dụng Google Translate TTS API
    fun playAudio(url: String) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener { it.start() }
            mediaPlayer.setOnErrorListener { _, what, extra ->
                Log.e("AudioError", "MediaPlayer error: $what, $extra")
                true
            }
        } catch (e: Exception) {
            Log.e("AudioError", "Lỗi phát âm: ${e.message}")
            Toast.makeText(context, "Không thể phát âm", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(setId, mode) {
        viewModel.loadCards(setId, mode)
    }

    LaunchedEffect(uiState.isSessionComplete) {
        if (uiState.isSessionComplete && uiState.sessionStats.totalStudied > 0) {
            onSessionComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = if (mode == "review") "Ôn tập" else "Học từ mới",
                        style = MaterialTheme.typography.titleMedium
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val remaining = uiState.cards.size - uiState.currentIndex
                    val total = uiState.totalOriginalCards

                    val currentDisplayIndex = if (total > 0) {
                        (total - remaining + 1).coerceIn(1, total)
                    } else 0

                    Text(
                        text = "$currentDisplayIndex/$total",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Thanh tiến trình cũng tính theo con số thực tế đã hoàn thành
            val remaining = uiState.cards.size - uiState.currentIndex
            val completedCount = (uiState.totalOriginalCards - remaining).coerceAtLeast(0)
            val progress = if (uiState.totalOriginalCards > 0) completedCount.toFloat() / uiState.totalOriginalCards else 0f

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.cards.isNotEmpty() && uiState.currentIndex < uiState.cards.size) {
                val currentPair = uiState.cards[uiState.currentIndex]
                val word = currentPair.second

                // Flashcard View với hiệu ứng lật 3D
                key(uiState.currentIndex) {
                    FlashcardItem(
                        word = word,
                        isFlipped = uiState.isFlipped,
                        onFlip = viewModel::onFlip,
                        onSpeak = {
                            playAudio(AudioUtils.getGoogleTtsUrl(word.word))
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Rating Buttons (chỉ hiện khi đã lật thẻ)
                if (uiState.isFlipped) {
                    RatingButtonsRow(
                        onRatingSelected = viewModel::onRatingSelected
                    )
                } else {
                    Text(
                        text = "Chạm vào thẻ để xem nghĩa",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            } else if (!uiState.isLoading && uiState.cards.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tuyệt vời! 🎉",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bạn đã hoàn thành hết các từ cần ôn tập của ngày hôm nay.",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = onBackClick) {
                            Text("Quay lại")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardItem(
    word: Word,
    isFlipped: Boolean,
    onFlip: () -> Unit,
    onSpeak: () -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f)
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .clickable(onClick = onFlip)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            // Mặt trước
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = word.word,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                word.pronunciation?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        IconButton(onClick = onSpeak) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Speak",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        } else {
            // Mặt sau
            Column(
                modifier = Modifier.graphicsLayer { rotationY = 180f },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = word.meaning,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
                
                word.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
                
                word.example?.let {
                    Text(
                        text = "\"$it\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun RatingButtonsRow(
    onRatingSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RatingButton(
            label = "Again",
            timeLabel = "< 1m",
            color = Color(0xFFEF4444),
            onClick = { onRatingSelected("again") }
        )
        RatingButton(
            label = "Hard",
            timeLabel = "2d",
            color = Color(0xFFF59E0B),
            onClick = { onRatingSelected("hard") }
        )
        RatingButton(
            label = "Good",
            timeLabel = "4d",
            color = Color(0xFF3D5AFE),
            onClick = { onRatingSelected("good") }
        )
        RatingButton(
            label = "Easy",
            timeLabel = "7d",
            color = Color(0xFF10B981),
            onClick = { onRatingSelected("easy") }
        )
    }
}

@Composable
fun RatingButton(
    label: String,
    timeLabel: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = color.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(width = 70.dp, height = 50.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(text = label, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = timeLabel,
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

fun Modifier.clip(shape: RoundedCornerShape) = this.graphicsLayer {
    this.shape = shape
    this.clip = true
}
