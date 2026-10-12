package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = GoldPrimary,
    onPrimary = ObsidianBackground,
    primaryContainer = GoldDark,
    onPrimaryContainer = TextWhite,
    secondary = CyanAccent,
    onSecondary = ObsidianBackground,
    secondaryContainer = CyanDark,
    onSecondaryContainer = TextWhite,
    tertiary = BullishGreen,
    onTertiary = ObsidianBackground,
    background = ObsidianBackground,
    onBackground = TextWhite,
    surface = ObsidianSurface,
    onSurface = TextWhite,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianCardBorder,
    error = BearishRed,
    onError = TextWhite,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(colorScheme = DarkColorScheme, typography = Typography, content = content)
}
