package com.example.englishapp.features.vocab.presentation.vocab_list

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.core.data.model.Word
import com.example.englishapp.features.vocab.presentation.create_edit.ImportPreviewBottomSheet
import com.example.englishapp.features.vocab.presentation.model.WordUiItem
import com.example.englishapp.features.vocab.presentation.viewmodel.VocabViewModel

/**
 * Màn hình chi tiết bộ từ vựng (VocabListScreen) hiển thị toàn bộ từ thuộc bộ từ
 * và cung cấp các tính năng: Học (SRS), Ôn tập, Tìm kiếm, Lọc, Import/Export CSV, Thêm từ vựng.
 */
@Composable
fun VocabListScreen(
    setId: String,
    onBackClick: () -> Unit,
    onLearnClick: (String) -> Unit, // Đi tới màn hình học thẻ ghi nhớ SRS
    onReviewClick: (String) -> Unit, // Đi tới màn hình ôn tập SRS
    viewModel: VocabViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Trạng thái hiển thị BottomSheet tra cứu & thêm từ
    var showAddWordSheet by remember { mutableStateOf(false) }
    
    // Trạng thái từ vựng được chọn để chỉnh sửa (null nghĩa là thêm mới)
    var selectedWordForEdit by remember { mutableStateOf<Word?>(null) }
    
    // Trạng thái hiển thị Import/Export CSV dialog
    var showImportSheet by remember { mutableStateOf(false) }
    var csvContentToImport by remember { mutableStateOf("") }
    
    // Khởi tạo và nạp dữ liệu bộ từ vựng khi mở màn hình
    LaunchedEffect(setId) {
        viewModel.initialize(setId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            VocabListTopBar(
                title = uiState.set?.name ?: "Oxford Essential",
                onBackClick = onBackClick,
                onExportClick = {
                    viewModel.exportCsv { csv ->
                        if (csv.isNotBlank()) {
                            // Copy CSV to clipboard or show toast
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Vocab CSV", csv)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Đã xuất dữ liệu và copy CSV vào bộ nhớ đệm!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Bộ từ trống, không thể xuất CSV.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                onImportClick = {
                    // Để giả lập, ta hiển thị input CSV thô cho người dùng
                    csvContentToImport = "Resilient,Kiên cường có khả năng phục hồi nhanh,/rɪˈzɪl.jənt/\nProlific,Sáng tác nhiều hiệu suất cao,/prəˈlɪf.ɪk/\nUbiquitous,Có mặt ở khắp mọi nơi,/juːˈbɪk.wɪ.təs/"
                    showImportSheet = true
                }
            )
        },
        floatingActionButton = {
            // Nút FAB thêm từ vựng mới
            FloatingActionButton(
                onClick = {
                    selectedWordForEdit = null
                    showAddWordSheet = true
                },
                shape = RoundedCornerShape(12.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Thêm từ mới",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Thẻ thống kê SubHeader
                    item {
                        VocabStatsHeader(
                            totalWords = uiState.words.size,
                            masteryPercent = if (uiState.words.isNotEmpty()) {
                                (uiState.words.count { it.status == "mastered" } * 100) / uiState.words.size
                            } else 0,
                            onLearnClick = { onLearnClick(setId) },
                            onReviewClick = { onReviewClick(setId) }
                        )
                    }

                    // 2. Thanh tìm kiếm và lọc trạng thái
                    item {
                        SearchAndFiltersSection(
                            searchQuery = uiState.searchQuery,
                            onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
                            filterStatus = uiState.filterStatus,
                            onFilterStatusChanged = { viewModel.onFilterStatusChanged(it) }
                        )
                    }

                    // 3. Danh sách các thẻ từ vựng
                    if (uiState.filteredWords.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Không tìm thấy từ vựng nào.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(uiState.filteredWords) { uiItem ->
                            WordCard(
                                uiItem = uiItem,
                                onClick = {
                                    selectedWordForEdit = uiItem.word
                                    showAddWordSheet = true
                                }
                            )
                        }
                    }

                    // 4. Ghost Card gợi ý thêm từ nằm cuối danh sách
                    item {
                        GhostCard(
                            onClick = {
                                selectedWordForEdit = null
                                showAddWordSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Hiển thị BottomSheet tra cứu & thêm từ
    if (showAddWordSheet) {
        AddWordBottomSheet(
            wordToEdit = selectedWordForEdit,
            onDismissRequest = { showAddWordSheet = false },
            onSaveWord = { wordVal, meaningVal, pronVal, descVal, examVal ->
                if (selectedWordForEdit == null) {
                    viewModel.addWord(wordVal, meaningVal, pronVal, descVal, examVal)
                } else {
                    val updated = selectedWordForEdit!!.copy(
                        word = wordVal,
                        meaning = meaningVal,
                        pronunciation = pronVal,
                        description = descVal,
                        example = examVal
                    )
                    viewModel.editWord(updated)
                }
                showAddWordSheet = false
            },
            onDeleteWord = {
                selectedWordForEdit?.let { viewModel.deleteWord(it) }
                showAddWordSheet = false
            },
            onLookupWord = { wordVal, onResult ->
                viewModel.lookupWordOnline(wordVal, onResult)
            }
        )
    }

    // Hiển thị Import preview sheet
    if (showImportSheet) {
        ImportPreviewBottomSheet(
            csvContent = csvContentToImport,
            onDismissRequest = { showImportSheet = false },
            onConfirmImport = { csv ->
                viewModel.importCsv(csv) {
                    showImportSheet = false
                    Toast.makeText(context, "Nhập thành công từ vựng!", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

/**
 * Thanh TopAppBar cho chi tiết bộ từ vựng
 */
@Composable
private fun VocabListTopBar(
    title: String,
    onBackClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Quay lại",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Row {
                IconButton(onClick = onImportClick) {
                    Icon(
                        imageVector = Icons.Default.Edit, // Tượng trưng cho thêm hàng loạt
                        contentDescription = "Nhập CSV",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Thêm",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Export CSV") },
                            onClick = {
                                showMenu = false
                                onExportClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Import CSV (Mẫu)") },
                            onClick = {
                                showMenu = false
                                onImportClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Thống kê bộ từ và các nút bấm học
 */
@Composable
private fun VocabStatsHeader(
    totalWords: Int,
    masteryPercent: Int,
    onLearnClick: () -> Unit,
    onReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Dòng thống kê
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Đếm từ
                Column {
                    Text(
                        text = "Tổng số từ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$totalWords",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                // % Mastery
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "% Thông thạo",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$masteryPercent%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981) // Success Color
                    )
                }
                // Streak liên tiếp
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Streak học tập",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "12",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B) // Warning Color
                        )
                        Icon(
                            imageVector = Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Nút bắt đầu học
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onLearnClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Học ngay", fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = onReviewClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Outlined.History, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ôn tập", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Tìm kiếm và bộ lọc trạng thái học tập
 */
@Composable
private fun SearchAndFiltersSection(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    filterStatus: String,
    onFilterStatusChanged: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Thanh nhập tìm kiếm
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Tìm kiếm từ vựng...") },
            leadingIcon = { Icon(imageVector = Icons.Outlined.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Các Chip bộ lọc
        val filterChips = listOf("Tất cả", "Chưa học", "Đang học", "Đã thuộc")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterChips.forEach { chipName ->
                val isSelected = chipName == filterStatus
                
                val containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                val textColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                val borderStroke = if (isSelected) null else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(containerColor)
                        .then(if (borderStroke != null) Modifier.border(borderStroke, RoundedCornerShape(20.dp)) else Modifier)
                        .clickable { onFilterStatusChanged(chipName) }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = chipName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }
}

/**
 * Thẻ từ vựng chi tiết hiển thị dấu chấm SRS
 */
@Composable
private fun WordCard(
    uiItem: WordUiItem,
    onClick: () -> Unit
) {
    // Xác định màu sắc chấm tròn dựa trên trạng thái SRS
    // "new" (đỏ), "learning" (cam/vàng), "mastered" (xanh success)
    val statusDotColor = when (uiItem.status) {
        "mastered" -> Color(0xFF10B981) // Success
        "learning" -> Color(0xFFF59E0B) // Warning
        else -> Color(0xFFEF4444) // Error
    }

    val statusText = when (uiItem.status) {
        "mastered" -> "Mastery"
        "learning" -> "Learning"
        else -> "New"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Chấm màu trạng thái SRS
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusDotColor)
            )

            // Thông tin từ và định nghĩa
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiItem.word.word,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Thẻ nhãn trạng thái học
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusDotColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = statusText.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = statusDotColor
                        )
                    }
                }

                if (!uiItem.word.pronunciation.isNullOrEmpty()) {
                    Text(
                        text = uiItem.word.pronunciation!!,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiItem.word.meaning,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            //Badge nhắc nhở ôn tập SRS bên phải
            if (uiItem.status != "new") {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "NEXT REVIEW",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Black
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = uiItem.nextReviewText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Chưa học",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Thẻ khung đứt (Ghost Card) ở đáy để gợi ý bấm thêm từ vựng
 */
@Composable
private fun GhostCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Thêm từ vựng mới vào bộ thẻ",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

