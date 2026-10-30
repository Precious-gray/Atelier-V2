package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiPlatform
import com.example.model.AiResearchEngine
import com.example.model.AiResearchTarget
import com.example.model.Garment
import com.example.model.PatternDraft
import com.example.ui.theme.AtelierBackground
import com.example.ui.theme.AtelierBorderGlass
import com.example.ui.theme.AtelierSurfaceCard
import com.example.ui.theme.AtelierSurfaceElevated
import com.example.ui.theme.CyberGold
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanDark
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatternDraftingSheet(
    targetGarment: Garment? = null,
    availableGarments: List<Garment> = emptyList(),
    initialPattern: PatternDraft = PatternDraft(),
    onDismiss: () -> Unit,
    onSavePattern: (PatternDraft) -> Unit = {}
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var activeGarment by remember(targetGarment, availableGarments) {
        mutableStateOf(targetGarment ?: availableGarments.firstOrNull())
    }

    var currentPattern by remember { mutableStateOf(initialPattern) }
    var patternName by remember(activeGarment) {
        mutableStateOf(activeGarment?.let { "${it.title} Sloper" } ?: initialPattern.name.ifBlank { "Custom Couture Sloper" })
    }
    var easeAllowance by remember { mutableFloatStateOf(initialPattern.easeAllowance) }
    var selectedDart by remember { mutableStateOf(initialPattern.dartPosition) }
    var selectedSeamAllowance by remember { mutableFloatStateOf(initialPattern.seamAllowance) }
    var selectedGrainline by remember { mutableStateOf(initialPattern.grainline) }
    var notes by remember { mutableStateOf(initialPattern.notes) }

    val presetTemplates = listOf(
        "Standard Bodice Sloper",
        "Sculpted Corset Sloper",
        "Architectural Blazer Sloper",
        "Bias-Cut Flared Gown",
        "Tailored Trouser Sloper",
        "A-Line Skirt Sloper",
        "Raglan Sculptural Sleeve",
        "Origami Asymmetric Collar"
    )

    val dartOptions = listOf("Princess Seam", "French Dart", "Waist Dart", "Underarm Dart", "Zero Dart / Draped")
    val grainlineOptions = listOf("Straight Grain", "True Bias (45°)", "Crosswise Weft")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AtelierBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .border(1.dp, NeonCyan, CircleShape)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_atelier_scissors_logo_1792658287419),
                            contentDescription = "Dressmaker Scissors logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PATTERN DRAFTING",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Parametric Sloper & Cutting Geometry",
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Chosen Garment Info Card & Selector
            if (activeGarment != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AtelierSurfaceCard)
                        .border(1.dp, NeonCyan, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.BookmarkAdded, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("DRAFTING PATTERN FOR CHOSEN GARMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(NeonViolet)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(activeGarment!!.category, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(activeGarment!!.title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("${activeGarment!!.silhouette} • ${activeGarment!!.fabric}", fontSize = 11.sp, color = TextSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            if (availableGarments.isNotEmpty()) {
                Text("CHOOSE GARMENT TO DRAFT PATTERN FOR", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableGarments) { g ->
                        val isSelected = activeGarment?.id == g.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                activeGarment = g
                                patternName = "${g.title} Sloper"
                            },
                            label = { Text(g.title, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan,
                                selectedLabelColor = AtelierBackground,
                                containerColor = AtelierSurfaceElevated,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 17 AI Pattern Drafting Platforms & Tutorials Launchers
            Text(
                text = "17 AI PATTERN DRAFTING PLATFORMS & TUTORIALS (CLICK TO OPEN APP/WEB)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Quick launch horizontal row for all 17 platforms
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(AiPlatform.entries) { platform ->
                    val promptObj = remember(platform, patternName, selectedDart, selectedGrainline) {
                        AiResearchEngine.buildPrompt(
                            platform = platform,
                            target = AiResearchTarget.TEXTILE_PATTERN,
                            garmentTitle = patternName,
                            silhouette = "$selectedDart Sloper ($selectedGrainline)",
                            fabric = "Pattern Drafting Muslin",
                            selectedTags = setOf("Metric Sloper Drafting", selectedDart)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(AtelierSurfaceElevated)
                            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(10.dp))
                            .clickable { openWebLink(context, promptObj.searchUrl) }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("pattern_platform_${platform.name.lowercase()}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(NeonViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = platform.iconKey,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = platform.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = "Launch Platform",
                                tint = NeonCyan,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Expandable or Featured Platform Cards with Prompt Copying & Launching
            var showAllPlatformsList by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FEATURED PATTERN STUDIO LAUNCHERS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 0.5.sp
                )
                TextButton(
                    onClick = { showAllPlatformsList = !showAllPlatformsList },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = if (showAllPlatformsList) "Collapse List" else "View All 17 Platforms",
                        fontSize = 11.sp,
                        color = CyberGold,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            val displayedPlatforms = if (showAllPlatformsList) AiPlatform.entries else listOf(
                AiPlatform.PATRON_IA,
                AiPlatform.AICLO,
                AiPlatform.YOUTUBE,
                AiPlatform.PINTEREST,
                AiPlatform.CLAUDE,
                AiPlatform.RECRAFT_AI
            )

            displayedPlatforms.forEach { platform ->
                val promptObj = remember(platform, patternName, selectedDart, selectedGrainline) {
                    AiResearchEngine.buildPrompt(
                        platform = platform,
                        target = AiResearchTarget.TEXTILE_PATTERN,
                        garmentTitle = patternName,
                        silhouette = "$selectedDart Sloper ($selectedGrainline)",
                        fabric = "Pattern Drafting Muslin",
                        selectedTags = setOf("Parametric Cutting", selectedDart)
                    )
                }

                FrostedGlassBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    borderColor = NeonCyan.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(NeonViolet.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = platform.iconKey,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = platform.displayName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = platform.promptFormatDescription,
                                        fontSize = 10.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Pattern Prompt (${platform.displayName})", promptObj.formattedPrompt))
                                        Toast.makeText(context, "Copied prompt for ${platform.displayName}", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Prompt",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                Button(
                                    onClick = { openWebLink(context, promptObj.searchUrl) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInBrowser,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Open Link",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(AtelierBackground)
                                .clickable { openWebLink(context, promptObj.searchUrl) }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = promptObj.formattedPrompt,
                                fontSize = 10.sp,
                                color = CyberGold,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Preset Sloper Selector
            Text(
                text = "CHANGE BASE PATTERN SLOPER",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presetTemplates) { templateName ->
                    val isSelected = patternName == templateName
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            patternName = templateName
                            currentPattern = currentPattern.copy(name = templateName)
                        },
                        label = {
                            Text(
                                text = templateName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyanDark,
                            containerColor = AtelierSurfaceElevated
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) NeonCyan else AtelierBorderGlass
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Interactive Pattern Geometry Canvas
            FrostedGlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                borderColor = NeonCyan.copy(alpha = 0.4f)
            ) {
                PatternPreviewCanvas(
                    patternName = patternName,
                    easeAllowance = easeAllowance,
                    dartPosition = selectedDart,
                    grainline = selectedGrainline
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Pattern Name Input
            OutlinedTextField(
                value = patternName,
                onValueChange = { patternName = it },
                label = { Text("Pattern Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("pattern_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = AtelierBorderGlass,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = AtelierSurfaceElevated,
                    unfocusedContainerColor = AtelierSurfaceElevated
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Ease Allowance Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ease Allowance (Couture Fit)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text(
                    text = String.format("%.1f cm", easeAllowance),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan
                )
            }
            Slider(
                value = easeAllowance,
                onValueChange = { easeAllowance = it },
                valueRange = 0f..5f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = NeonCyan,
                    activeTrackColor = NeonCyan,
                    inactiveTrackColor = AtelierSurfaceElevated
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Dart Manipulation
            Text("Dart Manipulation & Seam Placement", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dartOptions) { dart ->
                    val isSelected = selectedDart == dart
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDart = dart },
                        label = {
                            Text(dart, fontSize = 11.sp, color = if (isSelected) Color.White else TextSecondary)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonViolet,
                            containerColor = AtelierSurfaceElevated
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) NeonViolet else AtelierBorderGlass
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grainline Orientation
            Text("Grainline Alignment", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grainlineOptions.forEach { grain ->
                    val isSelected = selectedGrainline == grain
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedGrainline = grain },
                        label = { Text(grain, fontSize = 11.sp, color = if (isSelected) Color.White else TextSecondary) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberGold.copy(alpha = 0.8f),
                            containerColor = AtelierSurfaceElevated
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) CyberGold else AtelierBorderGlass
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Cutting & Construction Specs") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = AtelierBorderGlass,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = AtelierSurfaceElevated,
                    unfocusedContainerColor = AtelierSurfaceElevated
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Pattern Button
            Button(
                onClick = {
                    val updatedPattern = currentPattern.copy(
                        id = if (currentPattern.id.isBlank()) UUID.randomUUID().toString() else currentPattern.id,
                        name = patternName.ifBlank { "Custom Couture Sloper" },
                        easeAllowance = easeAllowance,
                        dartPosition = selectedDart,
                        seamAllowance = selectedSeamAllowance,
                        grainline = selectedGrainline,
                        notes = notes,
                        updatedAt = System.currentTimeMillis()
                    )
                    onSavePattern(updatedPattern)
                    Toast.makeText(context, "Pattern '${updatedPattern.name}' saved to Atelier studio!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_pattern_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyanDark),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.BookmarkAdded, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Pattern to Atelier Archives", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PatternPreviewCanvas(
    patternName: String,
    easeAllowance: Float,
    dartPosition: String,
    grainline: String
) {
    val cyanCol = NeonCyan
    val violetCol = NeonViolet
    val goldCol = CyberGold
    val magentaCol = NeonMagenta

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val w = size.width
        val h = size.height

        // Background grid dots simulating cutting mat
        val gridSpacing = 24f
        var x = 0f
        while (x < w) {
            var y = 0f
            while (y < h) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
                    radius = 1.2f,
                    center = Offset(x, y)
                )
                y += gridSpacing
            }
            x += gridSpacing
        }

        // Draw dynamic parametric sloper based on selected pattern
        val path = Path()
        val easeOffset = easeAllowance * 5f

        when {
            patternName.contains("Corset", ignoreCase = true) -> {
                // Hourglass corset piece with cinched waist
                path.moveTo(w * 0.25f - easeOffset, h * 0.15f)
                path.cubicTo(w * 0.35f, h * 0.18f, w * 0.65f, h * 0.12f, w * 0.75f + easeOffset, h * 0.15f)
                path.lineTo(w * 0.65f + easeOffset, h * 0.55f)
                // Waist curve
                path.quadraticBezierTo(w * 0.55f, h * 0.58f, w * 0.70f + easeOffset, h * 0.88f)
                path.lineTo(w * 0.30f - easeOffset, h * 0.88f)
                path.quadraticBezierTo(w * 0.45f, h * 0.58f, w * 0.35f - easeOffset, h * 0.55f)
                path.close()
            }
            patternName.contains("Sleeve", ignoreCase = true) -> {
                // Bell / Raglan crown sleeve
                path.moveTo(w * 0.2f, h * 0.85f)
                path.lineTo(w * 0.28f, h * 0.35f)
                path.cubicTo(w * 0.35f, h * 0.05f - easeOffset, w * 0.65f, h * 0.05f - easeOffset, w * 0.72f, h * 0.35f)
                path.lineTo(w * 0.8f, h * 0.85f)
                path.close()
            }
            patternName.contains("Trouser", ignoreCase = true) -> {
                // Trouser leg sloper
                path.moveTo(w * 0.30f, h * 0.15f)
                path.lineTo(w * 0.70f + easeOffset, h * 0.15f)
                path.lineTo(w * 0.75f + easeOffset, h * 0.40f) // Crotch line
                path.lineTo(w * 0.65f + easeOffset, h * 0.88f) // Inseam
                path.lineTo(w * 0.35f - easeOffset, h * 0.88f) // Leg opening
                path.lineTo(w * 0.25f - easeOffset, h * 0.40f) // Outseam
                path.close()
            }
            patternName.contains("Skirt", ignoreCase = true) -> {
                // A-Line skirt panel
                path.moveTo(w * 0.35f, h * 0.18f)
                path.lineTo(w * 0.65f, h * 0.18f)
                path.lineTo(w * 0.80f + easeOffset, h * 0.85f)
                path.lineTo(w * 0.20f - easeOffset, h * 0.85f)
                path.close()
            }
            else -> {
                // Classic Bodice Sloper with Neckline & Armhole
                path.moveTo(w * 0.25f, h * 0.18f)
                path.lineTo(w * 0.48f, h * 0.15f) // Shoulder seam
                path.cubicTo(w * 0.58f, h * 0.25f, w * 0.65f + easeOffset, h * 0.45f, w * 0.68f + easeOffset, h * 0.50f) // Armhole
                path.lineTo(w * 0.62f + easeOffset, h * 0.85f) // Side seam
                path.lineTo(w * 0.25f, h * 0.85f) // Waistline
                path.close()
            }
        }

        // Draw pattern cutline
        drawPath(
            path = path,
            color = cyanCol,
            style = Stroke(width = 2.5f)
        )

        // Draw internal seam line (dashed)
        drawPath(
            path = path,
            color = violetCol.copy(alpha = 0.6f),
            style = Stroke(
                width = 1.2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        )

        // Draw Grainline arrow
        val arrowStartX = w * 0.42f
        val arrowEndX = if (grainline.contains("Bias")) arrowStartX + 40f else arrowStartX
        val arrowStartY = h * 0.70f
        val arrowEndY = if (grainline.contains("Bias")) arrowStartY - 70f else h * 0.30f

        drawLine(
            color = goldCol,
            start = Offset(arrowStartX, arrowStartY),
            end = Offset(arrowEndX, arrowEndY),
            strokeWidth = 2f
        )
        // Arrow heads
        drawCircle(color = goldCol, radius = 3.5f, center = Offset(arrowEndX, arrowEndY))
        drawCircle(color = goldCol, radius = 3.5f, center = Offset(arrowStartX, arrowStartY))

        // Draw Dart indicator
        when (dartPosition) {
            "French Dart" -> {
                drawLine(
                    color = magentaCol,
                    start = Offset(w * 0.60f, h * 0.75f),
                    end = Offset(w * 0.42f, h * 0.52f),
                    strokeWidth = 1.8f
                )
            }
            "Princess Seam" -> {
                drawLine(
                    color = magentaCol,
                    start = Offset(w * 0.40f, h * 0.16f),
                    end = Offset(w * 0.40f, h * 0.85f),
                    strokeWidth = 1.8f
                )
            }
            "Zero Dart / Draped" -> {
                // No dart drawn for draped/zero dart sloper
            }
            else -> {
                drawLine(
                    color = magentaCol,
                    start = Offset(w * 0.42f, h * 0.85f),
                    end = Offset(w * 0.42f, h * 0.56f),
                    strokeWidth = 1.8f
                )
            }
        }
    }
}

private fun openWebLink(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open link: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
