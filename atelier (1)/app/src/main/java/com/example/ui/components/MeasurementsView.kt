package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BodyMeasurementCategory
import com.example.model.MasterBodyMeasurements
import com.example.model.MeasurementDefinition
import com.example.model.MeasurementUnit
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementsView(
    category: String,
    silhouette: String,
    modifier: Modifier = Modifier,
    initialCustomMeasurements: Map<String, Float> = emptyMap(),
    onSaveToDossier: ((String) -> Unit)? = null,
    onMeasurementsChanged: ((Map<String, Float>) -> Unit)? = null
) {
    val context = LocalContext.current
    var selectedUnit by remember { mutableStateOf(MeasurementUnit.CM) }
    var showAddPickerSheet by remember { mutableStateOf(false) }
    var selectedInfoDefinition by remember { mutableStateOf<MeasurementDefinition?>(null) }
    var selectedEditDefinition by remember { mutableStateOf<MeasurementDefinition?>(null) }

    // Required set of keys for this garment
    val defaultRequiredIds = remember(category, silhouette) {
        MasterBodyMeasurements.getRequiredIdsForGarment(category, silhouette)
    }

    // Active displayed IDs (defaults to required + any custom measurement keys)
    val activeIds = remember {
        val initialKeys = defaultRequiredIds.toMutableSet()
        initialKeys.addAll(initialCustomMeasurements.keys)
        mutableStateOf(initialKeys)
    }

    // Measurement values stored in CM
    val measurementValuesCm = remember {
        val map = mutableStateMapOf<String, Float>()
        MasterBodyMeasurements.ALL_49.forEach { def ->
            map[def.id] = initialCustomMeasurements[def.id] ?: def.defaultCm
        }
        map
    }

    val displayedDefinitions = remember(activeIds.value) {
        MasterBodyMeasurements.ALL_49.filter { activeIds.value.contains(it.id) }
    }

    val hasCustomizedValues = remember(measurementValuesCm.values.toList()) {
        displayedDefinitions.any { def ->
            val cur = measurementValuesCm[def.id] ?: def.defaultCm
            kotlin.math.abs(cur - def.defaultCm) > 0.05f
        }
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
                        Icons.Default.Straighten,
                        contentDescription = "Measurements",
                        tint = NeonViolet,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Anatomical Body Measurements",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Text(
                    text = "Tap any metric to edit • Showing ${displayedDefinitions.size} of 49 Points",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }

            // Unit Switcher (cm vs inches)
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
                        .background(if (selectedUnit == MeasurementUnit.CM) NeonViolet else Color.Transparent)
                        .clickable { selectedUnit = MeasurementUnit.CM }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("measurements_unit_cm")
                ) {
                    Text(
                        "CM",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedUnit == MeasurementUnit.CM) Color.White else TextSecondary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selectedUnit == MeasurementUnit.INCHES) NeonViolet else Color.Transparent)
                        .clickable { selectedUnit = MeasurementUnit.INCHES }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("measurements_unit_in")
                ) {
                    Text(
                        "IN",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedUnit == MeasurementUnit.INCHES) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons: "+ Add from 49 Master List" & Reset/Save options
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showAddPickerSheet = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonViolet.copy(alpha = 0.25f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("add_measurement_from_list_button")
            ) {
                Icon(Icons.Default.PlaylistAdd, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("+ Add from 49 List", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            }

            if (hasCustomizedValues) {
                OutlinedButton(
                    onClick = {
                        displayedDefinitions.forEach { def ->
                            measurementValuesCm[def.id] = def.defaultCm
                        }
                        onMeasurementsChanged?.invoke(measurementValuesCm.toMap())
                        Toast.makeText(context, "All measurements reset to standard baseline", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.height(40.dp).testTag("reset_all_measurements_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderGlass)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset", fontSize = 11.sp)
                }
            }

            if (onSaveToDossier != null) {
                Button(
                    onClick = {
                        val formattedSummary = displayedDefinitions.joinToString(" | ") { def ->
                            val valCm = measurementValuesCm[def.id] ?: def.defaultCm
                            if (selectedUnit == MeasurementUnit.CM) "${def.name}: ${valCm.toInt()}cm"
                            else "${def.name}: ${String.format(Locale.US, "%.1f", valCm / 2.54f)}in"
                        }
                        onSaveToDossier(formattedSummary)
                        Toast.makeText(context, "Saved measurements to dossier!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberGold.copy(alpha = 0.2f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Save Dossier", fontSize = 11.sp, color = CyberGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Displayed Measurements Grid / List with Tap-to-Edit
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            displayedDefinitions.forEach { def ->
                val currentValCm = measurementValuesCm[def.id] ?: def.defaultCm
                val isRequired = defaultRequiredIds.contains(def.id)
                val isCustomized = kotlin.math.abs(currentValCm - def.defaultCm) > 0.05f

                val displayValue = if (selectedUnit == MeasurementUnit.CM) {
                    "${String.format(Locale.US, "%.1f", currentValCm)} cm"
                } else {
                    "${String.format(Locale.US, "%.2f", currentValCm / 2.54f)} in"
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCustomized) NeonViolet.copy(alpha = 0.08f) else AtelierSurfaceElevated)
                        .border(
                            width = 1.dp,
                            color = if (isCustomized) NeonViolet.copy(alpha = 0.5f) else AtelierBorderGlass,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable {
                            selectedEditDefinition = def
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                        .testTag("measurement_item_${def.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = def.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                if (isRequired) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(NeonCyan.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("Required", fontSize = 9.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                                    }
                                }
                                if (isCustomized) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CyberGold.copy(alpha = 0.2f))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text("Custom Fit", fontSize = 9.sp, color = CyberGold, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = "${def.category.title} • Tap to edit value",
                                fontSize = 10.sp,
                                color = TextMuted
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Clickable Editable Value Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.45f))
                                    .border(1.dp, if (isCustomized) CyberGold else AtelierBorderGlass, RoundedCornerShape(8.dp))
                                    .clickable { selectedEditDefinition = def }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = displayValue,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        color = CyberGold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Measurement",
                                        tint = if (isCustomized) CyberGold else TextSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            // Anatomical Info Button
                            IconButton(
                                onClick = { selectedInfoDefinition = def },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Anatomical Landmark Guide",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }

                            // If not in required defaults, allow removing
                            if (!isRequired) {
                                IconButton(
                                    onClick = {
                                        val updated = activeIds.value.toMutableSet()
                                        updated.remove(def.id)
                                        activeIds.value = updated
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        tint = TextMuted,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Dialog: Edit Measurement Value with Real-Time Conversion & Stepper
    if (selectedEditDefinition != null) {
        val def = selectedEditDefinition!!
        val currentCm = measurementValuesCm[def.id] ?: def.defaultCm
        var editUnit by remember { mutableStateOf(selectedUnit) }
        var inputString by remember {
            val initVal = if (selectedUnit == MeasurementUnit.CM) currentCm else currentCm / 2.54f
            mutableStateOf(String.format(Locale.US, if (selectedUnit == MeasurementUnit.CM) "%.1f" else "%.2f", initVal))
        }

        AlertDialog(
            onDismissRequest = { selectedEditDefinition = null },
            containerColor = AtelierBackground,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Edit Measurement",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = def.name,
                            fontSize = 12.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    IconButton(onClick = { selectedEditDefinition = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = def.anatomicalDescription,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Unit Selection Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Input Unit:", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)

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
                                    .background(if (editUnit == MeasurementUnit.CM) NeonViolet else Color.Transparent)
                                    .clickable {
                                        if (editUnit != MeasurementUnit.CM) {
                                            val currentEntered = inputString.toFloatOrNull() ?: 0f
                                            val converted = currentEntered * 2.54f
                                            inputString = String.format(Locale.US, "%.1f", converted)
                                            editUnit = MeasurementUnit.CM
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Centimeters (cm)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (editUnit == MeasurementUnit.CM) Color.White else TextSecondary)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (editUnit == MeasurementUnit.INCHES) NeonViolet else Color.Transparent)
                                    .clickable {
                                        if (editUnit != MeasurementUnit.INCHES) {
                                            val currentEntered = inputString.toFloatOrNull() ?: 0f
                                            val converted = currentEntered / 2.54f
                                            inputString = String.format(Locale.US, "%.2f", converted)
                                            editUnit = MeasurementUnit.INCHES
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Inches (in)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (editUnit == MeasurementUnit.INCHES) Color.White else TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Main Numeric Input TextField
                    OutlinedTextField(
                        value = inputString,
                        onValueChange = { inputString = it },
                        label = { Text("Exact Measurement Value (${if (editUnit == MeasurementUnit.CM) "cm" else "in"})") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("measurement_input_field"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGold,
                            unfocusedBorderColor = AtelierBorderGlass,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = AtelierSurfaceElevated,
                            unfocusedContainerColor = AtelierSurfaceElevated
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Dual Unit Conversion & Comparison
                    val parsedVal = inputString.toFloatOrNull() ?: 0f
                    val enteredInCm = if (editUnit == MeasurementUnit.CM) parsedVal else parsedVal * 2.54f
                    val enteredInInches = if (editUnit == MeasurementUnit.INCHES) parsedVal else parsedVal / 2.54f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AtelierSurfaceElevated)
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Equivalent Sizing:", fontSize = 11.sp, color = TextMuted)
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", enteredInCm)} cm  •  ${String.format(Locale.US, "%.2f", enteredInInches)} in",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberGold
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            val diffCm = enteredInCm - def.defaultCm
                            val diffStr = if (kotlin.math.abs(diffCm) < 0.1f) "Standard Baseline" else if (diffCm > 0) "+${String.format(Locale.US, "%.1f", diffCm)} cm from baseline" else "${String.format(Locale.US, "%.1f", diffCm)} cm from baseline"
                            Text(
                                text = "Baseline (${def.defaultCm} cm): $diffStr",
                                fontSize = 10.sp,
                                color = if (kotlin.math.abs(diffCm) < 0.1f) TextMuted else NeonCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stepper Adjustment Buttons (-1, -0.5, +0.5, +1)
                    Text("Couture Micro-Adjustments:", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(-1.0f, -0.5f, 0.5f, 1.0f).forEach { delta ->
                            val label = if (delta > 0) "+$delta" else "$delta"
                            OutlinedButton(
                                onClick = {
                                    val current = inputString.toFloatOrNull() ?: 0f
                                    val newVal = (current + delta).coerceAtLeast(0.5f)
                                    inputString = String.format(Locale.US, if (editUnit == MeasurementUnit.CM) "%.1f" else "%.2f", newVal)
                                },
                                modifier = Modifier.weight(1f).height(34.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderGlass),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
                            ) {
                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            inputString = String.format(Locale.US, if (editUnit == MeasurementUnit.CM) "%.1f" else "%.2f", if (editUnit == MeasurementUnit.CM) def.defaultCm else def.defaultCm / 2.54f)
                        },
                        modifier = Modifier.weight(1f).height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AtelierBorderGlass)
                    ) {
                        Text("Reset Baseline", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val parsed = inputString.toFloatOrNull()
                            if (parsed != null && parsed > 0f) {
                                val finalCm = if (editUnit == MeasurementUnit.CM) parsed else parsed * 2.54f
                                measurementValuesCm[def.id] = finalCm
                                onMeasurementsChanged?.invoke(measurementValuesCm.toMap())
                                Toast.makeText(context, "Saved ${def.name}: ${String.format(Locale.US, "%.1f", finalCm)} cm", Toast.LENGTH_SHORT).show()
                                selectedEditDefinition = null
                            } else {
                                Toast.makeText(context, "Please enter a valid numeric measurement", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1.2f).height(44.dp).testTag("save_measurement_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Fitting", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }

    // Modal Bottom Sheet: Full 49 Master List Picker
    if (showAddPickerSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showAddPickerSheet = false },
            containerColor = AtelierBackground
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "49 Master Anatomical Measurements",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Select any couture measurement to add to this garment",
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }
                    IconButton(onClick = { showAddPickerSheet = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BodyMeasurementCategory.entries.forEach { cat ->
                        val itemsInCat = MasterBodyMeasurements.ALL_49.filter { it.category == cat }
                        if (itemsInCat.isNotEmpty()) {
                            item {
                                Text(
                                    text = cat.title.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberGold,
                                    modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                )
                            }

                            items(itemsInCat) { def ->
                                val isAdded = activeIds.value.contains(def.id)
                                val valDisplay = if (selectedUnit == MeasurementUnit.CM) "${def.defaultCm.toInt()} cm" else "${String.format(Locale.US, "%.1f", def.defaultCm / 2.54f)} in"

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isAdded) NeonViolet.copy(alpha = 0.15f) else AtelierSurfaceCard)
                                        .border(1.dp, if (isAdded) NeonViolet else AtelierBorderGlass, RoundedCornerShape(10.dp))
                                        .clickable {
                                            val updated = activeIds.value.toMutableSet()
                                            if (isAdded) {
                                                if (!defaultRequiredIds.contains(def.id)) {
                                                    updated.remove(def.id)
                                                }
                                            } else {
                                                updated.add(def.id)
                                            }
                                            activeIds.value = updated
                                        }
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(def.name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                            Text(def.anatomicalDescription, fontSize = 10.sp, color = TextMuted, maxLines = 2)
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(valDisplay, fontSize = 12.sp, color = CyberGold, fontFamily = FontFamily.Monospace)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(
                                                imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                                                contentDescription = null,
                                                tint = if (isAdded) NeonCyan else TextSecondary,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showAddPickerSheet = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Done (${activeIds.value.size} Selected)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Anatomical Landmark Guide Dialog
    if (selectedInfoDefinition != null) {
        val def = selectedInfoDefinition!!
        AlertDialog(
            onDismissRequest = { selectedInfoDefinition = null },
            containerColor = AtelierBackground,
            title = {
                Text(def.name, color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        text = "Category: ${def.category.title}",
                        fontSize = 11.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = def.anatomicalDescription,
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Standard Baseline: ${def.defaultCm} cm (${String.format(Locale.US, "%.1f", def.defaultCm / 2.54f)} in)",
                        fontSize = 12.sp,
                        color = CyberGold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedInfoDefinition = null }) {
                    Text("Close", color = NeonViolet, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
