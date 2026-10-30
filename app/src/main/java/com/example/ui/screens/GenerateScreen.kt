package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.AiPlatform
import com.example.model.AiResearchEngine
import com.example.model.AiResearchTarget
import com.example.model.Garment
import com.example.ui.components.FrostedGlassBox
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
import com.example.viewmodel.AtelierViewModel

@Composable
fun GenerateScreen(
    viewModel: AtelierViewModel? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activeResearchGarment = viewModel?.activeResearchGarment?.collectAsStateWithLifecycle()?.value

    var selectedTarget by remember { mutableStateOf(AiResearchTarget.GARMENT_INSPIRATION) }
    var selectedPlatform by remember { mutableStateOf(AiPlatform.MIDJOURNEY) }

    var garmentTitle by remember(activeResearchGarment) {
        mutableStateOf(activeResearchGarment?.title ?: "Liquid Silver Sculptural Ballgown")
    }
    var silhouette by remember(activeResearchGarment) {
        mutableStateOf(activeResearchGarment?.silhouette ?: "Dramatic Architectural Column")
    }
    var fabric by remember(activeResearchGarment) {
        mutableStateOf(activeResearchGarment?.fabric ?: "Fluid Metallic Silk Lamé & Optic Mesh")
    }
    var selectedTags by remember { mutableStateOf(setOf("Bioluminescent Optical Fibers", "Sculptural Avant-Garde")) }

    val activeTags = if (selectedTarget == AiResearchTarget.GARMENT_INSPIRATION) {
        AiResearchEngine.GARMENT_STYLE_TAGS
    } else {
        AiResearchEngine.TEXTILE_STYLE_TAGS
    }

    val currentGeneratedPrompt = remember(
        selectedPlatform,
        selectedTarget,
        garmentTitle,
        silhouette,
        fabric,
        selectedTags
    ) {
        AiResearchEngine.buildPrompt(
            platform = selectedPlatform,
            target = selectedTarget,
            garmentTitle = garmentTitle,
            silhouette = silhouette,
            fabric = fabric,
            selectedTags = selectedTags
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AtelierBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Top Atelier Header with Dressmaker Scissors Emblem
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(NeonViolet.copy(alpha = 0.2f))
                        .border(1.dp, NeonViolet, CircleShape)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_atelier_scissors_logo_1792658287419),
                        contentDescription = "Dressmaker Scissors",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AI RESEARCH HUB",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Tailored Prompts across 17 AI Platforms & Concept Studios",
                        fontSize = 11.sp,
                        color = NeonCyan
                    )
                }
            }

            IconButton(
                onClick = {
                    val randGarments = listOf(
                        Triple("Liquid Obsidian Corset", "Hourglass Micro-Cinch", "Rigid Molded Carbon & Velvet"),
                        Triple("Bioluminescent Kinetic Cape", "Origami Folded Cocoon", "Optic Fiber Silk Organza"),
                        Triple("Savile Row Cyber Tuxedo", "Tailored Structured Shoulder", "Worsted Wool & Electroluminescent Wire"),
                        Triple("Prismatic Hologram Column", "Fluid High-Slit Mermaid", "Thermochromic Liquid Foil"),
                        Triple("Zero-Waste Kimono Robe", "Geometric Wide Draped Sleeve", "Raw Japanese Selvedge Silk")
                    )
                    val pick = randGarments.random()
                    garmentTitle = pick.first
                    silhouette = pick.second
                    fabric = pick.third
                    Toast.makeText(context, "Loaded new couture concept!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AtelierSurfaceCard)
                    .border(1.dp, AtelierBorderGlass, CircleShape)
                    .testTag("random_concept_button")
            ) {
                Icon(Icons.Default.Casino, contentDescription = "Random Concept", tint = CyberGold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active Linked Garment Banner
        activeResearchGarment?.let { linked ->
            FrostedGlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(14.dp),
                borderColor = NeonCyan
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = linked.imageUrl.ifBlank { "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80" },
                        contentDescription = linked.title,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "LINKED GARMENT ACTIVE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = linked.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${linked.category} • ${linked.silhouette}",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { viewModel?.setResearchGarment(null) },
                        modifier = Modifier.testTag("unlink_research_garment_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Unlink Garment",
                            tint = TextMuted
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Target Tabs: Garment Inspiration vs Textile / Pattern Inspiration
        TabRow(
            selectedTabIndex = if (selectedTarget == AiResearchTarget.GARMENT_INSPIRATION) 0 else 1,
            containerColor = Color(0xFF0F0F18),
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                val tabIdx = if (selectedTarget == AiResearchTarget.GARMENT_INSPIRATION) 0 else 1
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[tabIdx]),
                    color = NeonCyan
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, AtelierBorderGlass, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedTarget == AiResearchTarget.GARMENT_INSPIRATION,
                onClick = {
                    selectedTarget = AiResearchTarget.GARMENT_INSPIRATION
                    selectedTags = setOf("Bioluminescent Optical Fibers", "Sculptural Avant-Garde")
                },
                text = {
                    Text(
                        "Garment & Silhouette",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTarget == AiResearchTarget.GARMENT_INSPIRATION) NeonCyan else TextSecondary
                    )
                }
            )
            Tab(
                selected = selectedTarget == AiResearchTarget.TEXTILE_PATTERN,
                onClick = {
                    selectedTarget = AiResearchTarget.TEXTILE_PATTERN
                    selectedTags = setOf("Cybernetic Jacquard Weave", "Seamless Monogram Damask")
                },
                text = {
                    Text(
                        "Pattern & Textile Prints",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTarget == AiResearchTarget.TEXTILE_PATTERN) NeonCyan else TextSecondary
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Platform Carousel (All 17 Platforms)
        Text(
            text = "Select Destination AI Engine / Platform (17 Available):",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(AiPlatform.entries) { platform ->
                val isSelected = selectedPlatform == platform
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonViolet.copy(alpha = 0.3f) else AtelierSurfaceCard)
                        .border(1.dp, if (isSelected) NeonViolet else AtelierBorderGlass, RoundedCornerShape(10.dp))
                        .clickable { selectedPlatform = platform }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("platform_chip_${platform.name.lowercase()}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NeonCyan else Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = platform.iconKey,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.Black else TextPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = platform.displayName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) TextPrimary else TextSecondary
                            )
                            Text(
                                text = platform.category,
                                fontSize = 9.sp,
                                color = if (isSelected) NeonCyan else TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Concept Inputs (Garment Title, Silhouette, Fabric)
        FrostedGlassBox(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp)) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Couture Concept Parameters", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = CyberGold)
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = garmentTitle,
                    onValueChange = { garmentTitle = it },
                    label = { Text("Garment Name / Project Title", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = AtelierBorderGlass,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = silhouette,
                        onValueChange = { silhouette = it },
                        label = { Text("Silhouette Line", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = AtelierBorderGlass,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = fabric,
                        onValueChange = { fabric = it },
                        label = { Text("Primary Fabric", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = AtelierBorderGlass,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Aesthetic & Craft Style Tags
                Text(
                    text = if (selectedTarget == AiResearchTarget.GARMENT_INSPIRATION) "Couture Aesthetic Elements:" else "Textile Weave & Pattern Accents:",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    activeTags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                val updated = selectedTags.toMutableSet()
                                if (isSelected) updated.remove(tag) else updated.add(tag)
                                selectedTags = updated
                            },
                            label = { Text(tag, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                                selectedLabelColor = NeonCyan
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                selectedBorderColor = NeonCyan,
                                borderColor = AtelierBorderGlass
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Formatted Prompt Display Card with Model-Specific Syntax
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF09090F))
                .border(1.5.dp, NeonViolet.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(16.dp)
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
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${selectedPlatform.displayName} Optimized Prompt",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Text(
                        text = selectedPlatform.promptFormatDescription,
                        fontSize = 10.sp,
                        color = CyberGold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = currentGeneratedPrompt.formattedPrompt,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFE2E2F0),
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Quick Actions (1-Tap Copy & Launch Platform)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("AI Prompt - ${selectedPlatform.displayName}", currentGeneratedPrompt.formattedPrompt)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copied prompt for ${selectedPlatform.displayName}!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("copy_prompt_button"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Prompt", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            try {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(currentGeneratedPrompt.searchUrl))
                                context.startActivity(browserIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Opening ${selectedPlatform.displayName}...", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("launch_platform_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Launch ${selectedPlatform.iconKey}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
