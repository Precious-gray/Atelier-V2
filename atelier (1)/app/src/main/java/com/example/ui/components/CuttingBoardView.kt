package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CuttingEngine
import com.example.model.FabricBoltWidth
import com.example.model.MeasurementUnit
import com.example.model.PatternPieceSpec
import com.example.ui.theme.AtelierBackground
import com.example.ui.theme.AtelierBorderGlass
import com.example.ui.theme.AtelierSurfaceCard
import com.example.ui.theme.AtelierSurfaceElevated
import com.example.ui.theme.CyberGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CuttingBoardView(
    garmentTitle: String,
    category: String,
    silhouette: String,
    modifier: Modifier = Modifier
) {
    var selectedBoltWidth by remember { mutableStateOf(FabricBoltWidth.WIDTH_54) }
    var selectedUnit by remember { mutableStateOf(MeasurementUnit.CM) }
    var selectedPiece by remember { mutableStateOf<PatternPieceSpec?>(null) }

    val pieces = remember(garmentTitle, category, silhouette) {
        CuttingEngine.generatePiecesForGarment(garmentTitle, category, silhouette)
    }

    val autonestLayout = remember(pieces, selectedBoltWidth) {
        CuttingEngine.computeAutonest(pieces, selectedBoltWidth.widthCm)
    }

    val totalPiecesNeeded = remember(pieces) { pieces.sumOf { it.quantity } }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtelierSurfaceCard)
            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ContentCut,
                        contentDescription = "Cutting Board",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cutting Board & Autonest Layout",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "$totalPiecesNeeded Pattern Pieces Needed • Autonest Optimization",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            // Metric Unit Selector (CM / Inches)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(1.dp, AtelierBorderGlass, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selectedUnit == MeasurementUnit.CM) NeonCyan else Color.Transparent)
                        .clickable { selectedUnit = MeasurementUnit.CM }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("unit_selector_cm")
                ) {
                    Text(
                        "CM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedUnit == MeasurementUnit.CM) Color.Black else TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selectedUnit == MeasurementUnit.INCHES) NeonCyan else Color.Transparent)
                        .clickable { selectedUnit = MeasurementUnit.INCHES }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("unit_selector_in")
                ) {
                    Text(
                        "IN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedUnit == MeasurementUnit.INCHES) Color.Black else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bolt Width Chips & Efficiency KPI
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FabricBoltWidth.entries.forEach { widthOption ->
                    val isSelected = selectedBoltWidth == widthOption
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NeonViolet.copy(alpha = 0.25f) else Color.Transparent)
                            .border(1.dp, if (isSelected) NeonViolet else AtelierBorderGlass, RoundedCornerShape(8.dp))
                            .clickable { selectedBoltWidth = widthOption }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = widthOption.inchesLabel,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NeonViolet else TextSecondary
                        )
                    }
                }
            }

            // Efficiency Metric
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.1f", autonestLayout.efficiencyPercent)}% Efficiency",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = CyberGold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${String.format("%.2f", autonestLayout.lengthUsedCm / 100f)}m (${String.format("%.2f", (autonestLayout.lengthUsedCm / 100f) * 1.0936f)}yd)",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        val goldCol = CyberGold
        val magentaCol = NeonMagenta
        val cyanCol = NeonCyan

        // Cutting Board Canvas (Fabric Bolt Surface with Nested Pieces)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0A0F0D)) // Dark self-healing cutting mat green/black
                .border(1.5.dp, Color(0xFF1E3A2B), RoundedCornerShape(12.dp))
                .testTag("cutting_board_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxWidth().height(260.dp)) {
                val canvasW = size.width
                val canvasH = size.height

                // Draw Self-Healing Cutting Mat Grid
                val gridSpacing = 20f
                var xGrid = 0f
                while (xGrid < canvasW) {
                    drawLine(
                        color = Color(0xFF1B3B2B).copy(alpha = 0.6f),
                        start = Offset(xGrid, 0f),
                        end = Offset(xGrid, canvasH),
                        strokeWidth = if ((xGrid / gridSpacing).toInt() % 5 == 0) 1.2f else 0.5f
                    )
                    xGrid += gridSpacing
                }

                var yGrid = 0f
                while (yGrid < canvasH) {
                    drawLine(
                        color = Color(0xFF1B3B2B).copy(alpha = 0.6f),
                        start = Offset(0f, yGrid),
                        end = Offset(canvasW, yGrid),
                        strokeWidth = if ((yGrid / gridSpacing).toInt() % 5 == 0) 1.2f else 0.5f
                    )
                    yGrid += gridSpacing
                }

                // Fabric Selvedge Edge lines (Top & Bottom)
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(0f, 6f),
                    end = Offset(canvasW, 6f),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.3f),
                    start = Offset(0f, canvasH - 6f),
                    end = Offset(canvasW, canvasH - 6f),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f))
                )

                // Scale factor from real cm to canvas coordinates
                val scale = (canvasW - 40f) / selectedBoltWidth.widthCm

                autonestLayout.nestedPieces.forEach { nested ->
                    val px = 20f + (nested.xCm * scale)
                    val py = 12f + (nested.yCm * (scale * 0.7f)).coerceAtMost(canvasH - 60f)
                    val pw = (nested.widthCm * scale).coerceAtLeast(24f)
                    val ph = (nested.heightCm * (scale * 0.7f)).coerceAtLeast(30f).coerceAtMost(canvasH - py - 12f)

                    val isSelected = selectedPiece?.id == nested.piece.id
                    val pieceColor = if (isSelected) goldCol else when (nested.piece.grainline) {
                        "Bias 45°" -> magentaCol
                        else -> cyanCol
                    }

                    // Pattern Piece Body on Cutting Mat
                    drawRoundRect(
                        color = pieceColor.copy(alpha = if (isSelected) 0.35f else 0.18f),
                        topLeft = Offset(px, py),
                        size = Size(pw, ph),
                        cornerRadius = CornerRadius(6f, 6f)
                    )

                    // Seam Allowance Border
                    drawRoundRect(
                        color = pieceColor,
                        topLeft = Offset(px, py),
                        size = Size(pw, ph),
                        cornerRadius = CornerRadius(6f, 6f),
                        style = Stroke(width = if (isSelected) 2.5f else 1.2f)
                    )

                    // Grainline Arrow
                    val midX = px + pw / 2f
                    val topY = py + 8f
                    val botY = py + ph - 8f

                    if (botY > topY + 12f) {
                        drawLine(
                            color = pieceColor.copy(alpha = 0.8f),
                            start = Offset(midX, topY),
                            end = Offset(midX, botY),
                            strokeWidth = 1.2f
                        )
                        // Arrow heads
                        drawLine(
                            color = pieceColor.copy(alpha = 0.8f),
                            start = Offset(midX, topY),
                            end = Offset(midX - 4f, topY + 6f),
                            strokeWidth = 1.2f
                        )
                        drawLine(
                            color = pieceColor.copy(alpha = 0.8f),
                            start = Offset(midX, topY),
                            end = Offset(midX + 4f, topY + 6f),
                            strokeWidth = 1.2f
                        )
                    }

                    // Notches
                    for (n in 1..nested.piece.notchesCount.coerceAtMost(3)) {
                        val notchY = py + (ph * (n / (nested.piece.notchesCount + 1f)))
                        drawLine(
                            color = Color.White,
                            start = Offset(px - 3f, notchY),
                            end = Offset(px + 4f, notchY),
                            strokeWidth = 2f
                        )
                    }
                }
            }

            // Overlay mat label
            Text(
                text = "HEALING CUTTING MAT • ${selectedBoltWidth.inchesLabel} SELVEDGE",
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF2E6B4A),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pattern Pieces List & Metric Directives
        Text(
            text = "Pattern Pieces & Drafting Directives ($totalPiecesNeeded Pieces Total)",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            pieces.forEach { piece ->
                val isSelected = selectedPiece?.id == piece.id
                val widthVal = if (selectedUnit == MeasurementUnit.CM) "${piece.widthCm.toInt()} cm" else "${String.format("%.1f", piece.widthCm / 2.54f)} in"
                val heightVal = if (selectedUnit == MeasurementUnit.CM) "${piece.heightCm.toInt()} cm" else "${String.format("%.1f", piece.heightCm / 2.54f)} in"
                val seamVal = if (selectedUnit == MeasurementUnit.CM) "${piece.seamAllowanceCm} cm" else "${String.format("%.2f", piece.seamAllowanceCm / 2.54f)} in"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonViolet.copy(alpha = 0.15f) else AtelierSurfaceElevated)
                        .border(1.dp, if (isSelected) NeonViolet else AtelierBorderGlass, RoundedCornerShape(10.dp))
                        .clickable {
                            selectedPiece = if (isSelected) null else piece
                        }
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(NeonCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Cut ${piece.quantity}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = piece.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }

                            Text(
                                text = "$widthVal × $heightVal",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = CyberGold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Grain: ${piece.grainline} • Seam: $seamVal • ${piece.notchesCount} Notches",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        // Drafting directives details
                        Spacer(modifier = Modifier.height(4.dp))
                        piece.directives.forEach { directive ->
                            Text(
                                text = "• $directive",
                                fontSize = 10.sp,
                                color = TextMuted,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
