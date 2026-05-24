package com.example.englishapp.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// =============================================================================
// DARK COLOR SCHEME
// =============================================================================
private val DarkColorScheme = darkColorScheme(
    // Primary
    primary = _root_ide_package_.com.example.englishapp.core.ui.theme.PrimaryDark,
    onPrimary = _root_ide_package_.com.example.englishapp.core.ui.theme.OnPrimaryDark,
    primaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.PrimaryContainerDark,
    onPrimaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.OnPrimaryContainerDark,

    // Secondary
    secondary = _root_ide_package_.com.example.englishapp.core.ui.theme.SecondaryDark,
    onSecondary = _root_ide_package_.com.example.englishapp.core.ui.theme.OnSecondaryDark,
    secondaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.SecondaryContainerDark,
    onSecondaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.OnSecondaryContainerDark,

    // Tertiary
    tertiary = _root_ide_package_.com.example.englishapp.core.ui.theme.TertiaryDark,
    onTertiary = _root_ide_package_.com.example.englishapp.core.ui.theme.OnTertiaryDark,
    tertiaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.TertiaryContainerDark,
    onTertiaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.OnTertiaryContainerDark,

    // Background / Surface
    background = _root_ide_package_.com.example.englishapp.core.ui.theme.BackgroundDark,
    onBackground = _root_ide_package_.com.example.englishapp.core.ui.theme.OnBackgroundDark,
    surface = _root_ide_package_.com.example.englishapp.core.ui.theme.SurfaceDark,
    onSurface = _root_ide_package_.com.example.englishapp.core.ui.theme.OnSurfaceDark,
    surfaceVariant = _root_ide_package_.com.example.englishapp.core.ui.theme.SurfaceVariantDark,
    onSurfaceVariant = _root_ide_package_.com.example.englishapp.core.ui.theme.OnSurfaceVariantDark,

    // Outline
    outline = _root_ide_package_.com.example.englishapp.core.ui.theme.OutlineDark,
    outlineVariant = _root_ide_package_.com.example.englishapp.core.ui.theme.OutlineVariantDark,

    // Error
    error = _root_ide_package_.com.example.englishapp.core.ui.theme.ColorError,
    onError = _root_ide_package_.com.example.englishapp.core.ui.theme.ColorOnError,
    errorContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.ErrorContainerDark,
    onErrorContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.OnErrorContainerDark,
)

// =============================================================================
// LIGHT COLOR SCHEME
// =============================================================================
private val LightColorScheme = lightColorScheme(
    // Primary
    primary = _root_ide_package_.com.example.englishapp.core.ui.theme.PrimaryLight,
    onPrimary = _root_ide_package_.com.example.englishapp.core.ui.theme.OnPrimaryLight,
    primaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.PrimaryContainerLight,
    onPrimaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.OnPrimaryContainerLight,

    // Secondary
    secondary = _root_ide_package_.com.example.englishapp.core.ui.theme.SecondaryLight,
    onSecondary = _root_ide_package_.com.example.englishapp.core.ui.theme.OnSecondaryLight,
    secondaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.SecondaryContainerLight,
    onSecondaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.OnSecondaryContainerLight,

    // Tertiary
    tertiary = _root_ide_package_.com.example.englishapp.core.ui.theme.TertiaryLight,
    onTertiary = _root_ide_package_.com.example.englishapp.core.ui.theme.OnTertiaryLight,
    tertiaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.TertiaryContainerLight,
    onTertiaryContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.OnTertiaryContainerLight,

    // Background / Surface
    background = _root_ide_package_.com.example.englishapp.core.ui.theme.BackgroundLight,
    onBackground = _root_ide_package_.com.example.englishapp.core.ui.theme.OnBackgroundLight,
    surface = _root_ide_package_.com.example.englishapp.core.ui.theme.SurfaceLight,
    onSurface = _root_ide_package_.com.example.englishapp.core.ui.theme.OnSurfaceLight,
    surfaceVariant = _root_ide_package_.com.example.englishapp.core.ui.theme.SurfaceVariantLight,
    onSurfaceVariant = _root_ide_package_.com.example.englishapp.core.ui.theme.OnSurfaceVariantLight,

    // Outline
    outline = _root_ide_package_.com.example.englishapp.core.ui.theme.OutlineLight,
    outlineVariant = _root_ide_package_.com.example.englishapp.core.ui.theme.OutlineVariantLight,

    // Error
    error = _root_ide_package_.com.example.englishapp.core.ui.theme.ColorError,
    onError = _root_ide_package_.com.example.englishapp.core.ui.theme.ColorOnError,
    errorContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.ErrorContainerLight,
    onErrorContainer = _root_ide_package_.com.example.englishapp.core.ui.theme.OnErrorContainerLight,
)

// =============================================================================
// THEME ENTRY POINT
// =============================================================================
@Composable
fun EngLishAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // false = bỏ qua Dynamic Color Android 12+, giữ nguyên bảng màu học thuật thiết kế riêng
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) _root_ide_package_.com.example.englishapp.core.ui.theme.DarkColorScheme else _root_ide_package_.com.example.englishapp.core.ui.theme.LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = _root_ide_package_.com.example.englishapp.core.ui.theme.Typography,
        content = content
    )
}