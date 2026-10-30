package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Straighten
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.ClientDossier
import com.example.model.Garment
import com.example.model.PatternDraft
import com.example.ui.components.ClientDossierDetailSheet
import com.example.ui.components.FabricIndexSheet
import com.example.ui.components.FrostedGlassBox
import com.example.ui.components.GarmentDetailSheet
import com.example.ui.components.MeasurementsGuideSheet
import com.example.ui.components.PatternDraftingSheet
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
import com.example.util.FittingNotificationHelper
import com.example.viewmodel.AtelierViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun LibraryScreen(
    viewModel: AtelierViewModel,
    onNavigateToGenerateTab: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Client Dossiers", "Private Garments")

    val clients by viewModel.clients.collectAsStateWithLifecycle()
    val customGarments by viewModel.customGarmentsOnly.collectAsStateWithLifecycle()
    val selectedGarment by viewModel.selectedGarment.collectAsStateWithLifecycle()
    val feedGarments by viewModel.filteredFeed.collectAsStateWithLifecycle()

    var showAddClientSheet by remember { mutableStateOf(false) }
    var patternDraftingClient by remember { mutableStateOf<ClientDossier?>(null) }
    var showGeneralPatternSheet by remember { mutableStateOf(false) }
    var selectedClientDetail by remember { mutableStateOf<ClientDossier?>(null) }
    var showFabricIndexSheet by remember { mutableStateOf(false) }
    var showMeasurementsGuideSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AtelierBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "ATELIER ARCHIVES",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "Private Cloud Store (/users/{uid}/...)",
                    fontSize = 10.sp,
                    color = NeonCyan
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Fabric Index Trigger
                OutlinedButton(
                    onClick = { showFabricIndexSheet = true },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp).testTag("open_fabric_index_button")
                ) {
                    Text("Fabrics (200)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CyberGold)
                }

                // 49 Guide Trigger
                OutlinedButton(
                    onClick = { showMeasurementsGuideSheet = true },
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp).testTag("open_49_guide_button")
                ) {
                    Text("49 Guide", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NeonViolet)
                }

                if (selectedTabIndex == 0) {
                    Button(
                        onClick = { showAddClientSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp).testTag("add_client_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Add Client", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Tabs
        TabRow(
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
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) TextPrimary else TextMuted
                        )
                    }
                )
            }
        }

        // Content
        if (selectedTabIndex == 0) {
            // Client Dossiers Tab
            if (clients.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Client Dossiers Yet",
                            fontSize = 16.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'Add Client' to record fittings, measurements and set reminders",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(clients, key = { it.id }) { client ->
                        ClientDossierCard(
                            client = client,
                            onClick = { selectedClientDetail = client },
                            onDelete = { viewModel.deleteClient(client.id) },
                            onDraftPattern = { patternDraftingClient = client },
                            onScheduleReminder = { reminderLead ->
                                FittingNotificationHelper.showFittingScheduledNotification(
                                    context = context,
                                    clientName = client.name,
                                    fittingDate = client.nextFittingDate,
                                    reminderNote = "Reminder set for $reminderLead"
                                )
                                Toast.makeText(context, "Fitting reminder scheduled for ${client.name}!", Toast.LENGTH_SHORT).show()
                            },
                            onSyncCalendar = {
                                FittingNotificationHelper.openCalendarEvent(
                                    context = context,
                                    clientName = client.name,
                                    fittingDate = client.nextFittingDate,
                                    notes = client.fittingNotes
                                )
                            }
                        )
                    }
                }
            }
        } else {
            // Private Garments Tab
            if (customGarments.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderSpecial,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Private Garments Created",
                            fontSize = 16.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Garments created via Home [+] appear here",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(customGarments, key = { it.id }) { garment ->
                        FrostedGlassBox(
                            shape = RoundedCornerShape(14.dp),
                            borderColor = NeonCyan.copy(alpha = 0.5f),
                            onClick = { viewModel.selectGarment(garment) }
                        ) {
                            Column {
                                AsyncImage(
                                    model = garment.imageUrl,
                                    contentDescription = garment.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.85f),
                                    contentScale = ContentScale.Crop
                                )
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = garment.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = garment.category,
                                        fontSize = 11.sp,
                                        color = NeonCyan
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddClientSheet) {
        AddClientSheet(
            onDismiss = { showAddClientSheet = false },
            onSubmit = { name, vipTier, email, phone, measurements, notes, photoUri, date, scheduleReminder, reminderLead ->
                viewModel.addClient(name, vipTier, email, phone, measurements, notes, photoUri, date) {
                    if (scheduleReminder) {
                        FittingNotificationHelper.showFittingScheduledNotification(
                            context = context,
                            clientName = name,
                            fittingDate = date,
                            reminderNote = "Reminder set for $reminderLead"
                        )
                    }
                    showAddClientSheet = false
                }
            }
        )
    }

    // Pattern Drafting Sheet (when opened directly or for a client)
    if (showGeneralPatternSheet || patternDraftingClient != null) {
        val client = patternDraftingClient
        val initialDraft = remember(client) {
            if (client != null) {
                PatternDraft(
                    name = "Bespoke Sloper for ${client.name}",
                    notes = "Engineered to client specifications: ${client.measurements}. Fitting: ${client.nextFittingDate}"
                )
            } else {
                PatternDraft()
            }
        }

        PatternDraftingSheet(
            initialPattern = initialDraft,
            onDismiss = {
                showGeneralPatternSheet = false
                patternDraftingClient = null
            },
            onSavePattern = { saved ->
                showGeneralPatternSheet = false
                patternDraftingClient = null
            }
        )
    }

    selectedGarment?.let { garment ->
        GarmentDetailSheet(
            garment = garment,
            isOwner = viewModel.isUserOwner(garment),
            clients = clients,
            onDismiss = { viewModel.selectGarment(null) },
            onUpdateGarment = { updated ->
                viewModel.updateCustomGarment(updated)
            },
            onDeleteGarment = { target ->
                viewModel.deleteCustomGarment(target) {
                    viewModel.selectGarment(null)
                }
            },
            onAssignToClient = { client, item ->
                viewModel.addGarmentToClient(client, item)
            },
            onOpenInAiResearchHub = { target ->
                viewModel.setResearchGarment(target)
                viewModel.selectGarment(null)
                onNavigateToGenerateTab()
            }
        )
    }

    selectedClientDetail?.let { detailClient ->
        val freshClient = clients.find { it.id == detailClient.id } ?: detailClient
        ClientDossierDetailSheet(
            client = freshClient,
            availableGarments = feedGarments,
            onDismiss = { selectedClientDetail = null },
            onUpdateClient = { updated ->
                viewModel.updateClientDossier(updated)
            },
            onDeleteClient = { clientId ->
                viewModel.deleteClient(clientId)
                selectedClientDetail = null
            },
            onSelectGarment = { garment ->
                viewModel.selectGarment(garment)
            },
            onOpenPatternStudio = { clientDossier ->
                patternDraftingClient = clientDossier
            }
        )
    }

    if (showFabricIndexSheet) {
        FabricIndexSheet(
            clients = clients,
            onAssignFabricToClient = { client, fab ->
                viewModel.updateClientDossier(client)
            },
            onDismiss = { showFabricIndexSheet = false }
        )
    }

    if (showMeasurementsGuideSheet) {
        MeasurementsGuideSheet(
            clients = clients,
            onOpenClientDossiers = {
                // Already on library screen
            },
            onApplyToClient = { client, def ->
                val updatedNotes = if (client.measurements.isBlank()) def.name else "${client.measurements} | ${def.name}"
                viewModel.updateClientDossier(client.copy(measurements = updatedNotes))
            },
            onDismiss = { showMeasurementsGuideSheet = false }
        )
    }
}

@Composable
fun ClientDossierCard(
    client: ClientDossier,
    onClick: () -> Unit = {},
    onDelete: () -> Unit,
    onDraftPattern: () -> Unit = {},
    onScheduleReminder: (leadTime: String) -> Unit = {},
    onSyncCalendar: () -> Unit = {}
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showReminderMenu by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Client Dossier?", color = TextPrimary) },
            text = { Text("Remove ${client.name} from your private fitting roster?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
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
            containerColor = AtelierSurfaceCard
        )
    }

    FrostedGlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("client_dossier_card_${client.id}"),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NeonViolet.copy(alpha = 0.2f))
                            .border(1.dp, NeonViolet, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = client.name.take(1).uppercase(),
                            color = NeonViolet,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = client.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyberGold.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = client.vipTier.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberGold
                            )
                        }
                    }
                }

                IconButton(onClick = { showDeleteConfirm = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete client",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Measurements Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AtelierSurfaceElevated)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Straighten,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = client.measurements,
                    fontSize = 12.sp,
                    color = TextPrimary
                )
            }

            if (client.fittingNotes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "FITTING NOTES: ${client.fittingNotes}",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fitting Date & Action Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AtelierSurfaceElevated.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = NeonViolet,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("NEXT FITTING", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                        Text(
                            text = client.nextFittingDate,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Reminder Trigger
                    IconButton(
                        onClick = { onScheduleReminder("1 Day Before Fitting") },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NeonViolet.copy(alpha = 0.15f))
                            .testTag("schedule_reminder_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddAlert,
                            contentDescription = "Schedule Reminder Notification",
                            tint = NeonViolet,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Calendar Sync
                    IconButton(
                        onClick = onSyncCalendar,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .testTag("sync_calendar_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventAvailable,
                            contentDescription = "Sync to Calendar",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Draft Pattern Action Button
            OutlinedButton(
                onClick = onDraftPattern,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .testTag("draft_pattern_button_${client.id}"),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                Icon(Icons.Default.DesignServices, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Draft & Save Pattern for ${client.name.split(" ").firstOrNull() ?: "Client"}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NeonCyan)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddClientSheet(
    onDismiss: () -> Unit,
    onSubmit: (
        name: String,
        vipTier: String,
        email: String,
        phone: String,
        measurements: String,
        notes: String,
        photoUri: Uri?,
        date: String,
        scheduleReminder: Boolean,
        reminderLead: String
    ) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf("") }
    var vipTier by remember { mutableStateOf("Haute VIP") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var measurements by remember { mutableStateOf("Bust: 34in | Waist: 26in | Hips: 36in | Height: 5'10\"") }
    var fittingNotes by remember { mutableStateOf("") }
    var nextFittingDate by remember { mutableStateOf("Nov 25, 2026") }
    var scheduleReminder by remember { mutableStateOf(true) }
    var reminderLead by remember { mutableStateOf("1 Day Before") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val reminderOptions = listOf("1 Day Before", "2 Days Before", "Morning of Fitting (9 AM)")

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) selectedImageUri = uri
    }

    // Date Picker Dialog setup
    val calendar = remember { Calendar.getInstance() }
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)
                nextFittingDate = format.format(selectedCal.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AtelierBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("New Client Dossier", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Includes date picker & fitting reminder scheduler", fontSize = 11.sp, color = NeonCyan)
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Client Full Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("client_name_input"),
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

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = vipTier,
                    onValueChange = { vipTier = it },
                    label = { Text("VIP Tier") },
                    modifier = Modifier.weight(1f),
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
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone / WhatsApp") },
                    modifier = Modifier.weight(1f),
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
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Date Picker Field
            Text(
                text = "UPCOMING FITTING DATE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            FrostedGlassBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("date_picker_trigger"),
                shape = RoundedCornerShape(12.dp),
                borderColor = NeonViolet,
                onClick = { datePickerDialog.show() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Pick Date", tint = NeonViolet)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Scheduled Fitting Date", fontSize = 11.sp, color = TextMuted)
                            Text(nextFittingDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                    OutlinedButton(
                        onClick = { datePickerDialog.show() },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet)
                    ) {
                        Text("Select Date", fontSize = 11.sp, color = NeonViolet, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notification Scheduler Section
            FrostedGlassBox(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                borderColor = if (scheduleReminder) NeonCyan.copy(alpha = 0.5f) else AtelierBorderGlass
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Fitting Notification Reminder", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Alerts device prior to atelier session", fontSize = 11.sp, color = TextMuted)
                            }
                        }
                        Switch(
                            checked = scheduleReminder,
                            onCheckedChange = { scheduleReminder = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonCyan
                            ),
                            modifier = Modifier.testTag("reminder_switch")
                        )
                    }

                    if (scheduleReminder) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Reminder Lead Time:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            reminderOptions.forEach { opt ->
                                val isSelected = reminderLead == opt
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { reminderLead = opt },
                                    label = { Text(opt, fontSize = 10.sp, color = if (isSelected) Color.White else TextSecondary) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = NeonCyanDark,
                                        containerColor = AtelierSurfaceElevated
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = if (isSelected) NeonCyan else AtelierBorderGlass
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = measurements,
                onValueChange = { measurements = it },
                label = { Text("Measurements (Bust, Waist, Hips, Inseam)") },
                modifier = Modifier.fillMaxWidth(),
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
                value = fittingNotes,
                onValueChange = { fittingNotes = it },
                label = { Text("Fitting & Alteration Notes") },
                placeholder = { Text("Hem adjustments, fabric preferences, posture observations...") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonViolet,
                    unfocusedBorderColor = AtelierBorderGlass,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = AtelierSurfaceElevated,
                    unfocusedContainerColor = AtelierSurfaceElevated
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    onSubmit(name, vipTier, email, phone, measurements, fittingNotes, selectedImageUri, nextFittingDate, scheduleReminder, reminderLead)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_client_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.EventAvailable, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Client & Schedule Reminder", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
