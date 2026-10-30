package com.example.model

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

enum class ColorHarmonyMode(val label: String, val description: String) {
    COMPLEMENTARY("Complementary", "Exact opposite on color wheel (+180°) creates electric high-contrast couture tension."),
    ANALOGOUS("Analogous", "Adjacent 30° hues create organic, gradient fluid transitions across folds."),
    TRIADIC("Triadic", "Three equally spaced hues (120°) providing balanced, dynamic runway presence."),
    SPLIT_COMPLEMENTARY("Split Complementary", "Base hue with two adjacent colors to its complement (+150°, +210°)."),
    MONOCHROMATIC("Monochromatic", "Single hue varied through saturation and value depths for tonal elegance."),
    TETRADIC("Tetradic (Square)", "Two complementary pairs (90° steps) for rich multi-fabric layering.")
}

enum class GarmentColorZone(val title: String, val role: String) {
    SHELL("Shell Fabric", "Main outer body drape"),
    LINING("Lining & Interfacing", "Interior silk organza or cupro"),
    FACING("Contrast Facing", "Lapels, cuffs, collar reverses"),
    PIPING("Piping & Binding", "Structural edge piping & bias trim"),
    HARDWARE("Metallic Hardware", "Boning eyelets, zippers, buckles"),
    THREAD("Topstitch Thread", "Contrast or tonal couture stitching")
}

data class ColorZoneAssignment(
    val zone: GarmentColorZone,
    val color: Color,
    val hexCode: String,
    val pantoneName: String
)

data class ColorStudioPalette(
    val baseHue: Float,
    val harmonyMode: ColorHarmonyMode,
    val zones: Map<GarmentColorZone, ColorZoneAssignment>
)

object ColorStudioEngine {

    fun generatePalette(baseHue: Float, harmonyMode: ColorHarmonyMode): ColorStudioPalette {
        val h = (baseHue % 360f + 360f) % 360f

        val colorList = when (harmonyMode) {
            ColorHarmonyMode.COMPLEMENTARY -> {
                val comp = (h + 180f) % 360f
                listOf(
                    hsvToColor(h, 0.85f, 0.90f),
                    hsvToColor(h, 0.35f, 0.95f),
                    hsvToColor(comp, 0.80f, 0.85f),
                    hsvToColor(comp, 0.95f, 0.70f),
                    hsvToColor((h + 45f) % 360f, 0.20f, 0.80f),
                    hsvToColor(comp, 0.90f, 0.95f)
                )
            }
            ColorHarmonyMode.ANALOGOUS -> {
                listOf(
                    hsvToColor(h, 0.85f, 0.90f),
                    hsvToColor((h + 25f) % 360f, 0.60f, 0.85f),
                    hsvToColor((h - 25f + 360f) % 360f, 0.80f, 0.80f),
                    hsvToColor((h + 50f) % 360f, 0.90f, 0.75f),
                    hsvToColor(h, 0.15f, 0.90f),
                    hsvToColor((h + 15f) % 360f, 0.95f, 0.95f)
                )
            }
            ColorHarmonyMode.TRIADIC -> {
                val t1 = (h + 120f) % 360f
                val t2 = (h + 240f) % 360f
                listOf(
                    hsvToColor(h, 0.85f, 0.90f),
                    hsvToColor(t1, 0.40f, 0.90f),
                    hsvToColor(t1, 0.85f, 0.85f),
                    hsvToColor(t2, 0.90f, 0.80f),
                    hsvToColor(t2, 0.25f, 0.75f),
                    hsvToColor(h, 0.95f, 0.95f)
                )
            }
            ColorHarmonyMode.SPLIT_COMPLEMENTARY -> {
                val s1 = (h + 150f) % 360f
                val s2 = (h + 210f) % 360f
                listOf(
                    hsvToColor(h, 0.85f, 0.90f),
                    hsvToColor(s1, 0.35f, 0.92f),
                    hsvToColor(s1, 0.80f, 0.85f),
                    hsvToColor(s2, 0.85f, 0.80f),
                    hsvToColor((h + 30f) % 360f, 0.20f, 0.80f),
                    hsvToColor(s2, 0.95f, 0.95f)
                )
            }
            ColorHarmonyMode.MONOCHROMATIC -> {
                listOf(
                    hsvToColor(h, 0.85f, 0.88f),
                    hsvToColor(h, 0.30f, 0.95f),
                    hsvToColor(h, 0.95f, 0.60f),
                    hsvToColor(h, 0.90f, 0.40f),
                    hsvToColor(h, 0.15f, 0.80f),
                    hsvToColor(h, 0.70f, 1.0f)
                )
            }
            ColorHarmonyMode.TETRADIC -> {
                val q1 = (h + 90f) % 360f
                val q2 = (h + 180f) % 360f
                val q3 = (h + 270f) % 360f
                listOf(
                    hsvToColor(h, 0.85f, 0.90f),
                    hsvToColor(q1, 0.40f, 0.92f),
                    hsvToColor(q2, 0.80f, 0.85f),
                    hsvToColor(q3, 0.85f, 0.80f),
                    hsvToColor(q1, 0.20f, 0.80f),
                    hsvToColor(q2, 0.95f, 0.95f)
                )
            }
        }
        val c1 = colorList[0]
        val c2 = colorList[1]
        val c3 = colorList[2]
        val c4 = colorList[3]
        val c5 = colorList[4]
        val c6 = colorList[5]

        val map = mapOf(
            GarmentColorZone.SHELL to ColorZoneAssignment(GarmentColorZone.SHELL, c1, toHex(c1), getCoutureName(h, "Shell")),
            GarmentColorZone.LINING to ColorZoneAssignment(GarmentColorZone.LINING, c2, toHex(c2), getCoutureName((h + 40f) % 360f, "Silk")),
            GarmentColorZone.FACING to ColorZoneAssignment(GarmentColorZone.FACING, c3, toHex(c3), getCoutureName((h + 120f) % 360f, "Satin")),
            GarmentColorZone.PIPING to ColorZoneAssignment(GarmentColorZone.PIPING, c4, toHex(c4), getCoutureName((h + 180f) % 360f, "Cord")),
            GarmentColorZone.HARDWARE to ColorZoneAssignment(GarmentColorZone.HARDWARE, c5, toHex(c5), "Titanium Chrome"),
            GarmentColorZone.THREAD to ColorZoneAssignment(GarmentColorZone.THREAD, c6, toHex(c6), "Gutterman Topstitch")
        )

        return ColorStudioPalette(
            baseHue = h,
            harmonyMode = harmonyMode,
            zones = map
        )
    }

    private fun hsvToColor(hue: Float, saturation: Float, value: Float): Color {
        val hPrime = (hue % 360f) / 60f
        val c = value * saturation
        val x = c * (1f - kotlin.math.abs((hPrime % 2f) - 1f))
        val m = value - c

        val (r1, g1, b1) = when {
            hPrime < 1f -> Triple(c, x, 0f)
            hPrime < 2f -> Triple(x, c, 0f)
            hPrime < 3f -> Triple(0f, c, x)
            hPrime < 4f -> Triple(0f, x, c)
            hPrime < 5f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        return Color(
            red = (r1 + m).coerceIn(0f, 1f),
            green = (g1 + m).coerceIn(0f, 1f),
            blue = (b1 + m).coerceIn(0f, 1f),
            alpha = 1f
        )
    }

    private fun toHex(color: Color): String {
        val r = (color.red * 255).roundToInt()
        val g = (color.green * 255).roundToInt()
        val b = (color.blue * 255).roundToInt()
        return String.format("#%02X%02X%02X", r, g, b)
    }

    private fun getCoutureName(hue: Float, suffix: String): String {
        return when ((hue / 30f).toInt()) {
            0, 12 -> "Crimson $suffix"
            1 -> "Coral Ochre $suffix"
            2 -> "Solar Amber $suffix"
            3 -> "Chartreuse $suffix"
            4 -> "Emerald Viridian $suffix"
            5 -> "Teal Opal $suffix"
            6 -> "Cyan Biolum $suffix"
            7 -> "Cobalt Azure $suffix"
            8 -> "Ultramarine $suffix"
            9 -> "Electric Violet $suffix"
            10 -> "Magenta Orchid $suffix"
            11 -> "Rose Quartz $suffix"
            else -> "Couture $suffix"
        }
    }
}
