package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColorHarmonyMode
import com.example.model.ColorStudioEngine
import com.example.model.GarmentColorZone
import com.example.ui.theme.AtelierBackground
import com.example.ui.theme.AtelierBorderGlass
import com.example.ui.theme.AtelierSurfaceCard
import com.example.ui.theme.AtelierSurfaceElevated
import com.example.ui.theme.CyberGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ColorStudioView(
    modifier: Modifier = Modifier
) {
    var baseHue by remember { mutableFloatStateOf(270f) } // Default electric violet
    var selectedHarmony by remember { mutableStateOf(ColorHarmonyMode.COMPLEMENTARY) }

    val palette = remember(baseHue, selectedHarmony) {
        ColorStudioEngine.generatePalette(baseHue, selectedHarmony)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtelierSurfaceCard)
            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = "Color Studio",
                        tint = CyberGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chromatic Garment Color Studio",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "Harmonious chromatic wheel for garment fabrication",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            // Hue Angle Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.dp, AtelierBorderGlass, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${baseHue.toInt()}° Hue",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = CyberGold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Harmony Modes Horizontal Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ColorHarmonyMode.entries.forEach { mode ->
                val isSelected = selectedHarmony == mode
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedHarmony = mode },
                    label = { Text(mode.label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CyberGold.copy(alpha = 0.2f),
                        selectedLabelColor = CyberGold
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        selectedBorderColor = CyberGold,
                        borderColor = AtelierBorderGlass
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chromatic Color Wheel Canvas + Interactive Pointer
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val center = Offset(70f * density, 70f * density)
                            val dx = offset.x - center.x
                            val dy = offset.y - center.y
                            var angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
                            if (angle < 0) angle += 360f
                            baseHue = angle
                        }
                    }
                    .testTag("chromatic_color_wheel")
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val radius = size.minDimension / 2f
                    val center = Offset(size.width / 2f, size.height / 2f)

                    // Draw 360 segments of color wheel
                    for (i in 0 until 360 step 3) {
                        val rad = Math.toRadians(i.toDouble())
                        val endX = center.x + radius * cos(rad).toFloat()
                        val endY = center.y + radius * sin(rad).toFloat()
                        val color = Color.hsv(i.toFloat(), 0.9f, 0.95f)
                        drawLine(
                            color = color,
                            start = center,
                            end = Offset(endX, endY),
                            strokeWidth = 4f
                        )
                    }

                    // Inner donut hole for modern aesthetic
                    drawCircle(
                        color = Color(0xFF0F0F16),
                        radius = radius * 0.45f,
                        center = center
                    )

                    // Selected Hue Pointer Circle
                    val radSel = Math.toRadians(baseHue.toDouble())
                    val ptrX = center.x + (radius * 0.72f) * cos(radSel).toFloat()
                    val ptrY = center.y + (radius * 0.72f) * sin(radSel).toFloat()

                    drawCircle(
                        color = Color.White,
                        radius = 8f,
                        center = Offset(ptrX, ptrY)
                    )
                    drawCircle(
                        color = Color.Black,
                        radius = 5f,
                        center = Offset(ptrX, ptrY)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Harmony Explanation
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = selectedHarmony.label,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = selectedHarmony.description,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Slider(
                    value = baseHue,
                    onValueChange = { baseHue = it },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(
                        thumbColor = CyberGold,
                        activeTrackColor = CyberGold.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("hue_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Garment Construction Zone Swatches (Shell, Lining, Facing, Piping, Hardware, Thread)
        Text(
            text = "Fabric Construction Swatches & Zone Assignment",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            GarmentColorZone.entries.forEach { zone ->
                val assignment = palette.zones[zone]
                if (assignment != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AtelierSurfaceElevated)
                            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Color swatch circle
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(assignment.color)
                                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = zone.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = zone.role,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = assignment.hexCode,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberGold
                                )
                                Text(
                                    text = assignment.pantoneName,
                                    fontSize = 9.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
