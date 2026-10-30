package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MasterFabricCatalog
import com.example.model.ClientDossier
import com.example.model.FabricItem
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FabricIndexSheet(
    clients: List<ClientDossier>,
    onAssignFabricToClient: (ClientDossier, FabricItem) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Master list containing 200 standard fabrics + user added custom fabrics
    val customFabrics = remember { mutableStateListOf<FabricItem>() }
    val allFabrics = remember(customFabrics.size) {
        customFabrics + MasterFabricCatalog.FABRICS_200
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    var selectedFabricForDetail by remember { mutableStateOf<FabricItem?>(null) }
    var fabricToAssign by remember { mutableStateOf<FabricItem?>(null) }
    var showAddCustomFabricDialog by remember { mutableStateOf(false) }

    val filteredFabrics = remember(searchQuery, selectedCategory, allFabrics) {
        allFabrics.filter { item ->
            val matchesCat = selectedCategory == "All" || item.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.composition.contains(searchQuery, ignoreCase = true) ||
                    item.weaveType.contains(searchQuery, ignoreCase = true) ||
                    item.bestForGarments.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCat && matchesQuery
        }
    }

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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AtelierSurfaceCard)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "FABRIC & MATERIAL INDEX",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "200+ Haute Couture Textiles • Showing ${filteredFabrics.size} Items",
                        fontSize = 11.sp,
                        color = CyberGold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { showAddCustomFabricDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("add_custom_fabric_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Fabric", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AtelierSurfaceElevated)
                            .testTag("close_fabric_sheet")
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

            // Search input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search 200+ fabrics by name, composition, weave...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("fabric_search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CyberGold,
                    unfocusedBorderColor = AtelierBorderGlass,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = AtelierSurfaceCard,
                    unfocusedContainerColor = AtelierSurfaceCard
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Category Chips Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MasterFabricCatalog.FABRIC_CATEGORIES) { cat ->
                    val isSel = selectedCategory == cat
                    FilterChip(
                        selected = isSel,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CyberGold,
                            selectedLabelColor = AtelierBackground,
                            containerColor = AtelierSurfaceElevated,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSel,
                            borderColor = if (isSel) Color.Transparent else AtelierBorderGlass
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fabrics List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(filteredFabrics, key = { it.id }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AtelierSurfaceCard)
                            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(12.dp))
                            .clickable { selectedFabricForDetail = item }
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = item.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                if (item.isCustom) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NeonViolet.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("Custom", fontSize = 9.sp, color = NeonViolet, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Text(
                                text = "${item.category} • ${item.composition}",
                                fontSize = 11.sp,
                                color = TextMuted
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Weight: ${item.weightGsm} gsm",
                                    fontSize = 11.sp,
                                    color = CyberGold,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Drape: ${item.drapability}",
                                    fontSize = 11.sp,
                                    color = NeonCyan
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Button(
                                onClick = { fabricToAssign = item },
                                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("assign_fabric_button_${item.id}")
                            ) {
                                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Assign", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Fabric Detail Modal Dialog
    selectedFabricForDetail?.let { fabric ->
        AlertDialog(
            onDismissRequest = { selectedFabricForDetail = null },
            containerColor = AtelierBackground,
            shape = RoundedCornerShape(18.dp),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(fabric.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = { selectedFabricForDetail = null }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AtelierSurfaceElevated)
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("• Category: ${fabric.category}", fontSize = 12.sp, color = TextPrimary)
                            Text("• Composition: ${fabric.composition}", fontSize = 12.sp, color = TextPrimary)
                            Text("• Weight: ${fabric.weightGsm} g/m² (GSM)", fontSize = 12.sp, color = CyberGold)
                            Text("• Weave Structure: ${fabric.weaveType}", fontSize = 12.sp, color = TextPrimary)
                            Text("• Drapability: ${fabric.drapability}", fontSize = 12.sp, color = NeonCyan)
                            Text("• Stretch Factor: ${fabric.stretchPercentage}", fontSize = 12.sp, color = TextPrimary)
                            Text("• Recommended Garments: ${fabric.bestForGarments.joinToString(", ")}", fontSize = 12.sp, color = TextSecondary)
                            Text("• Care Instructions: ${fabric.careInstructions}", fontSize = 12.sp, color = TextMuted)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        fabricToAssign = fabric
                        selectedFabricForDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Assign to Client Dossier")
                }
            }
        )
    }

    // Assign Fabric to Client Modal
    fabricToAssign?.let { fab ->
        AlertDialog(
            onDismissRequest = { fabricToAssign = null },
            containerColor = AtelierBackground,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text("Assign '${fab.name}' to Client", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text("Select a client dossier to append this fabric specification:", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(10.dp))

                    if (clients.isEmpty()) {
                        Text("No client dossiers available.", fontSize = 12.sp, color = TextSecondary)
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(clients, key = { it.id }) { client ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AtelierSurfaceElevated)
                                        .clickable {
                                            val updatedNotes = if (client.fittingNotes.isBlank()) {
                                                "Chosen Fabric: ${fab.name} (${fab.composition}, ${fab.weightGsm}gsm)"
                                            } else {
                                                "${client.fittingNotes}\n• Fabric Specification: ${fab.name} (${fab.composition})"
                                            }
                                            onAssignFabricToClient(client.copy(fittingNotes = updatedNotes), fab)
                                            Toast.makeText(context, "Assigned ${fab.name} to ${client.name}'s Dossier!", Toast.LENGTH_SHORT).show()
                                            fabricToAssign = null
                                        }
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(client.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text(client.vipTier, fontSize = 10.sp, color = CyberGold)
                                    }
                                    Button(
                                        onClick = {
                                            val updatedNotes = if (client.fittingNotes.isBlank()) {
                                                "Chosen Fabric: ${fab.name} (${fab.composition}, ${fab.weightGsm}gsm)"
                                            } else {
                                                "${client.fittingNotes}\n• Fabric Specification: ${fab.name} (${fab.composition})"
                                            }
                                            onAssignFabricToClient(client.copy(fittingNotes = updatedNotes), fab)
                                            Toast.makeText(context, "Assigned ${fab.name} to ${client.name}'s Dossier!", Toast.LENGTH_SHORT).show()
                                            fabricToAssign = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Assign", fontSize = 10.sp)
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

    // Add Custom Fabric Dialog
    if (showAddCustomFabricDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("Silk & Satin") }
        var composition by remember { mutableStateOf("") }
        var weight by remember { mutableStateOf("150") }

        AlertDialog(
            onDismissRequest = { showAddCustomFabricDialog = false },
            containerColor = AtelierBackground,
            shape = RoundedCornerShape(18.dp),
            title = { Text("Add Custom Fabric to Catalog", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary) },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Fabric Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("custom_fabric_name_input")
                    )
                    OutlinedTextField(
                        value = composition,
                        onValueChange = { composition = it },
                        label = { Text("Composition (e.g. 100% Silk)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text("Weight (GSM)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val newItem = FabricItem(
                                id = "custom_fab_${System.currentTimeMillis()}",
                                name = name.trim(),
                                category = category,
                                composition = composition.ifBlank { "Custom Material Blend" },
                                weightGsm = weight.toIntOrNull() ?: 150,
                                isCustom = true
                            )
                            customFabrics.add(0, newItem)
                            Toast.makeText(context, "Added '${newItem.name}' to Fabric Catalog!", Toast.LENGTH_SHORT).show()
                            showAddCustomFabricDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                    modifier = Modifier.testTag("save_custom_fabric_button")
                ) {
                    Text("Save Fabric")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCustomFabricDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
