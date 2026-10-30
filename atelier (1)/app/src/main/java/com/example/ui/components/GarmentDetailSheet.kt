package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.AiPlatform
import com.example.model.AiResearchEngine
import com.example.model.AiResearchTarget
import com.example.model.ClientDossier
import com.example.model.Garment
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GarmentDetailSheet(
    garment: Garment,
    isOwner: Boolean,
    categories: List<String> = emptyList(),
    clients: List<ClientDossier> = emptyList(),
    onDismiss: () -> Unit,
    onUpdateGarment: (Garment) -> Unit,
    onDeleteGarment: (Garment) -> Unit,
    onAssignToClient: ((ClientDossier, Garment) -> Unit)? = null,
    onCreateNewClient: ((String, String) -> Unit)? = null,
    onOpenInAiResearchHub: ((Garment) -> Unit)? = null
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isEditing by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showTechPackSheet by remember { mutableStateOf(false) }
    var showAssignClientDialog by remember { mutableStateOf(false) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    var editTitle by remember { mutableStateOf(garment.title) }
    var editCategory by remember { mutableStateOf(garment.category) }
    var editFabric by remember { mutableStateOf(garment.fabric) }
    var editSilhouette by remember { mutableStateOf(garment.silhouette) }
    var editDescription by remember { mutableStateOf(garment.description) }
    var editImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            editImageUri = uri
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            containerColor = AtelierBackground,
            title = {
                Text(
                    text = "Delete Private Garment",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete '${garment.title}' from your private cloud storage? This action cannot be undone.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDeleteGarment(garment)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("Delete Permanently", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    if (showTechPackSheet) {
        TechPackExportSheet(
            garment = garment,
            onDismiss = { showTechPackSheet = false }
        )
    }

    if (showAssignClientDialog) {
        var selectedClient by remember { mutableStateOf(clients.firstOrNull()) }
        var newClientName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAssignClientDialog = false },
            containerColor = AtelierBackground,
            title = { Text("Add Garment to Client Page", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select a client dossier to assign '${garment.title}' to:", color = TextSecondary, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    if (clients.isNotEmpty()) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(clients) { client ->
                                val isSelected = selectedClient?.id == client.id
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedClient = client },
                                    label = { Text(client.name, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonCyan,
                                        selectedLabelColor = AtelierBackground,
                                        containerColor = AtelierSurfaceElevated,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    OutlinedTextField(
                        value = newClientName,
                        onValueChange = { newClientName = it },
                        label = { Text("Or Enter New Client Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = AtelierBorderGlass,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clientToAssign = selectedClient
                        if (clientToAssign != null && onAssignToClient != null) {
                            onAssignToClient(clientToAssign, garment)
                            Toast.makeText(context, "Assigned '${garment.title}' to ${clientToAssign.name}'s Client Page", Toast.LENGTH_SHORT).show()
                        } else if (newClientName.isNotBlank() && onCreateNewClient != null) {
                            onCreateNewClient(newClientName, "Private Client")
                            Toast.makeText(context, "Created client '$newClientName' and assigned garment", Toast.LENGTH_SHORT).show()
                        }
                        showAssignClientDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text("Assign to Client", color = AtelierBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAssignClientDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AtelierBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = editImageUri ?: garment.imageUrl.ifBlank { "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=800&q=80" },
                    contentDescription = garment.title,
                    modifier = Modifier.fillMaxWidth().height(300.dp),
                    contentScale = ContentScale.Crop
                )

                // Top Controls Overlay
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Badge: Custom vs Public
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (garment.isCustom) NeonCyan else CyberGold.copy(alpha = 0.2f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (garment.isCustom) NeonCyan else CyberGold,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (garment.isCustom) "PRIVATE CREATION" else "PUBLIC CATALOG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (garment.isCustom) AtelierBackground else CyberGold
                        )
                    }

                    // Close button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AtelierBackground.copy(alpha = 0.7f))
                            .testTag("close_detail_sheet_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // If editing and is owner, show photo change overlay
                if (isEditing && isOwner) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .clip(CircleShape)
                            .background(AtelierBackground.copy(alpha = 0.85f))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Change photo",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Change Photo", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                if (isEditing && isOwner) {
                    // Editing Mode for Private Garments
                    Text(
                        text = "Edit Private Garment",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonViolet,
                            unfocusedBorderColor = AtelierBorderGlass,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = AtelierSurfaceElevated,
                            unfocusedContainerColor = AtelierSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                    Spacer(modifier = Modifier.height(6.dp))

                    val allCategoryOptions = remember(categories, editCategory) {
                        val base = categories.ifEmpty {
                            listOf("All", "Haute Couture", "Avant-Garde", "Tailored Suit", "Evening Gown", "Corsetry", "Minimalist Tech", "Cyber-Luminescence", "Runway Statement")
                        }.filter { it != "All" && it != "My Creations" }
                        if (!base.contains(editCategory)) listOf(editCategory) + base else base
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(allCategoryOptions) { cat ->
                            val isSelected = editCategory == cat
                            FilterChip(
                                selected = isSelected,
                                onClick = { editCategory = cat },
                                label = { Text(cat, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                                    selectedLabelColor = NeonCyan,
                                    containerColor = AtelierSurfaceElevated,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = AtelierBorderGlass,
                                    selectedBorderColor = NeonCyan
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editFabric,
                        onValueChange = { editFabric = it },
                        label = { Text("Fabric & Textiles") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_fabric_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonViolet,
                            unfocusedBorderColor = AtelierBorderGlass,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = AtelierSurfaceElevated,
                            unfocusedContainerColor = AtelierSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editSilhouette,
                        onValueChange = { editSilhouette = it },
                        label = { Text("Silhouette") },
                        modifier = Modifier.fillMaxWidth().testTag("edit_silhouette_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonViolet,
                            unfocusedBorderColor = AtelierBorderGlass,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = AtelierSurfaceElevated,
                            unfocusedContainerColor = AtelierSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Couture Description & Notes") },
                        modifier = Modifier.fillMaxWidth().height(120.dp).testTag("edit_description_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonViolet,
                            unfocusedBorderColor = AtelierBorderGlass,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = AtelierSurfaceElevated,
                            unfocusedContainerColor = AtelierSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { isEditing = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", color = TextSecondary)
                        }
                        Button(
                            onClick = {
                                val updated = garment.copy(
                                    title = editTitle,
                                    category = editCategory,
                                    fabric = editFabric,
                                    silhouette = editSilhouette,
                                    description = editDescription,
                                    imageUrl = editImageUri?.toString() ?: garment.imageUrl
                                )
                                onUpdateGarment(updated)
                                isEditing = false
                            },
                            modifier = Modifier.weight(1f).testTag("save_edits_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Standard Viewing Mode Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = garment.category.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonViolet,
                            letterSpacing = 1.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = NeonMagenta,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${garment.likes} runway likes", fontSize = 12.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = garment.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Text(
                        text = "Designed by ${garment.designer}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Atelier Feature Tabs
                    val tabs = listOf(
                        "Overview",
                        "2D/3D Croquis",
                        "Cutting Board",
                        "49 Measurements",
                        "Color Studio",
                        "Fabric Yardage",
                        "17 AI Platforms"
                    )

                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color(0xFF0C0C14),
                        contentColor = NeonCyan,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = NeonCyan
                            )
                        },
                        edgePadding = 0.dp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(10.dp))
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        title,
                                        fontSize = 11.sp,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTabIndex == index) NeonCyan else TextSecondary
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tab Contents
                    when (selectedTabIndex) {
                        0 -> {
                            // 1. Overview Tab
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FrostedGlassBox(
                                    modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("FABRIC", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(garment.fabric, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                                    }
                                }
                                FrostedGlassBox(
                                    modifier = Modifier.weight(1f).padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("SILHOUETTE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(garment.silhouette, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "COUTURE ATELIER NOTES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = garment.description,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // One-Tap "Export Haute Couture Tech Pack" Button
                            Button(
                                onClick = { showTechPackSheet = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("open_tech_pack_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberGold.copy(alpha = 0.2f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = CyberGold, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Export Haute Couture Tech Pack", color = CyberGold, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Atelier Actions Row: Add to Client Page & Add to Runway Lookbook
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Add to Client Page Button
                                Button(
                                    onClick = { showAssignClientDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("add_to_client_page_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.PersonAdd, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add to Client Page", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                }

                                // Add to Runway Lookbook Button
                                Button(
                                    onClick = {
                                        val updated = garment.copy(isCustom = true)
                                        onUpdateGarment(updated)
                                        Toast.makeText(context, "'${garment.title}' added to your Runway Lookbook!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp)
                                        .testTag("add_to_runway_lookbook_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet.copy(alpha = 0.25f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.ViewInAr, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Add to Lookbook", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Quick 17 AI Platform Launchers Card
                            FrostedGlassBox(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = NeonCyan,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "17 AI INSPIRATION PLATFORMS",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = NeonCyan,
                                                letterSpacing = 0.5.sp
                                            )
                                        }

                                        TextButton(
                                            onClick = { selectedTabIndex = 6 },
                                            contentPadding = PaddingValues(0.dp)
                                        ) {
                                            Text("View Studio", fontSize = 11.sp, color = NeonViolet, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    if (onOpenInAiResearchHub != null) {
                                        Button(
                                            onClick = {
                                                onOpenInAiResearchHub(garment)
                                                onDismiss()
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(42.dp)
                                                .testTag("link_garment_to_ai_hub_button"),
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Hub, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Link Garment to AI Research Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }

                                    Text(
                                        text = "Generate concept art, pattern drafting instructions, and finished garment inspiration:",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(AiPlatform.entries) { platform ->
                                            val promptObj = remember(platform, garment.title, garment.silhouette, garment.fabric) {
                                                AiResearchEngine.buildPrompt(
                                                    platform = platform,
                                                    target = AiResearchTarget.GARMENT_INSPIRATION,
                                                    garmentTitle = garment.title,
                                                    silhouette = garment.silhouette,
                                                    fabric = garment.fabric,
                                                    selectedTags = emptySet()
                                                )
                                            }

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(AtelierSurfaceElevated)
                                                    .border(1.dp, AtelierBorderGlass, RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        try {
                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(promptObj.searchUrl))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Could not open ${platform.displayName}", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 8.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = platform.displayName,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.OpenInBrowser,
                                                        contentDescription = null,
                                                        tint = NeonCyan,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        1 -> {
                            // 2. 2D/3D Croquis Rendering & Anatomical Fit
                            CroquisAnatomicalView(
                                garmentTitle = garment.title,
                                silhouette = garment.silhouette,
                                category = garment.category
                            )
                        }

                        2 -> {
                            // 3. Cutting Board & Autonest Layout
                            CuttingBoardView(
                                garmentTitle = garment.title,
                                category = garment.category,
                                silhouette = garment.silhouette
                            )
                        }

                        3 -> {
                            // 4. 49 Master Body Measurements
                            MeasurementsView(
                                category = garment.category,
                                silhouette = garment.silhouette,
                                initialCustomMeasurements = garment.customMeasurements,
                                onMeasurementsChanged = { updatedMap ->
                                    if (isOwner) {
                                        onUpdateGarment(garment.copy(customMeasurements = updatedMap))
                                    }
                                }
                            )
                        }

                        4 -> {
                            // 5. Chromatic Garment Color Studio
                            ColorStudioView()
                        }

                        5 -> {
                            // 6. Fabric Yardage Estimator
                            FabricEstimatorView(
                                garmentTitle = garment.title,
                                category = garment.category,
                                silhouette = garment.silhouette
                            )
                        }

                        6 -> {
                            // 7. 17 AI Inspiration Platforms
                            GarmentAiInspirationView(
                                garment = garment,
                                onOpenInAiResearchHub = { g ->
                                    onOpenInAiResearchHub?.invoke(g)
                                    onDismiss()
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Edit & Delete controls for user's own garments
                    if (isOwner) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Private Garment Ownership",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonCyan
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { isEditing = true },
                                    modifier = Modifier.weight(1f).testTag("edit_garment_button"),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Edit Garment", color = NeonViolet, fontWeight = FontWeight.SemiBold)
                                }
                                Button(
                                    onClick = { showDeleteConfirm = true },
                                    modifier = Modifier.weight(1f).testTag("delete_garment_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta.copy(alpha = 0.25f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Delete", color = NeonMagenta, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    } else {
                        // Global baseline: edit controls are completely HIDDEN
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AtelierSurfaceElevated)
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Protected",
                                tint = CyberGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Global Baseline Collection",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Read-only curated garment in /garments. Edit controls are restricted.",
                                    fontSize = 11.sp,
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

@Composable
fun GarmentAiInspirationView(
    garment: Garment,
    onOpenInAiResearchHub: ((Garment) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTarget by remember { mutableStateOf(AiResearchTarget.GARMENT_INSPIRATION) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = remember {
        listOf("All", "Visual Search", "Visual Moodboard", "Video & Draping", "Generative Creative", "Cinematic Diffusion", "Conversational AI", "Technical Reasoning", "Vector & Flat Sketch", "3D Digital Prototyping")
    }

    val filteredPlatforms = remember(searchQuery, selectedCategoryFilter) {
        AiPlatform.entries.filter { platform ->
            val matchesSearch = searchQuery.isBlank() ||
                platform.displayName.contains(searchQuery, ignoreCase = true) ||
                platform.category.contains(searchQuery, ignoreCase = true) ||
                platform.promptFormatDescription.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategoryFilter == "All" || platform.category.equals(selectedCategoryFilter, ignoreCase = true)
            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (onOpenInAiResearchHub != null) {
            Button(
                onClick = { onOpenInAiResearchHub(garment) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("link_garment_to_ai_hub_studio_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Hub, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Link Garment to AI Research Hub", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Target Mode Toggle (Garment Concept vs Textile & Pattern Drafting)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AtelierSurfaceElevated)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AiResearchTarget.entries.forEach { target ->
                val isSelected = selectedTarget == target
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) NeonViolet else Color.Transparent)
                        .clickable { selectedTarget = target }
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = target.label,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search bar & Platform Count
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Filter 17 AI Platforms (e.g. Midjourney, Google, Pinterest...)", fontSize = 12.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = NeonCyan) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_ai_platforms_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = AtelierBorderGlass,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = AtelierSurfaceElevated,
                unfocusedContainerColor = AtelierSurfaceElevated
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                val isSelected = selectedCategoryFilter == cat
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategoryFilter = cat },
                    label = { Text(cat, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonCyan.copy(alpha = 0.2f),
                        selectedLabelColor = NeonCyan,
                        containerColor = AtelierSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = AtelierBorderGlass,
                        selectedBorderColor = NeonCyan
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "DIRECT LAUNCHERS & PROMPT GENERATORS (${filteredPlatforms.size}/17)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Render platform list
        filteredPlatforms.forEach { platform ->
            val generatedPrompt = remember(platform, selectedTarget, garment.title, garment.silhouette, garment.fabric) {
                AiResearchEngine.buildPrompt(
                    platform = platform,
                    target = selectedTarget,
                    garmentTitle = garment.title,
                    silhouette = garment.silhouette,
                    fabric = garment.fabric,
                    selectedTags = emptySet()
                )
            }

            FrostedGlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Badge Icon
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(NeonViolet.copy(alpha = 0.2f))
                                    .border(1.dp, NeonViolet, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = platform.iconKey,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = platform.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = platform.category + " • " + platform.promptFormatDescription,
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Formatted Prompt Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AtelierBackground)
                            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = generatedPrompt.formattedPrompt,
                            fontSize = 11.sp,
                            color = CyberGold,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Open in Platform / Browser
                        Button(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(generatedPrompt.searchUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Could not open ${platform.displayName}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("launch_platform_${platform.name.lowercase()}"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInBrowser,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open ${platform.displayName}", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                        }

                        // Copy Prompt
                        OutlinedButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("AI Prompt (${platform.displayName})", generatedPrompt.formattedPrompt)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Copied prompt for ${platform.displayName}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("copy_prompt_${platform.name.lowercase()}"),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderGlass),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Copy Prompt", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

