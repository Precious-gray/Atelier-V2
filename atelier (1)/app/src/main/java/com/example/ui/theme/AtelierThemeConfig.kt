package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class AtelierColors(
    val background: Color,
    val surfaceCard: Color,
    val surfaceElevated: Color,
    val surfaceGlass: Color,
    val borderGlass: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val tertiaryAccent: Color,
    val goldAccent: Color,
    val emeraldAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val darkDivider: Color,
    val isLight: Boolean = false
)

enum class AtelierThemePreset(
    val id: String,
    val title: String,
    val subtitle: String,
    val background: Color,
    val surfaceCard: Color,
    val surfaceElevated: Color,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val tertiaryAccent: Color,
    val goldAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val isLight: Boolean = false
) {
    CYBER_LUMINESCENCE(
        id = "cyber_luminescence",
        title = "Cyber Luminescence",
        subtitle = "Deep obsidian with neon violet, electric cyan, and cyber gold",
        background = Color(0xFF0D0D12),
        surfaceCard = Color(0xFF1A1A24),
        surfaceElevated = Color(0xFF222232),
        primaryAccent = Color(0xFFA855F7),
        secondaryAccent = Color(0xFF00F0FF),
        tertiaryAccent = Color(0xFFFF2E93),
        goldAccent = Color(0xFFFBBF24),
        textPrimary = Color(0xFFF9FAFB),
        textSecondary = Color(0xFF9CA3AF)
    ),
    MIDNIGHT_ROYAL(
        id = "midnight_royal",
        title = "Midnight Royal Velvet",
        subtitle = "Deep naval indigo with 24K bullion gold and crimson trims",
        background = Color(0xFF090D1A),
        surfaceCard = Color(0xFF131B30),
        surfaceElevated = Color(0xFF1B2642),
        primaryAccent = Color(0xFFE5B842),
        secondaryAccent = Color(0xFF38BDF8),
        tertiaryAccent = Color(0xFFEF4444),
        goldAccent = Color(0xFFFFD700),
        textPrimary = Color(0xFFF8FAFC),
        textSecondary = Color(0xFF94A3B8)
    ),
    EMERALD_HAUTE(
        id = "emerald_haute",
        title = "Emerald Haute Couture",
        subtitle = "Deep imperial jade with radiant emerald and rose gold",
        background = Color(0xFF06130E),
        surfaceCard = Color(0xFF0E241C),
        surfaceElevated = Color(0xFF153328),
        primaryAccent = Color(0xFF10B981),
        secondaryAccent = Color(0xFF2DD4BF),
        tertiaryAccent = Color(0xFFF43F5E),
        goldAccent = Color(0xFFF59E0B),
        textPrimary = Color(0xFFF0FDF4),
        textSecondary = Color(0xFF86EFAC)
    ),
    ALABASTER_STUDIO(
        id = "alabaster_studio",
        title = "Alabaster Daylight Atelier",
        subtitle = "Clean daylight cutting room with crisp alabaster and royal violet",
        background = Color(0xFFF4F5F7),
        surfaceCard = Color(0xFFFFFFFF),
        surfaceElevated = Color(0xFFE5E7EB),
        primaryAccent = Color(0xFF7C3AED),
        secondaryAccent = Color(0xFF0284C7),
        tertiaryAccent = Color(0xFFE11D48),
        goldAccent = Color(0xFFD97706),
        textPrimary = Color(0xFF111827),
        textSecondary = Color(0xFF4B5563),
        isLight = true
    ),
    OBSIDIAN_CHROME(
        id = "obsidian_chrome",
        title = "Obsidian Noir & Chrome",
        subtitle = "High-contrast monochrome with liquid silver and icy blue",
        background = Color(0xFF050507),
        surfaceCard = Color(0xFF111115),
        surfaceElevated = Color(0xFF1C1C22),
        primaryAccent = Color(0xFFE2E8F0),
        secondaryAccent = Color(0xFF38BDF8),
        tertiaryAccent = Color(0xFFA855F7),
        goldAccent = Color(0xFFCBD5E1),
        textPrimary = Color(0xFFFFFFFF),
        textSecondary = Color(0xFFA1A1AA)
    ),
    ROSEWOOD_DAMASK(
        id = "rosewood_damask",
        title = "Rosewood & Damask",
        subtitle = "Warm vintage burgundy velvet with dusty rose and bronze",
        background = Color(0xFF140A0F),
        surfaceCard = Color(0xFF24131C),
        surfaceElevated = Color(0xFF331C28),
        primaryAccent = Color(0xFFEC4899),
        secondaryAccent = Color(0xFFFB7185),
        tertiaryAccent = Color(0xFFC084FC),
        goldAccent = Color(0xFFF59E0B),
        textPrimary = Color(0xFFFFF1F2),
        textSecondary = Color(0xFFFDA4AF)
    );

    fun toAtelierColors(customAccent: Color? = null): AtelierColors {
        val primary = customAccent ?: this.primaryAccent
        val surfaceGlass = if (isLight) Color(0x0D000000) else Color(0x0DFFFFFF)
        val borderGlass = if (isLight) Color(0x26000000) else Color(0x1FFFFFFF)
        val textMuted = if (isLight) Color(0xFF9CA3AF) else Color(0xFF6B7280)
        val darkDivider = if (isLight) Color(0xFFE5E7EB) else Color(0xFF262636)

        return AtelierColors(
            background = this.background,
            surfaceCard = this.surfaceCard,
            surfaceElevated = this.surfaceElevated,
            surfaceGlass = surfaceGlass,
            borderGlass = borderGlass,
            primaryAccent = primary,
            secondaryAccent = this.secondaryAccent,
            tertiaryAccent = this.tertiaryAccent,
            goldAccent = this.goldAccent,
            emeraldAccent = Color(0xFF10B981),
            textPrimary = this.textPrimary,
            textSecondary = this.textSecondary,
            textMuted = textMuted,
            darkDivider = darkDivider,
            isLight = this.isLight
        )
    }
}

val LocalAtelierColors = compositionLocalOf {
    AtelierThemePreset.CYBER_LUMINESCENCE.toAtelierColors()
}
