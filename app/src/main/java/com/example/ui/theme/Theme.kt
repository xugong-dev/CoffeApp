package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SleekPrimaryDark,
    secondary = SleekSecondaryDark,
    tertiary = SleekTertiary,
    background = SleekBgDark,
    surface = SleekSurfaceDark,
    onPrimary = Color(0xFF381E72),
    onSecondary = Color(0xFFE8DEF8),
    onBackground = SleekTextPrimaryDark,
    onSurface = SleekTextPrimaryDark,
    surfaceVariant = SleekSurfaceVariantDark,
    onSurfaceVariant = SleekTextSecondaryDark,
    outline = SleekOutlineDark,
    outlineVariant = SleekOutlineVariantDark
)

private val LightColorScheme = lightColorScheme(
    primary = SleekPrimary,
    secondary = SleekSecondary,
    tertiary = SleekTertiary,
    background = SleekBg,
    surface = SleekSurface,
    onPrimary = Color.White,
    onSecondary = Color(0xFF1D192B),
    onBackground = SleekTextPrimary,
    onSurface = SleekTextPrimary,
    surfaceVariant = SleekSurfaceVariant,
    onSurfaceVariant = SleekTextSecondary,
    outline = SleekOutline,
    outlineVariant = SleekOutlineVariant
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set to false to strictly enforce our beautiful design theme style
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
