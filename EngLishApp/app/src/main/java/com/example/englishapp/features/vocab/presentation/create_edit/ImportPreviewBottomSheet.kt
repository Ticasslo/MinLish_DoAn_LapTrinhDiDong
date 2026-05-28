package com.example.englishapp.features.vocab.presentation.create_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.englishapp.core.util.CsvParser

/**
 * BottomSheet hiển thị xem trước danh sách từ vựng phân tích từ CSV
 * giúp người dùng xác minh tính chính xác trước khi bấm "Nhập ngay".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportPreviewBottomSheet(
    csvContent: String,
    onDismissRequest: () -> Unit,
    onConfirmImport: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    // Parse nội dung CSV ngay khi hiển thị BottomSheet
    val parsedWords = remember(csvContent) {
        CsvParser.parseCsv(csvContent)
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Thanh tiêu đề và nút Xác nhận nhập
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Xem trước từ nhập (${parsedWords.size} từ)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Button(
                    onClick = {
                        onConfirmImport(csvContent)
                    },
                    enabled = parsedWords.isNotEmpty(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Nhập ngay", fontWeight = FontWeight.Bold)
                }
            }

            // Danh sách xem trước hoặc thông báo lỗi
            if (parsedWords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không tìm thấy dữ liệu từ vựng hợp lệ. Hãy kiểm tra lại định dạng file CSV (Từ vựng, Định nghĩa, Phiên âm).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    items(parsedWords) { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = item.word,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (!item.pronunciation.isNullOrEmpty()) {
                                        Text(
                                            text = item.pronunciation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.meaning,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}