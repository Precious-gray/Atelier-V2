package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MyApplicationTheme(
    preset: AtelierThemePreset = AtelierThemePreset.CYBER_LUMINESCENCE,
    customAccent: Color? = null,
    content: @Composable () -> Unit
) {
    val activeColors = remember(preset, customAccent) {
        preset.toAtelierColors(customAccent)
    }

    val colorScheme = remember(activeColors) {
        if (activeColors.isLight) {
            lightColorScheme(
                primary = activeColors.primaryAccent,
                onPrimary = Color.White,
                primaryContainer = activeColors.surfaceElevated,
                onPrimaryContainer = activeColors.primaryAccent,
                secondary = activeColors.secondaryAccent,
                onSecondary = Color.White,
                secondaryContainer = activeColors.surfaceElevated,
                onSecondaryContainer = activeColors.secondaryAccent,
                tertiary = activeColors.tertiaryAccent,
                onTertiary = Color.White,
                background = activeColors.background,
                onBackground = activeColors.textPrimary,
                surface = activeColors.surfaceCard,
                onSurface = activeColors.textPrimary,
                surfaceVariant = activeColors.surfaceElevated,
                onSurfaceVariant = activeColors.textSecondary,
                outline = activeColors.borderGlass,
                outlineVariant = activeColors.darkDivider
            )
        } else {
            darkColorScheme(
                primary = activeColors.primaryAccent,
                onPrimary = activeColors.textPrimary,
                primaryContainer = activeColors.surfaceElevated,
                onPrimaryContainer = activeColors.primaryAccent.copy(alpha = 0.8f),
                secondary = activeColors.secondaryAccent,
                onSecondary = activeColors.background,
                secondaryContainer = activeColors.surfaceElevated,
                onSecondaryContainer = activeColors.secondaryAccent,
                tertiary = activeColors.tertiaryAccent,
                onTertiary = activeColors.textPrimary,
                background = activeColors.background,
                onBackground = activeColors.textPrimary,
                surface = activeColors.surfaceCard,
                onSurface = activeColors.textPrimary,
                surfaceVariant = activeColors.surfaceElevated,
                onSurfaceVariant = activeColors.textSecondary,
                outline = activeColors.borderGlass,
                outlineVariant = activeColors.darkDivider
            )
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = activeColors.background.toArgb()
                window.navigationBarColor = activeColors.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = activeColors.isLight
                insetsController.isAppearanceLightNavigationBars = activeColors.isLight
            }
        }
    }

    CompositionLocalProvider(
        LocalAtelierColors provides activeColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
