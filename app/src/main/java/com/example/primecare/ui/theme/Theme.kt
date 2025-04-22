package com.example.primecare.ui.theme


import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.primecare.data.ThemeMode
import com.example.primecare.data.ThemePreferences



private val DarkColorScheme = darkColorScheme(
    primary = MainColor,        // Keep MainColor as primary in dark mode
    onPrimary = White,          // Text/icon color on top of primary
    background = Black,
    onBackground = White,
    tertiary = MainColor
)

private val LightColorScheme = lightColorScheme(
    primary = MainColor,        // Keep MainColor as primary in light mode
    onPrimary = Black,          // Text/icon color on top of primary
    background = White,
    onBackground = Black,
    tertiary = MainColor
)

@Composable
fun PrimeCareTheme(
    themePreferences: ThemePreferences,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val themeMode = themePreferences.themeMode.collectAsState(initial = ThemeMode.SYSTEM).value

    val isDarkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        // Dynamic colors (Android 12+) with fallback to ensure MainColor persists
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            val dynamicScheme = if (isDarkTheme) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            }
            // Override dynamic scheme to enforce MainColor
            dynamicScheme.copy(
                primary = MainColor,
                tertiary = MainColor,
                onPrimary = if (isDarkTheme) White else Black
            )
        }
        isDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}