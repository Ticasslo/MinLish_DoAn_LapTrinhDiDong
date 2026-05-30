package com.example.englishapp.features.profile.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.englishapp.R
import com.example.englishapp.features.auth.domain.model.AuthResult
import com.example.englishapp.features.profile.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit = {},
    onNavItemClick: (Int) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val changeResult = uiState.changePasswordResult

    var currentPassword by remember { mutableStateOf("") }
    var newPassword     by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentVisible by remember { mutableStateOf(false) }
    var newVisible     by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current

    val newPasswordLengthError = newPassword.isNotEmpty() && newPassword.length < 8
    val samePasswordError = newPassword.isNotEmpty() && currentPassword.isNotEmpty() && newPassword == currentPassword
    val confirmPasswordError = confirmPassword.isNotEmpty() && confirmPassword != newPassword

    val newPasswordFieldError = newPasswordLengthError || samePasswordError

    val isLoading = changeResult is AuthResult.Loading

    val successMessage = stringResource(R.string.change_password_success)

    LaunchedEffect(changeResult) {
        if (changeResult is AuthResult.Success) {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            viewModel.resetChangePasswordState()
            onBackClick()
        }
    }

    val navItems = listOf(
        Icons.Outlined.Home to stringResource(R.string.nav_home),
        Icons.AutoMirrored.Outlined.MenuBook to stringResource(R.string.nav_library),
        Icons.Outlined.BarChart to stringResource(R.string.nav_progress),
        Icons.Filled.Person to stringResource(R.string.nav_profile)
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            ChangePasswordTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            ChangePasswordBottomBar(
                navItems = navItems,
                selectedIndex = 3,
                onItemClick = onNavItemClick,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 40.dp),
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.change_password_desc),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
            )

            PasswordField(
                label = stringResource(R.string.current_password),
                value = currentPassword,
                onValueChange = { currentPassword = it },
                placeholder = stringResource(R.string.current_password_placeholder),
                visible = currentVisible,
                onToggleVisibility = { currentVisible = !currentVisible },
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )

            Spacer(Modifier.height(24.dp))

            PasswordField(
                label = stringResource(R.string.new_password),
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = stringResource(R.string.new_password_placeholder),
                visible = newVisible,
                onToggleVisibility = { newVisible = !newVisible },
                isError = newPasswordFieldError,
                supportingText = when {
                    newPasswordLengthError -> stringResource(R.string.password_length_error)
                    samePasswordError -> stringResource(R.string.same_password_error)
                    else -> stringResource(R.string.password_length_error)
                },
                supportingTextIsError = newPasswordFieldError,
                imeAction = ImeAction.Next,
                onImeAction = { focusManager.moveFocus(FocusDirection.Down) },
            )

            Spacer(Modifier.height(24.dp))

            PasswordField(
                label = stringResource(R.string.confirm_new_password),
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = stringResource(R.string.confirm_new_password_placeholder),
                visible = confirmVisible,
                onToggleVisibility = { confirmVisible = !confirmVisible },
                isError = confirmPasswordError,
                supportingText = if (confirmPasswordError) stringResource(R.string.confirm_password_error) else null,
                supportingTextIsError = true,
                imeAction = ImeAction.Done,
                onImeAction = { focusManager.clearFocus() },
            )

            if (changeResult is AuthResult.Error) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = changeResult.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.changePassword(currentPassword, newPassword)
                },
                enabled = !isLoading
                        && currentPassword.isNotEmpty()
                        && newPassword.length >= 8
                        && newPassword != currentPassword
                        && confirmPassword == newPassword,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text = stringResource(R.string.save_changes),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean = false,
    supportingText: String? = null,
    supportingTextIsError: Boolean = false,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            },
            singleLine = true,
            isError = isError,
            visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = imeAction,
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() },
            ),
            trailingIcon = {
                IconButton(onClick = onToggleVisibility) {
                    Icon(
                        imageVector = if (visible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = if (visible) stringResource(R.string.hide_password) else stringResource(R.string.show_password),
                        tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor = MaterialTheme.colorScheme.error,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                errorContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.primary,
            ),
        )

        if (supportingText != null) {
            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodySmall,
                color = if (isError && supportingTextIsError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
        }
    }
}

@Composable
private fun ChangePasswordTopBar(onBackClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        shadowElevation = 1.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Text(
                text = stringResource(R.string.change_password),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun ChangePasswordBottomBar(
    navItems: List<Pair<ImageVector, String>>,
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
            navItems.forEachIndexed { index, (icon, label) ->
                val isSelected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .then(
                            if (isSelected) Modifier.background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                            else Modifier
                        )
                        .clickable { onItemClick(index) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
