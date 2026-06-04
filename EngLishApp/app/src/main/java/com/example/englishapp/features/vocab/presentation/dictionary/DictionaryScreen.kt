package com.example.englishapp.features.vocab.presentation.dictionary

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.features.vocab.presentation.viewmodel.DictionaryViewModel

/**
 * Màn hình tra cứu từ điển trực tuyến:
 * - Nhập từ tiếng Anh → lấy phiên âm IPA, nghĩa EN + dịch nghĩa tiếng Việt
 * - Nút Lưu → truyền dữ liệu đã điền sẵn vào màn hình thêm từ
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    onBackClick: () -> Unit = {},
    onSaveWord: (word: String, phonetic: String, vietnameseMeaning: String, englishDefinition: String, example: String) -> Unit = { _, _, _, _, _ -> },
    viewModel: DictionaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            DictionaryTopBar(onBackClick = onBackClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            // Ô nhập từ cần tra cứu
            SearchInputSection(
                query = uiState.query,
                isLoading = uiState.isLoading,
                onQueryChanged = viewModel::onQueryChanged,
                onSearch = {
                    keyboardController?.hide()
                    viewModel.lookup()
                }
            )

            // Thông báo lỗi
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                uiState.error?.let { errorMsg ->
                    ErrorCard(message = errorMsg)
                }
            }

            // Kết quả tra cứu
            AnimatedVisibility(
                visible = uiState.hasResult,
                enter = fadeIn(tween(400)) + expandVertically(tween(400)),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (uiState.hasResult) {
                    ResultSection(
                        word = uiState.word,
                        phonetic = uiState.phonetic,
                        partOfSpeech = uiState.partOfSpeech,
                        englishDefinition = uiState.englishDefinition,
                        vietnameseMeaning = uiState.vietnameseMeaning,
                        example = uiState.example,
                        onSaveClick = {
                            onSaveWord(
                                uiState.word,
                                uiState.phonetic,
                                uiState.vietnameseMeaning,
                                uiState.englishDefinition,
                                uiState.example
                            )
                        }
                    )
                }
            }

            // Hướng dẫn khi chưa tra cứu
            AnimatedVisibility(
                visible = !uiState.hasResult && !uiState.isLoading && uiState.error == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                HintSection()
            }
        }
    }
}

// TOP APP BAR
@Composable
private fun DictionaryTopBar(onBackClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Nút quay lại
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            // Tiêu đề giữa
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Từ điển trực tuyến",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Anh – Việt",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Ô TÌM KIẾM
@Composable
private fun SearchInputSection(
    query: String,
    isLoading: Boolean,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Nhập từ tiếng Anh",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChanged,
            placeholder = { Text("VD: resilient, ubiquitous...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChanged("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Xóa", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = onSearch,
            enabled = query.isNotBlank() && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    strokeWidth = 2.5.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text("Đang tra cứu...", fontWeight = FontWeight.Bold)
            } else {
                Icon(Icons.Outlined.ManageSearch, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Tra cứu", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// KẾT QUẢ TRA CỨU
@Composable
private fun ResultSection(
    word: String,
    phonetic: String,
    partOfSpeech: String,
    englishDefinition: String,
    vietnameseMeaning: String,
    example: String,
    onSaveClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // Thẻ từ chính
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Từ vựng chính
                        Text(
                            text = word,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        // Phiên âm IPA
                        if (phonetic.isNotBlank()) {
                            Text(
                                text = phonetic,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontStyle = FontStyle.Italic
                            )
                        }
                        // Loại từ
                        if (partOfSpeech.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = partOfSpeech,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Nút Lưu từ
                    Button(
                        onClick = onSaveClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Lưu", fontWeight = FontWeight.Bold)
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Nghĩa tiếng Việt
                if (vietnameseMeaning.isNotBlank()) {
                    InfoRow(
                        icon = Icons.Outlined.Translate,
                        iconColor = Color(0xFF10B981),
                        label = "Nghĩa tiếng Việt",
                        value = vietnameseMeaning
                    )
                }
            }
        }

        // Thẻ định nghĩa tiếng Anh
        if (englishDefinition.isNotBlank()) {
            DetailCard(
                icon = Icons.Outlined.Article,
                title = "Định nghĩa (EN)",
                content = englishDefinition,
                accentColor = MaterialTheme.colorScheme.primary
            )
        }

        // Thẻ câu ví dụ
        if (example.isNotBlank()) {
            DetailCard(
                icon = Icons.Outlined.FormatQuote,
                title = "Câu ví dụ",
                content = "\"$example\"",
                accentColor = Color(0xFFF59E0B),
                contentStyle = FontStyle.Italic
            )
        }

        // Nút lưu lớn ở cuối
        Button(
            onClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Lưu từ vào bộ từ vựng",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

// COMPONENTS PHỤ
@Composable
private fun InfoRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DetailCard(
    icon: ImageVector,
    title: String,
    content: String,
    accentColor: Color,
    contentStyle: FontStyle = FontStyle.Normal
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontStyle = contentStyle
                )
            }
        }
    }
}

@Composable
private fun ErrorCard(message: String) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
private fun HintSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.MenuBook,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Tra cứu từ điển Anh–Việt",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Nhập từ tiếng Anh bên trên để xem:\n• Phiên âm IPA\n• Nghĩa tiếng Việt\n• Định nghĩa tiếng Anh\n• Câu ví dụ minh họa",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}
