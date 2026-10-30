package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CuttingEngine
import com.example.model.FabricBoltWidth
import com.example.model.FabricNapOption
import com.example.model.Garment
import com.example.model.MasterBodyMeasurements
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechPackExportSheet(
    garment: Garment,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val pieces = remember(garment) {
        CuttingEngine.generatePiecesForGarment(garment.title, garment.category, garment.silhouette)
    }

    val estimate = remember(pieces) {
        CuttingEngine.calculateEstimate(pieces, FabricBoltWidth.WIDTH_54, FabricNapOption.STANDARD)
    }

    val requiredMeasurements = remember(garment) {
        val reqIds = MasterBodyMeasurements.getRequiredIdsForGarment(garment.category, garment.silhouette)
        MasterBodyMeasurements.ALL_49.filter { reqIds.contains(it.id) }
    }

    val dateFormatted = SimpleDateFormat("MMM dd, yyyy", Locale.US).format(Date())

    val techPackText = remember(garment, pieces, estimate, requiredMeasurements) {
        buildString {
            appendLine("=====================================================")
            appendLine("           ATELIER HAUTE COUTURE TECH PACK           ")
            appendLine("=====================================================")
            appendLine("Garment Title   : ${garment.title}")
            appendLine("Category        : ${garment.category}")
            appendLine("Silhouette      : ${garment.silhouette}")
            appendLine("Primary Fabric  : ${garment.fabric}")
            appendLine("Designer/Studio : ${garment.designer}")
            appendLine("Date Generated  : $dateFormatted")
            appendLine()
            appendLine("-----------------------------------------------------")
            appendLine("1. BILL OF MATERIALS & YARDAGE SPECIFICATION")
            appendLine("-----------------------------------------------------")
            appendLine("Fabric Bolt Width : ${estimate.boltWidth.inchesLabel}")
            appendLine("Length Required   : ${String.format("%.2f", estimate.lengthYards)} Yards (${String.format("%.2f", estimate.lengthMeters)} Meters)")
            appendLine("Cutting Waste Est : ${String.format("%.1f", estimate.wastePercent)}%")
            appendLine("Nap Directives    : ${estimate.cuttingDirectiveSummary}")
            appendLine()
            appendLine("-----------------------------------------------------")
            appendLine("2. PATTERN PIECES & CUTTING DIRECTIVES (${pieces.sumOf { it.quantity }} TOTAL PIECES)")
            appendLine("-----------------------------------------------------")
            pieces.forEachIndexed { idx, p ->
                appendLine("${idx + 1}. ${p.name} [CUT ${p.quantity}]")
                appendLine("   Dimensions : ${p.widthCm.toInt()}cm x ${p.heightCm.toInt()}cm (${String.format("%.1f", p.widthCm / 2.54f)}\" x ${String.format("%.1f", p.heightCm / 2.54f)}\")")
                appendLine("   Grainline  : ${p.grainline}")
                appendLine("   Seam Allow : ${p.seamAllowanceCm}cm")
                p.directives.forEach { d -> appendLine("   • Directive: $d") }
            }
            appendLine()
            appendLine("-----------------------------------------------------")
            appendLine("3. ANATOMICAL FIT & SPEC MEASUREMENTS")
            appendLine("-----------------------------------------------------")
            requiredMeasurements.forEach { m ->
                appendLine("• ${m.name.padEnd(30)} : ${m.defaultCm.toInt()} cm  |  ${String.format("%.1f", m.defaultCm / 2.54f)} in")
            }
            appendLine()
            appendLine("-----------------------------------------------------")
            appendLine("4. ATELIER FINISHING DIRECTIVES")
            appendLine("-----------------------------------------------------")
            appendLine("• Seam Finishes: French Seams on sheer layers; bound silk bias on shell seams.")
            appendLine("• Stitch Pitch : 12-14 Stitches Per Inch (SPI) on fine silk lamé / organza.")
            appendLine("• Hemline      : Hand-rolled blind couture hemline with 2.5cm horsehair braid insert.")
            appendLine("=====================================================")
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = AtelierBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        tint = CyberGold,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Export Couture Tech Pack",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Standard ISO Specification • Ready for cutting & production",
                            fontSize = 11.sp,
                            color = NeonCyan
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Spec Text Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF07070A))
                    .border(1.dp, AtelierBorderGlass, RoundedCornerShape(12.dp))
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = techPackText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFD4D4E0),
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons (Copy & Share)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Tech Pack Spec - ${garment.title}", techPackText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Tech Pack copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("copy_tech_pack_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberGold),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Spec", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TITLE, "Couture Tech Pack: ${garment.title}")
                            putExtra(Intent.EXTRA_SUBJECT, "Tech Pack Specification: ${garment.title}")
                            putExtra(Intent.EXTRA_TEXT, techPackText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(sendIntent, "Share Couture Tech Pack"))
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("share_tech_pack_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share Spec", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
