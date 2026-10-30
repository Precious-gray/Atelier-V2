package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Straighten
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CuttingEngine
import com.example.model.FabricBoltWidth
import com.example.model.FabricNapOption
import com.example.ui.theme.AtelierBorderGlass
import com.example.ui.theme.AtelierSurfaceCard
import com.example.ui.theme.AtelierSurfaceElevated
import com.example.ui.theme.CyberGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun FabricEstimatorView(
    garmentTitle: String,
    category: String,
    silhouette: String,
    modifier: Modifier = Modifier
) {
    var selectedWidth by remember { mutableStateOf(FabricBoltWidth.WIDTH_54) }
    var selectedNap by remember { mutableStateOf(FabricNapOption.STANDARD) }
    var shrinkagePercent by remember { mutableFloatStateOf(5.0f) }

    val pieces = remember(garmentTitle, category, silhouette) {
        CuttingEngine.generatePiecesForGarment(garmentTitle, category, silhouette)
    }

    val estimate = remember(pieces, selectedWidth, selectedNap, shrinkagePercent) {
        CuttingEngine.calculateEstimate(pieces, selectedWidth, selectedNap, shrinkagePercent)
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
                        Icons.Default.Calculate,
                        contentDescription = "Fabric Estimator",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Couture Fabric Yardage Estimator",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "Calculated from pattern piece dimensions, nap, and shrinkage",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Estimation Card (Big Readouts)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0F141C))
                .border(1.5.dp, NeonCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL FABRIC NEEDED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.2f", estimate.lengthYards),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = TextPrimary
                        )
                        Text(
                            text = " YDS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyberGold,
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "(${String.format("%.2f", estimate.lengthMeters)} m)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Monospace,
                            color = TextSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }

                // Waste buffer percentage
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Cutting Waste",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Text(
                        text = "${String.format("%.1f", estimate.wastePercent)}%",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (estimate.wastePercent < 15f) Color(0xFF00E676) else CyberGold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Bolt Width Selector
        Text("Standard Fabric Bolt Width:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FabricBoltWidth.entries.forEach { widthOption ->
                val isSelected = selectedWidth == widthOption
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else AtelierSurfaceElevated)
                        .border(1.dp, if (isSelected) NeonCyan else AtelierBorderGlass, RoundedCornerShape(8.dp))
                        .clickable { selectedWidth = widthOption }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = widthOption.inchesLabel,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonCyan else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Nap / Grainline Options
        Text("Grainline & Nap Requirement:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(6.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FabricNapOption.entries.forEach { nap ->
                val isSelected = selectedNap == nap
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonViolet.copy(alpha = 0.18f) else AtelierSurfaceElevated)
                        .border(1.dp, if (isSelected) NeonViolet else AtelierBorderGlass, RoundedCornerShape(8.dp))
                        .clickable { selectedNap = nap }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nap.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                        if (nap.extraAllowanceFactor > 1.0f) {
                            Text(
                                text = "+${((nap.extraAllowanceFactor - 1f) * 100).toInt()}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberGold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Pre-shrinkage allowance slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Washing Pre-Shrinkage Buffer:", fontSize = 11.sp, color = TextSecondary)
            Text(
                "${shrinkagePercent.toInt()}% Allowance",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
        }
        Slider(
            value = shrinkagePercent,
            onValueChange = { shrinkagePercent = it },
            valueRange = 0f..10f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan.copy(alpha = 0.7f)
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Atelier Cutting Directive Advice Callout
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(AtelierSurfaceElevated)
                .border(1.dp, AtelierBorderGlass, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            Row {
                Icon(
                    Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = CyberGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = estimate.cuttingDirectiveSummary,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
