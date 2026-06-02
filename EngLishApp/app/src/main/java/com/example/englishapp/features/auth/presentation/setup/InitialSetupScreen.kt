package com.example.englishapp.features.auth.presentation.setup

import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.core.data.sync.SyncWorker
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.auth.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InitialSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val updateState = uiState.updateProfileResult
    val authState = uiState.authResult
    val isLoading = uiState.isLoading
    val scrollState = rememberScrollState()

    // Lấy tên User từ authState (nếu có)
    val userName = (authState as? AuthResult.Success)?.data?.name ?: "Bạn"

    var selectedGoal by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("") }

    val goals = listOf("IELTS", "TOEIC", "Business", "Travel", "Communication")
    val levels = listOf("A1", "A2", "B1", "B2", "C1", "C2")

    // Bộ xử lý kết quả xin quyền
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // Cập nhật trạng thái vào DB và Server
        viewModel.updateUserProfile(userName, selectedGoal, selectedLevel, isGranted)
    }

    LaunchedEffect(updateState) {
        if (updateState is AuthResult.Success) {
            onSetupComplete()
            viewModel.resetState()
        } else if (updateState is AuthResult.Error) {
            Toast.makeText(context, "Lỗi: ${updateState.message}", Toast.LENGTH_LONG).show()
            viewModel.resetState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        Text(
            text = "Chào $userName,",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Text(
            text = "Thiết lập mục tiêu cá nhân hóa lộ trình",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Bạn học Tiếng Anh để làm gì?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            goals.forEach { goal ->
                val isSelected = selectedGoal == goal
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedGoal = goal },
                    label = { Text(goal) },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Trình độ hiện tại của bạn?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            levels.forEach { level ->
                val isSelected = selectedLevel == level
                SuggestionChip(
                    onClick = { selectedLevel = level },
                    label = { Text(level) },
                    shape = RoundedCornerShape(12.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        labelColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        iconContentColor = MaterialTheme.colorScheme.primary
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        borderWidth = if (isSelected) 2.dp else 1.dp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                if (selectedGoal.isEmpty() || selectedLevel.isEmpty()) {
                    Toast.makeText(context, "Vui lòng chọn đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                } else {
                    // Nếu là Android 13 trở lên (API 33), ta xin quyền thông báo trước khi update
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val status = ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                        if (status != PackageManager.PERMISSION_GRANTED) {
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.updateUserProfile(userName, selectedGoal, selectedLevel, true)
                        }
                    } else {
                        // Android cũ hơn thì mặc định là true (vì quyền được cấp lúc cài đặt)
                        viewModel.updateUserProfile(userName, selectedGoal, selectedLevel, true)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary, 
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Bắt đầu ngay",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
