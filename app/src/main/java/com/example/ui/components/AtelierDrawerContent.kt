package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
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

import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class DrawerFeature {
    THEME_CUSTOMIZER,
    PATTERN_DRAFTING,
    FABRIC_INDEX,
    MEASUREMENTS_GUIDE,
    FITTING_SCHEDULE,
    LOOKBOOK_SHOWCASE,
    SECURITY_OVERVIEW
}

@Composable
fun AtelierDrawerContent(
    currentUid: String,
    userName: String = "Haute Couture Designer",
    onUpdateUserName: ((String) -> Unit)? = null,
    onClose: () -> Unit,
    onFeatureSelected: (DrawerFeature) -> Unit
) {
    var isEditingName by remember { mutableStateOf(false) }
    var tempName by remember(userName) { mutableStateOf(userName) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(310.dp)
            .background(AtelierBackground)
            .border(width = 1.dp, color = AtelierBorderGlass)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .testTag("atelier_drawer_menu")
    ) {
        // Drawer Header with Close Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.dp, NeonViolet, CircleShape)
                        .padding(2.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_atelier_scissors_logo_1792658287419),
                        contentDescription = "Atelier Dressmakers Emblem",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "ATELIER",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Studio Management",
                        fontSize = 11.sp,
                        color = CyberGold
                    )
                }
            }

            IconButton(onClick = onClose, modifier = Modifier.testTag("close_drawer_button")) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Menu",
                    tint = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Device Auth Banner in Drawer with Editable User Name
        FrostedGlassBox(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            borderColor = NeonViolet.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AUTHENTICATED ATELIER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 0.5.sp
                    )

                    if (onUpdateUserName != null) {
                        Icon(
                            imageVector = if (isEditingName) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = "Edit Name",
                            tint = CyberGold,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    if (isEditingName) {
                                        onUpdateUserName(tempName)
                                        isEditingName = false
                                    } else {
                                        isEditingName = true
                                    }
                                }
                                .testTag("edit_drawer_user_name_icon")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (isEditingName) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("drawer_user_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberGold,
                            unfocusedBorderColor = AtelierBorderGlass,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = AtelierSurfaceElevated,
                            unfocusedContainerColor = AtelierSurfaceElevated
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Text(
                        text = userName,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "UID: ${currentUid.take(12)}...",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "COUTURE ATELIER TOOLS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        DrawerMenuItem(
            icon = Icons.Default.Palette,
            title = "Atelier Themes & Colors",
            subtitle = "6 luxury presets • custom accent studio",
            accentColor = CyberGold,
            onClick = { onFeatureSelected(DrawerFeature.THEME_CUSTOMIZER) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem(
            icon = Icons.Default.DesignServices,
            title = "Pattern Drafting & Slopers",
            subtitle = "Parametric cuts • patronIA & Aiclo",
            accentColor = NeonCyan,
            onClick = { onFeatureSelected(DrawerFeature.PATTERN_DRAFTING) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem(
            icon = Icons.Default.ViewInAr,
            title = "Fabric & Material Index",
            subtitle = "Specs on lamé, silk, optic fiber",
            accentColor = NeonCyan,
            onClick = { onFeatureSelected(DrawerFeature.FABRIC_INDEX) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem(
            icon = Icons.Default.Straighten,
            title = "Bespoke Measurement Guide",
            subtitle = "Anatomical sizing & conversion",
            accentColor = NeonViolet,
            onClick = { onFeatureSelected(DrawerFeature.MEASUREMENTS_GUIDE) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem(
            icon = Icons.Default.CalendarMonth,
            title = "Couture Fitting Calendar",
            subtitle = "Client timeline & runway calls",
            accentColor = CyberGold,
            onClick = { onFeatureSelected(DrawerFeature.FITTING_SCHEDULE) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem(
            icon = Icons.Default.MenuBook,
            title = "Runway Lookbook",
            subtitle = "Curated seasonal collection",
            accentColor = NeonCyan,
            onClick = { onFeatureSelected(DrawerFeature.LOOKBOOK_SHOWCASE) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        DrawerMenuItem(
            icon = Icons.Default.Security,
            title = "Cloud Security Topology",
            subtitle = "Zero-trust rule inspection",
            accentColor = NeonViolet,
            onClick = { onFeatureSelected(DrawerFeature.SECURITY_OVERVIEW) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Divider(color = AtelierBorderGlass, thickness = 1.dp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Atelier v1.0 • Haute Couture Studio",
            fontSize = 11.sp,
            color = TextMuted
        )
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    FrostedGlassBox(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}
