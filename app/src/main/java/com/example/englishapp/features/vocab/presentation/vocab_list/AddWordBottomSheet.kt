package com.example.englishapp.features.vocab.presentation.vocab_list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.ManageSearch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.englishapp.core.data.model.Word
import com.example.englishapp.features.vocab.presentation.viewmodel.DictionaryLookupResult

/**
 * BottomSheet kéo lên cho phép Tra cứu từ điển trực tuyến hoặc điền thông tin từ vựng thủ công
 * và thực hiện các thao tác Thêm, Sửa, Xóa từ vựng chi tiết.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWordBottomSheet(
    wordToEdit: Word?,
    onDismissRequest: () -> Unit,
    onSaveWord: (word: String, meaning: String, pronunciation: String?, description: String?, example: String?) -> Unit,
    onDeleteWord: () -> Unit,
    onLookupEnToVi: (word: String, onResult: (DictionaryLookupResult?) -> Unit) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Khởi tạo các trạng thái trường nhập liệu dựa trên wordToEdit
    var wordVal by remember { mutableStateOf(wordToEdit?.word ?: "") }
    var meaningVal by remember { mutableStateOf(wordToEdit?.meaning ?: "") }
    var pronVal by remember { mutableStateOf(wordToEdit?.pronunciation ?: "") }
    var descVal by remember { mutableStateOf(wordToEdit?.description ?: "") }
    var examVal by remember { mutableStateOf(wordToEdit?.example ?: "") }

    // Điều khiển hiển thị sheet tra cứu từ điển
    var showLookupSheet by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxHeight(0.85f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Đầu BottomSheet: Tiêu đề và các nút Lưu/Xóa
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (wordToEdit == null) "Thêm từ vựng mới" else "Cập nhật từ vựng",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (wordToEdit != null) {
                        IconButton(onClick = onDeleteWord) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Xóa từ",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (wordVal.isNotBlank() && meaningVal.isNotBlank()) {
                                onSaveWord(
                                    wordVal.trim(),
                                    meaningVal.trim(),
                                    pronVal.trim().ifEmpty { null },
                                    descVal.trim().ifEmpty { null },
                                    examVal.trim().ifEmpty { null }
                                )
                            }
                        },
                        enabled = wordVal.isNotBlank() && meaningVal.isNotBlank(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Lưu", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Nút mở màn hình tra cứu từ điển (Chỉ hiển thị khi thêm mới)
            if (wordToEdit == null) {
                OutlinedButton(
                    onClick = { showLookupSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ManageSearch,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tra cứu từ điển trực tuyến", fontWeight = FontWeight.Bold)
                }
            }

            // Các trường nhập liệu
            OutlinedTextField(
                value = wordVal,
                onValueChange = { wordVal = it },
                label = { Text("Thuật ngữ / Từ vựng *") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pronVal,
                onValueChange = { pronVal = it },
                label = { Text("Phiên âm IPA (Ví dụ: /rɪˈzɪl.jənt/)") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = meaningVal,
                onValueChange = { meaningVal = it },
                label = { Text("Nghĩa tiếng Việt *") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descVal,
                onValueChange = { descVal = it },
                label = { Text("Định nghĩa tiếng Anh (Không bắt buộc)") },
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = examVal,
                onValueChange = { examVal = it },
                label = { Text("Ví dụ minh họa (Không bắt buộc)") },
                minLines = 2,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ── Sheet tra cứu từ điển (hiện đè lên AddWordBottomSheet) ────────────────────
    if (showLookupSheet) {
        DictionaryLookupSheet(
            onDismissRequest = { showLookupSheet = false },
            onExport = { englishWord, ipa, viMeaning, example ->
                wordVal = englishWord
                if (!ipa.isNullOrEmpty()) pronVal = ipa
                if (!viMeaning.isNullOrEmpty()) meaningVal = viMeaning
                if (!example.isNullOrEmpty()) examVal = example
                showLookupSheet = false
            },
            onLookupEnToVi = onLookupEnToVi
        )
    }
}
