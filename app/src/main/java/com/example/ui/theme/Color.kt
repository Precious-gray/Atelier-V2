package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dynamic Theme Palette Accessors delegating to active LocalAtelierColors
val AtelierBackground: Color
    @Composable
    get() = LocalAtelierColors.current.background

val AtelierSurfaceCard: Color
    @Composable
    get() = LocalAtelierColors.current.surfaceCard

val AtelierSurfaceElevated: Color
    @Composable
    get() = LocalAtelierColors.current.surfaceElevated

val AtelierSurfaceGlass: Color
    @Composable
    get() = LocalAtelierColors.current.surfaceGlass

val AtelierBorderGlass: Color
    @Composable
    get() = LocalAtelierColors.current.borderGlass

// Dynamic Neon & Luxury Accents
val NeonViolet: Color
    @Composable
    get() = LocalAtelierColors.current.primaryAccent

val NeonVioletLight: Color
    @Composable
    get() = LocalAtelierColors.current.primaryAccent.copy(alpha = 0.8f)

val NeonCyan: Color
    @Composable
    get() = LocalAtelierColors.current.secondaryAccent

val NeonCyanDark: Color
    @Composable
    get() = LocalAtelierColors.current.secondaryAccent.copy(alpha = 0.6f)

val NeonMagenta: Color
    @Composable
    get() = LocalAtelierColors.current.tertiaryAccent

val CyberGold: Color
    @Composable
    get() = LocalAtelierColors.current.goldAccent

val EmeraldGlow: Color
    @Composable
    get() = LocalAtelierColors.current.emeraldAccent

// Typography Colors
val TextPrimary: Color
    @Composable
    get() = LocalAtelierColors.current.textPrimary

val TextSecondary: Color
    @Composable
    get() = LocalAtelierColors.current.textSecondary

val TextMuted: Color
    @Composable
    get() = LocalAtelierColors.current.textMuted

val DarkDivider: Color
    @Composable
    get() = LocalAtelierColors.current.darkDivider
