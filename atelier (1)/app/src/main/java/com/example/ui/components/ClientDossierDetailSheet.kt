package com.example.ui.components

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.FilterChip
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
import com.example.model.ClientDossier
import com.example.model.Garment
import com.example.model.PatternDraft
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
import com.example.util.FittingNotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientDossierDetailSheet(
    client: ClientDossier,
    availableGarments: List<Garment>,
    onDismiss: () -> Unit,
    onUpdateClient: (ClientDossier) -> Unit,
    onDeleteClient: (String) -> Unit,
    onSelectGarment: (Garment) -> Unit,
    onOpenPatternStudio: (ClientDossier) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Chosen Garments (${client.chosenGarments.size})",
        "Body Measurements",
        "Patterns & Cutting",
        "Fitting Calendar & Schedule",
        "Fitting Notes"
    )

    var isEditing by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf(client.name) }
    var editVipTier by remember { mutableStateOf(client.vipTier) }
    var editEmail by remember { mutableStateOf(client.email) }
    var editPhone by remember { mutableStateOf(client.phone) }
    var editFittingDate by remember { mutableStateOf(client.nextFittingDate) }
    var editFittingNotes by remember { mutableStateOf(client.fittingNotes) }

    var showGarmentPickerSheet by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AtelierBackground,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AtelierSurfaceCard)
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!client.photoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = client.photoUrl,
                                    contentDescription = client.name,
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .border(2.dp, NeonViolet, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(NeonViolet.copy(alpha = 0.3f))
                                        .border(2.dp, NeonViolet, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = client.name.take(1).uppercase(),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = client.name,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextPrimary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(CyberGold.copy(alpha = 0.2f))
                                            .border(1.dp, CyberGold, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = client.vipTier,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberGold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${client.chosenGarments.size} Garments Assigned",
                                        fontSize = 11.sp,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { isEditing = !isEditing },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(AtelierSurfaceElevated)
                                    .testTag("edit_client_dossier_button")
                            ) {
                                Icon(
                                    imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                                    contentDescription = "Edit Dossier",
                                    tint = if (isEditing) NeonMagenta else NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(AtelierSurfaceElevated)
                                    .testTag("close_client_dossier_sheet")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    if (isEditing) {
                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick Edit Form
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text("Client Name") },
                            modifier = Modifier.fillMaxWidth().testTag("edit_client_name_field"),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonViolet,
                                unfocusedBorderColor = AtelierBorderGlass,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = AtelierSurfaceElevated,
                                unfocusedContainerColor = AtelierSurfaceElevated
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = editVipTier,
                                onValueChange = { editVipTier = it },
                                label = { Text("VIP Status") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyberGold,
                                    unfocusedBorderColor = AtelierBorderGlass,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = AtelierSurfaceElevated,
                                    unfocusedContainerColor = AtelierSurfaceElevated
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = editFittingDate,
                                onValueChange = { editFittingDate = it },
                                label = { Text("Next Fitting") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = AtelierBorderGlass,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedContainerColor = AtelierSurfaceElevated,
                                    unfocusedContainerColor = AtelierSurfaceElevated
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = { showDeleteConfirm = true },
                                modifier = Modifier.testTag("delete_client_dossier_button")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonMagenta)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    val updated = client.copy(
                                        name = editName.ifBlank { client.name },
                                        vipTier = editVipTier.ifBlank { client.vipTier },
                                        nextFittingDate = editFittingDate.ifBlank { client.nextFittingDate }
                                    )
                                    onUpdateClient(updated)
                                    isEditing = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("save_client_edits_button")
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Save Dossier")
                            }
                        }
                    } else {
                        // Fitting & Calendar Controls Bar
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AtelierSurfaceElevated)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Fitting: ${client.nextFittingDate}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        FittingNotificationHelper.showFittingScheduledNotification(
                                            context = context,
                                            clientName = client.name,
                                            fittingDate = client.nextFittingDate,
                                            reminderNote = "Haute Couture Fitting Scheduled"
                                        )
                                        Toast.makeText(context, "Fitting reminder set!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet.copy(alpha = 0.25f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("reminder_button")
                                ) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Remind", fontSize = 11.sp, color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = {
                                        FittingNotificationHelper.openCalendarEvent(
                                            context = context,
                                            clientName = client.name,
                                            fittingDate = client.nextFittingDate,
                                            notes = client.fittingNotes
                                        )
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("calendar_button")
                                ) {
                                    Icon(Icons.Default.EventAvailable, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Calendar", fontSize = 11.sp, color = NeonCyan)
                                }
                            }
                        }
                    }
                }
            }

            // Scrollable Tab Navigation
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = AtelierBackground,
                contentColor = NeonViolet,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NeonViolet,
                        height = 2.dp
                    )
                },
                divider = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(AtelierBorderGlass)
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) TextPrimary else TextMuted
                            )
                        }
                    )
                }
            }

            // Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // 1. Chosen Garments Tab
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "CLIENT'S CHOSEN GARMENTS (${client.chosenGarments.size})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 0.5.sp
                                )

                                Button(
                                    onClick = { showGarmentPickerSheet = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("add_garment_to_client_catalog_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Assign Garment", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (client.chosenGarments.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(AtelierSurfaceCard)
                                        .border(1.dp, AtelierBorderGlass, RoundedCornerShape(14.dp))
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = Icons.Default.FolderSpecial,
                                            contentDescription = null,
                                            tint = TextMuted,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "No garments assigned to ${client.name} yet.",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = TextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Tap '+ Assign Garment' to choose from the couture feed.",
                                            fontSize = 11.sp,
                                            color = TextMuted
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    client.chosenGarments.forEach { garment ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(AtelierSurfaceCard)
                                                .border(1.dp, AtelierBorderGlass, RoundedCornerShape(14.dp))
                                                .clickable { onSelectGarment(garment) }
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            AsyncImage(
                                                model = garment.imageUrl.ifBlank { "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80" },
                                                contentDescription = garment.title,
                                                modifier = Modifier
                                                    .size(70.dp)
                                                    .clip(RoundedCornerShape(10.dp)),
                                                contentScale = ContentScale.Crop
                                            )

                                            Spacer(modifier = Modifier.width(12.dp))

                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = garment.title,
                                                    fontSize = 15.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                                Text(
                                                    text = "${garment.category} • ${garment.silhouette}",
                                                    fontSize = 11.sp,
                                                    color = TextMuted
                                                )
                                                Text(
                                                    text = "Fabric: ${garment.fabric}",
                                                    fontSize = 11.sp,
                                                    color = CyberGold,
                                                    fontWeight = FontWeight.Medium
                                                )
                                                if (garment.customMeasurements.isNotEmpty()) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(4.dp))
                                                            .background(NeonCyan.copy(alpha = 0.2f))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "Customized Fit (${garment.customMeasurements.size} Points)",
                                                            fontSize = 9.sp,
                                                            color = NeonCyan,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }

                                            Column(
                                                horizontalAlignment = Alignment.End,
                                                verticalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = { onSelectGarment(garment) },
                                                    shape = RoundedCornerShape(8.dp),
                                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(32.dp)
                                                ) {
                                                    Text("Specs", fontSize = 11.sp, color = NeonViolet, fontWeight = FontWeight.Bold)
                                                }

                                                IconButton(
                                                    onClick = {
                                                        val updatedGarments = client.chosenGarments.filter { it.id != garment.id }
                                                        onUpdateClient(client.copy(chosenGarments = updatedGarments))
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Remove",
                                                        tint = TextMuted,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // 2. Anatomical Body Measurements Tab
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            MeasurementsView(
                                category = client.chosenGarments.firstOrNull()?.category ?: "Haute Couture",
                                silhouette = client.chosenGarments.firstOrNull()?.silhouette ?: "Sculptural",
                                initialCustomMeasurements = client.chosenGarments.firstOrNull()?.customMeasurements ?: emptyMap(),
                                onSaveToDossier = { formattedSummary ->
                                    val updated = client.copy(measurements = formattedSummary)
                                    onUpdateClient(updated)
                                    Toast.makeText(context, "Saved measurements to ${client.name}'s Dossier!", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    2 -> {
                        // 3. Patterns & Cutting Tab
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "PATTERN DRAFTING DIRECTIVES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted,
                                    letterSpacing = 0.5.sp
                                )

                                Button(
                                    onClick = { onOpenPatternStudio(client) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("open_pattern_studio_for_client_button")
                                ) {
                                    Icon(Icons.Default.DesignServices, contentDescription = null, tint = AtelierBackground, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Pattern Studio", fontSize = 12.sp, color = AtelierBackground, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (client.chosenGarments.isEmpty()) {
                                Text("Assign at least one garment to calculate cutting pieces and pattern specs.", fontSize = 12.sp, color = TextSecondary)
                            } else {
                                client.chosenGarments.forEach { garment ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AtelierSurfaceCard)
                                            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(12.dp))
                                            .padding(14.dp)
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.ContentCut, contentDescription = null, tint = CyberGold, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "${garment.title} (${garment.category})",
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "• Silhouette: ${garment.silhouette} • Fabric: ${garment.fabric}",
                                                fontSize = 12.sp,
                                                color = TextSecondary
                                            )
                                            Text(
                                                text = "• Pattern Pieces: Front Bodice, Back Bodice, Sleeve x2, Facing x2, Lining",
                                                fontSize = 11.sp,
                                                color = TextMuted
                                            )
                                            Text(
                                                text = "• Seam Allowance: 1.5 cm (Haute Couture French Seam)",
                                                fontSize = 11.sp,
                                                color = NeonCyan
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }

                    3 -> {
                        // 4. Fitting Calendar & Schedule Tab
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "FITTING CALENDAR & APPOINTMENT SCHEDULE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NeonCyan,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Client: ${client.name} (${client.vipTier})",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = CyberGold,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Current Active Appointment Banner
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AtelierSurfaceCard)
                                    .border(1.dp, NeonViolet, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(NeonViolet.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.EventAvailable, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text("Next Fitting Date", fontSize = 10.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = editFittingDate.ifBlank { "No Fitting Scheduled Yet" },
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (editFittingDate.isBlank()) TextSecondary else CyberGold
                                        )
                                        Text("Studio System Alarm & Google Calendar Sync Ready", fontSize = 10.sp, color = NeonCyan)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Interactive Studio Calendar Month Grid
                            Text("OCTOBER / NOVEMBER ATELIER SCHEDULE GRID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Spacer(modifier = Modifier.height(8.dp))

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(7),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AtelierSurfaceElevated)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                items((1..28).toList()) { day ->
                                    val dayStr = "2026-10-$day at 14:00"
                                    val isSelected = editFittingDate.startsWith("2026-10-$day") || editFittingDate.contains("Day $day")

                                    Box(
                                        modifier = Modifier
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (isSelected) NeonViolet else AtelierSurfaceCard
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) CyberGold else AtelierBorderGlass,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                editFittingDate = "2026-10-$day at 14:00 (Muslin Toile Fitting)"
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$day",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else TextPrimary
                                            )
                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(CyberGold)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick Appointment Preset Chips
                            Text("QUICK SCHEDULE PRESETS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                            Spacer(modifier = Modifier.height(6.dp))

                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    FilterChip(
                                        selected = false,
                                        onClick = { editFittingDate = "Tomorrow at 10:00 AM (Toile Muslin)" },
                                        label = { Text("Tomorrow 10:00 AM", fontSize = 11.sp) }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = false,
                                        onClick = { editFittingDate = "In 3 Days at 14:00 (First Fitting)" },
                                        label = { Text("In 3 Days 14:00", fontSize = 11.sp) }
                                    )
                                }
                                item {
                                    FilterChip(
                                        selected = false,
                                        onClick = { editFittingDate = "Next Monday at 11:00 AM (Final Hemming)" },
                                        label = { Text("Next Monday 11:00 AM", fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Custom Date/Time TextField
                            OutlinedTextField(
                                value = editFittingDate,
                                onValueChange = { editFittingDate = it },
                                label = { Text("Custom Date & Fitting Notes") },
                                placeholder = { Text("e.g. 2026-11-05 at 15:30 (Couture Silk Fitting)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("custom_fitting_date_field"),
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

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Trigger System Notification
                                Button(
                                    onClick = {
                                        val dateToSchedule = editFittingDate.ifBlank { "Upcoming Studio Session" }
                                        val updated = client.copy(nextFittingDate = dateToSchedule)
                                        onUpdateClient(updated)

                                        FittingNotificationHelper.showFittingScheduledNotification(
                                            context = context,
                                            clientName = client.name,
                                            fittingDate = dateToSchedule,
                                            reminderNote = "Ensure pattern sloper and fabric swatches are ready"
                                        )

                                        Toast.makeText(context, "Fitting scheduled & Notification Alarm set for ${client.name}!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("schedule_fitting_alarm_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Set Alarm Reminder", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                // Open Calendar Event
                                OutlinedButton(
                                    onClick = {
                                        val dateToSchedule = editFittingDate.ifBlank { "Upcoming Session" }
                                        FittingNotificationHelper.openCalendarEvent(
                                            context = context,
                                            clientName = client.name,
                                            fittingDate = dateToSchedule,
                                            notes = client.fittingNotes.ifBlank { "Bespoke Haute Couture fitting" }
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("sync_google_calendar_button"),
                                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold.copy(alpha = 0.15f)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = CyberGold, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Sync Calendar", fontSize = 11.sp, color = CyberGold, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    4 -> {
                        // 5. Fitting Notes Tab
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "FITTING NOTES & ALTERATION INSTRUCTIONS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = editFittingNotes,
                                onValueChange = { editFittingNotes = it },
                                label = { Text("Client Fitting & Alteration Log") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .testTag("fitting_notes_field"),
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

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val updated = client.copy(fittingNotes = editFittingNotes)
                                    onUpdateClient(updated)
                                    Toast.makeText(context, "Fitting notes saved!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(46.dp).testTag("save_notes_button")
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Save Fitting Notes", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Garment Selector Sheet
    if (showGarmentPickerSheet) {
        AlertDialog(
            onDismissRequest = { showGarmentPickerSheet = false },
            containerColor = AtelierBackground,
            shape = RoundedCornerShape(20.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Select Garment to Assign", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = { showGarmentPickerSheet = false }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Choose from public baseline catalog and private creations:",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (availableGarments.isEmpty()) {
                        Text("No garments available in feed.", fontSize = 12.sp, color = TextSecondary)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(availableGarments, key = { it.id }) { garment ->
                                val isAssigned = client.chosenGarments.any { it.id == garment.id }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isAssigned) NeonCyan.copy(alpha = 0.08f) else AtelierSurfaceElevated)
                                        .border(
                                            width = 1.dp,
                                            color = if (isAssigned) NeonCyan.copy(alpha = 0.5f) else AtelierBorderGlass,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            if (!isAssigned) {
                                                val updatedList = client.chosenGarments + garment
                                                onUpdateClient(client.copy(chosenGarments = updatedList))
                                                Toast.makeText(context, "Assigned '${garment.title}' to ${client.name}", Toast.LENGTH_SHORT).show()
                                                showGarmentPickerSheet = false
                                            }
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = garment.imageUrl.ifBlank { "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80" },
                                        contentDescription = garment.title,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(garment.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("${garment.category} • ${garment.silhouette}", fontSize = 10.sp, color = TextMuted)
                                    }

                                    if (isAssigned) {
                                        Text("Assigned", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                    } else {
                                        Button(
                                            onClick = {
                                                val updatedList = client.chosenGarments + garment
                                                onUpdateClient(client.copy(chosenGarments = updatedList))
                                                Toast.makeText(context, "Assigned '${garment.title}' to ${client.name}", Toast.LENGTH_SHORT).show()
                                                showGarmentPickerSheet = false
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Select", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Client Dossier?", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove ${client.name}'s dossier from your private cloud storage?", color = TextSecondary, fontSize = 13.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClient(client.id)
                        showDeleteConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = AtelierBackground
        )
    }
}
