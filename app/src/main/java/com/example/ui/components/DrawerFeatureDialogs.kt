package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClientDossier
import com.example.model.FabricItem
import com.example.model.Garment
import com.example.model.MeasurementDefinition
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
fun DrawerFeatureSheet(
    feature: DrawerFeature,
    clients: List<ClientDossier> = emptyList(),
    archiveGarments: List<Garment> = emptyList(),
    onOpenClientDossiers: () -> Unit = {},
    onAssignFabricToClient: (ClientDossier, FabricItem) -> Unit = { _, _ -> },
    onApplyMetricToClient: (ClientDossier, MeasurementDefinition) -> Unit = { _, _ -> },
    onSelectGarment: (Garment) -> Unit = {},
    onAddCustomGarment: ((String, String, String, android.net.Uri?, String, String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    when (feature) {
        DrawerFeature.FABRIC_INDEX -> {
            FabricIndexSheet(
                clients = clients,
                onAssignFabricToClient = onAssignFabricToClient,
                onDismiss = onDismiss
            )
        }

        DrawerFeature.MEASUREMENTS_GUIDE -> {
            MeasurementsGuideSheet(
                clients = clients,
                onOpenClientDossiers = onOpenClientDossiers,
                onApplyToClient = onApplyMetricToClient,
                onDismiss = onDismiss
            )
        }

        DrawerFeature.LOOKBOOK_SHOWCASE -> {
            RunwayLookbookSheet(
                archiveGarments = archiveGarments,
                clients = clients,
                onSelectGarment = onSelectGarment,
                onAddCustomGarment = onAddCustomGarment,
                onDismiss = onDismiss
            )
        }

        else -> {
            ModalBottomSheet(
                onDismissRequest = onDismiss,
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
                            Text(
                                text = when (feature) {
                                    DrawerFeature.THEME_CUSTOMIZER -> "Atelier Themes & Color Palettes"
                                    DrawerFeature.PATTERN_DRAFTING -> "Pattern Drafting & Slopers"
                                    DrawerFeature.FITTING_SCHEDULE -> "Couture Fitting Schedule"
                                    DrawerFeature.SECURITY_OVERVIEW -> "Cloud Security Architecture"
                                    else -> "Atelier Feature"
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Atelier Couture Reference",
                                fontSize = 11.sp,
                                color = NeonCyan
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    when (feature) {
                        DrawerFeature.THEME_CUSTOMIZER -> {
                            Text(
                                text = "Atelier Theme Studio",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyberGold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Select from Cyber-Luminescence, Midnight Velvet & Royal Gold, Emerald Haute, Alabaster Atelier, Obsidian Chrome, or Rosewood Damask.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }

                        DrawerFeature.PATTERN_DRAFTING -> {
                            Text(
                                text = "Parametric Sloper Drafting Studio",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Access parametric base slopers for corsets, tailored jackets, raglan sleeves, and couture trains. Modify ease allowances, manipulate darts, and integrate with patronIA & Aiclo AI concept studios.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }

                        DrawerFeature.FITTING_SCHEDULE -> {
                            val schedule = listOf(
                                Triple("Contessa Vespera", "Liquid Mercury Cyber Ballgown", "Nov 12, 2026 • 2:00 PM"),
                                Triple("Lord Julian Drake", "Neon Filigree Bespoke Tuxedo", "Nov 20, 2026 • 4:30 PM"),
                                Triple("Sienna Kincaid", "Obsidian Carbon Corset", "Nov 28, 2026 • 11:00 AM"),
                                Triple("Madame De La Tour", "Gold Pleated Gala Sheath", "Dec 05, 2026 • 3:00 PM")
                            )
                            schedule.forEach { (client, garment, date) ->
                                FrostedGlassBox(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(client, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                            Text(date, fontSize = 11.sp, color = NeonCyan)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(garment, fontSize = 12.sp, color = CyberGold)
                                    }
                                }
                            }
                        }

                        DrawerFeature.SECURITY_OVERVIEW -> {
                            Text(
                                text = "Zero-Trust Hybrid Storage",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "All client records and custom garments are stored under private Firestore path /users/{uid}/... Protected by Firebase Security Rules.",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}
