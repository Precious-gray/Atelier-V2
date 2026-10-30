package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled._3dRotation
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterCenterFocus
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AtelierBackground
import com.example.ui.theme.AtelierBorderGlass
import com.example.ui.theme.AtelierSurfaceCard
import com.example.ui.theme.CyberGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CroquisAnatomicalView(
    garmentTitle: String,
    silhouette: String,
    category: String,
    modifier: Modifier = Modifier
) {
    var is3DMode by remember { mutableStateOf(false) }
    var rotationAngleDeg by remember { mutableFloatStateOf(0f) }
    var showDrapeLayer by remember { mutableStateOf(true) }
    var showFitCheckpoints by remember { mutableStateOf(true) }
    var showBalanceGrid by remember { mutableStateOf(true) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngleDeg,
        animationSpec = tween(durationMillis = 200),
        label = "croquis_rotation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtelierSurfaceCard)
            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Top Header with 2D / 3D Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Accessibility,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (is3DMode) "3D Dynamic Wireframe Croquis" else "2D Technical Flat Croquis",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "Anatomical fit analysis • Drag to rotate perspective",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            IconButton(
                onClick = { is3DMode = !is3DMode },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (is3DMode) NeonViolet.copy(alpha = 0.25f) else Color.Transparent)
                    .border(1.dp, if (is3DMode) NeonViolet else AtelierBorderGlass, CircleShape)
                    .testTag("toggle_3d_mode_button")
            ) {
                Icon(
                    Icons.Default._3dRotation,
                    contentDescription = "Toggle 3D perspective",
                    tint = if (is3DMode) NeonViolet else TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        val cyanCol = NeonCyan
        val violetCol = NeonViolet
        val goldCol = CyberGold
        val magentaCol = NeonMagenta

        // Interactive Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF07070B))
                .border(1.dp, AtelierBorderGlass.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .pointerInput(is3DMode) {
                    if (is3DMode) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            rotationAngleDeg = (rotationAngleDeg + dragAmount.x * 0.5f).coerceIn(-60f, 60f)
                        }
                    }
                }
                .testTag("croquis_interactive_canvas")
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val centerX = w / 2f
                val rotFactor = (animatedRotation / 60f) * (w * 0.15f)

                // 1. Metric Balance Grid Layer
                if (showBalanceGrid) {
                    val gridGap = 28f
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 8f), 0f)

                    // Vertical Center Front Line (Plumb line)
                    drawLine(
                        color = cyanCol.copy(alpha = 0.25f),
                        start = Offset(centerX, 20f),
                        end = Offset(centerX, h - 20f),
                        strokeWidth = 1.5f,
                        pathEffect = dashEffect
                    )

                    // Horizontal Landmark Reference Lines
                    val landmarks = listOf(
                        "Shoulder Plane" to 70f,
                        "Bust Apex" to 115f,
                        "Underbust" to 140f,
                        "Natural Waist" to 175f,
                        "High Hip" to 205f,
                        "Low Hip / Seat" to 235f,
                        "Knee Line" to 290f
                    )
                    landmarks.forEach { (label, y) ->
                        drawLine(
                            color = Color.White.copy(alpha = 0.08f),
                            start = Offset(30f, y),
                            end = Offset(w - 30f, y),
                            strokeWidth = 1f,
                            pathEffect = dashEffect
                        )
                    }
                }

                // 2. Anatomical Mannequin Form (Wireframe or Contour)
                val bodyPath = Path().apply {
                    val shift = if (is3DMode) rotFactor else 0f

                    // Head & Neck
                    moveTo(centerX + shift * 0.2f, 30f)
                    // Neck to Left Shoulder
                    lineTo(centerX - 46f + shift * 0.4f, 70f)
                    // Left Armscye / Underarm
                    quadraticTo(
                        centerX - 40f + shift * 0.5f, 95f,
                        centerX - 36f + shift * 0.6f, 115f
                    )
                    // Bust curve to Natural Waist cinch
                    quadraticTo(
                        centerX - 42f + shift * 0.7f, 135f,
                        centerX - 24f + shift * 0.8f, 175f
                    )
                    // Waist to High & Low Hip curve
                    quadraticTo(
                        centerX - 44f + shift * 0.9f, 215f,
                        centerX - 40f + shift, 240f
                    )
                    // Thigh down to Knee
                    lineTo(centerX - 24f + shift, 290f)
                    lineTo(centerX - 12f + shift, h - 35f)

                    // Center fork / crotch
                    lineTo(centerX, 260f)

                    // Right Leg
                    lineTo(centerX + 12f - shift, h - 35f)
                    lineTo(centerX + 24f - shift, 290f)
                    // Right Hip
                    quadraticTo(
                        centerX + 40f - shift, 240f,
                        centerX + 44f - shift * 0.9f, 215f
                    )
                    // Right Waist
                    quadraticTo(
                        centerX + 24f - shift * 0.8f, 175f,
                        centerX + 42f - shift * 0.7f, 135f
                    )
                    // Right Bust to Underarm
                    quadraticTo(
                        centerX + 36f - shift * 0.6f, 115f,
                        centerX + 40f - shift * 0.5f, 95f
                    )
                    // Right Shoulder
                    lineTo(centerX + 46f - shift * 0.4f, 70f)
                    close()
                }

                // Fill silhouette subtle glow
                drawPath(
                    path = bodyPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            violetCol.copy(alpha = 0.12f),
                            cyanCol.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )

                // Outline Body Form
                drawPath(
                    path = bodyPath,
                    color = if (is3DMode) violetCol.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.5f),
                    style = Stroke(width = if (is3DMode) 1.5f else 1.2f)
                )

                // 3D Perspective Cross-Section Contours
                if (is3DMode) {
                    val levels = listOf(70f, 115f, 140f, 175f, 210f, 240f)
                    levels.forEach { y ->
                        val curveDepth = (sin((y / h) * Math.PI).toFloat()) * (rotationAngleDeg * 0.25f)
                        val contourPath = Path().apply {
                            moveTo(centerX - 40f, y)
                            quadraticTo(centerX, y + curveDepth, centerX + 40f, y)
                        }
                        drawPath(
                            path = contourPath,
                            color = cyanCol.copy(alpha = 0.35f),
                            style = Stroke(width = 1f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3f, 5f)))
                        )
                    }
                }

                // 3. Garment Drape Contour Layer (Silhouette-specific)
                if (showDrapeLayer) {
                    val drapePath = Path()
                    val shift = if (is3DMode) rotFactor else 0f

                    when {
                        category.contains("CORSET", ignoreCase = true) || silhouette.contains("CORSET", ignoreCase = true) -> {
                            // Corset boning channels & cinch lines
                            drapePath.moveTo(centerX - 38f + shift * 0.6f, 110f)
                            drapePath.lineTo(centerX - 22f + shift * 0.8f, 175f)
                            drapePath.lineTo(centerX - 35f + shift, 205f)
                            drapePath.lineTo(centerX, 215f)
                            drapePath.lineTo(centerX + 35f - shift, 205f)
                            drapePath.lineTo(centerX + 22f - shift * 0.8f, 175f)
                            drapePath.lineTo(centerX + 38f - shift * 0.6f, 110f)
                            drapePath.close()

                            // Boning structural vertical stitches
                            for (b in -2..2) {
                                val xOff = b * 12f
                                drawLine(
                                    color = goldCol.copy(alpha = 0.85f),
                                    start = Offset(centerX + xOff + shift * 0.6f, 118f),
                                    end = Offset(centerX + xOff * 0.7f + shift * 0.8f, 195f),
                                    strokeWidth = 2f
                                )
                            }
                        }
                        category.contains("TROUSER", ignoreCase = true) || silhouette.contains("TROUSER", ignoreCase = true) -> {
                            // Tailored trouser crease and drape
                            drawLine(
                                color = cyanCol.copy(alpha = 0.9f),
                                start = Offset(centerX - 24f + shift, 175f),
                                end = Offset(centerX - 20f + shift, h - 35f),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = cyanCol.copy(alpha = 0.9f),
                                start = Offset(centerX + 24f - shift, 175f),
                                end = Offset(centerX + 20f - shift, h - 35f),
                                strokeWidth = 2f
                            )
                        }
                        else -> {
                            // Haute Couture Sculptural Gown drape cascade
                            drapePath.moveTo(centerX - 46f + shift * 0.4f, 70f)
                            drapePath.quadraticTo(centerX - 55f + shift * 0.5f, 130f, centerX - 24f + shift * 0.8f, 175f)
                            drapePath.quadraticTo(centerX - 70f + shift, 240f, centerX - 90f + shift, h - 30f)
                            drapePath.lineTo(centerX + 90f - shift, h - 30f)
                            drapePath.quadraticTo(centerX + 70f - shift, 240f, centerX + 24f - shift * 0.8f, 175f)
                            drapePath.quadraticTo(centerX + 55f - shift * 0.5f, 130f, centerX + 46f - shift * 0.4f, 70f)
                            drapePath.close()

                            // Fluid cascade drape folds
                            val fold1 = Path().apply {
                                moveTo(centerX - 10f + shift * 0.8f, 175f)
                                quadraticTo(centerX - 35f + shift, 240f, centerX - 45f + shift, h - 30f)
                            }
                            val fold2 = Path().apply {
                                moveTo(centerX + 10f - shift * 0.8f, 175f)
                                quadraticTo(centerX + 35f - shift, 240f, centerX + 45f - shift, h - 30f)
                            }
                            drawPath(fold1, color = magentaCol.copy(alpha = 0.7f), style = Stroke(width = 1.8f))
                            drawPath(fold2, color = magentaCol.copy(alpha = 0.7f), style = Stroke(width = 1.8f))
                        }
                    }

                    drawPath(
                        path = drapePath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                cyanCol.copy(alpha = 0.25f),
                                violetCol.copy(alpha = 0.15f),
                                Color.Transparent
                            )
                        )
                    )
                    drawPath(
                        path = drapePath,
                        color = cyanCol,
                        style = Stroke(width = 2f)
                    )
                }

                // 4. Fit Checkpoint Indicators (Apex, Cinch, Ease)
                if (showFitCheckpoints) {
                    val shift = if (is3DMode) rotFactor else 0f

                    // Left & Right Bust Apex (Fit check: Green balanced)
                    drawCircle(
                        color = Color(0xFF00E676),
                        radius = 4.5f,
                        center = Offset(centerX - 16f + shift * 0.6f, 115f)
                    )
                    drawCircle(
                        color = Color(0xFF00E676),
                        radius = 4.5f,
                        center = Offset(centerX + 16f - shift * 0.6f, 115f)
                    )

                    // Waist Cinch (Yellow = Couture snug 2cm ease)
                    drawCircle(
                        color = goldCol,
                        radius = 4.5f,
                        center = Offset(centerX - 24f + shift * 0.8f, 175f)
                    )
                    drawCircle(
                        color = goldCol,
                        radius = 4.5f,
                        center = Offset(centerX + 24f - shift * 0.8f, 175f)
                    )

                    // Shoulder Point (Green = Clean balance line)
                    drawCircle(
                        color = Color(0xFF00E676),
                        radius = 4.5f,
                        center = Offset(centerX - 46f + shift * 0.4f, 70f)
                    )
                    drawCircle(
                        color = Color(0xFF00E676),
                        radius = 4.5f,
                        center = Offset(centerX + 46f - shift * 0.4f, 70f)
                    )
                }
            }

            // Overlay Badge & Angle readout
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .border(1.dp, AtelierBorderGlass, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (is3DMode) "3D Wireframe • ${animatedRotation.toInt()}°" else "2D CAD Flat • Front View",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonCyan
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Layer Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = showDrapeLayer,
                onClick = { showDrapeLayer = !showDrapeLayer },
                label = { Text("Drape Contour", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                    selectedLabelColor = NeonCyan
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = showDrapeLayer,
                    selectedBorderColor = NeonCyan,
                    borderColor = AtelierBorderGlass
                ),
                modifier = Modifier.testTag("toggle_drape_layer")
            )

            FilterChip(
                selected = showFitCheckpoints,
                onClick = { showFitCheckpoints = !showFitCheckpoints },
                label = { Text("Fit Checkpoints", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = CyberGold.copy(alpha = 0.2f),
                    selectedLabelColor = CyberGold
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = showFitCheckpoints,
                    selectedBorderColor = CyberGold,
                    borderColor = AtelierBorderGlass
                ),
                modifier = Modifier.testTag("toggle_fit_layer")
            )

            FilterChip(
                selected = showBalanceGrid,
                onClick = { showBalanceGrid = !showBalanceGrid },
                label = { Text("Balance Grid", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonViolet.copy(alpha = 0.2f),
                    selectedLabelColor = NeonViolet
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = showBalanceGrid,
                    selectedBorderColor = NeonViolet,
                    borderColor = AtelierBorderGlass
                ),
                modifier = Modifier.testTag("toggle_grid_layer")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fit Check Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Balanced Ease (+2.5cm)", fontSize = 11.sp, color = TextSecondary)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(CyberGold)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Snug Cinch (1.0cm)", fontSize = 11.sp, color = TextSecondary)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonMagenta)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("True Bias Drape", fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}
