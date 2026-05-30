package com.example.englishapp.features.learn.presentation.flashcard

import android.speech.tts.TextToSpeech
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
import androidx.compose.ui.draw.clip
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
    
    // Khởi tạo TextToSpeech
    val tts = remember {
        var ttsInstance: TextToSpeech? = null
        ttsInstance = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsInstance?.language = Locale.US
            }
        }
        ttsInstance
    }

    // Giải phóng TTS khi không dùng nữa
    DisposableEffect(Unit) {
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    LaunchedEffect(setId, mode) {
        viewModel.loadCards(setId, mode)
    }

    LaunchedEffect(uiState.isSessionComplete) {
        if (uiState.isSessionComplete) {
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
                    Text(
                        text = "${uiState.currentIndex + 1}/${uiState.cards.size}",
                        modifier = Modifier.padding(end = 16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
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
            // Progress Bar
            LinearProgressIndicator(
                progress = if (uiState.cards.isEmpty()) 0f else (uiState.currentIndex + 1).toFloat() / uiState.cards.size,
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
            } else if (uiState.cards.isNotEmpty()) {
                val currentPair = uiState.cards[uiState.currentIndex]
                val word = currentPair.second

                // Flashcard View với hiệu ứng lật 3D
                FlashcardItem(
                    word = word,
                    isFlipped = uiState.isFlipped,
                    onFlip = viewModel::onFlip,
                    onSpeak = {
                        tts?.speak(word.word, TextToSpeech.QUEUE_FLUSH, null, null)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Rating Buttons (chỉ hiện khi đã lật thẻ)
                if (uiState.isFlipped) {
                    RatingButtonsRow(
                        ratingIntervals = uiState.ratingIntervals,
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
                Text(text = "Không có từ nào cần học!")
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
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onFlip)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
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
                    color = Color(0xFF1A1D3B)
                )
                
                word.pronunciation?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        IconButton(onClick = onSpeak) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Speak",
                                tint = Color(0xFF3D5AFE)
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
                    color = Color(0xFF3D5AFE),
                    textAlign = TextAlign.Center
                )
                
                word.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
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
    ratingIntervals: Map<String, String>,
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
            timeLabel = ratingIntervals["again"] ?: "< 1m",
            color = Color(0xFFEF4444),
            onClick = { onRatingSelected("again") }
        )
        RatingButton(
            label = "Hard",
            timeLabel = ratingIntervals["hard"] ?: "2d",
            color = Color(0xFFF59E0B),
            onClick = { onRatingSelected("hard") }
        )
        RatingButton(
            label = "Good",
            timeLabel = ratingIntervals["good"] ?: "4d",
            color = Color(0xFF3D5AFE),
            onClick = { onRatingSelected("good") }
        )
        RatingButton(
            label = "Easy",
            timeLabel = ratingIntervals["easy"] ?: "7d",
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
