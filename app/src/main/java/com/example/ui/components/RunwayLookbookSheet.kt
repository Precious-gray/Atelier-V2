package com.example.ui.components

import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import coil.compose.AsyncImage
import com.example.model.AiPlatform
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
fun RunwayLookbookSheet(
    archiveGarments: List<Garment>,
    clients: List<ClientDossier> = emptyList(),
    onSelectGarment: (Garment) -> Unit,
    onAddCustomGarment: ((title: String, category: String, description: String, imageUri: Uri?, fabric: String, silhouette: String) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var searchQuery by remember { mutableStateOf("") }
    var showAddSheet by remember { mutableStateOf(false) }

    // User-added looks ONLY: custom garments + client private garments
    val userAddedLooks = remember(archiveGarments, clients) {
        val customFromGarments = archiveGarments.filter { it.isCustom || it.createdBy != null }
        val customFromClients = clients.flatMap { client ->
            client.chosenGarments.map { garment ->
                garment.copy(
                    category = "Client: ${client.name}",
                    isCustom = true
                )
            }
        }
        (customFromGarments + customFromClients).distinctBy { it.id.ifBlank { it.title } }
    }

    val filteredLooks = remember(searchQuery, userAddedLooks) {
        if (searchQuery.isBlank()) userAddedLooks
        else userAddedLooks.filter { g ->
            g.title.contains(searchQuery, ignoreCase = true) ||
                    g.category.contains(searchQuery, ignoreCase = true) ||
                    g.silhouette.contains(searchQuery, ignoreCase = true) ||
                    g.fabric.contains(searchQuery, ignoreCase = true)
        }
    }

    fun launchExternalSearch(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }

    if (showAddSheet) {
        AddGarmentSheet(
            isUploading = false,
            onDismiss = { showAddSheet = false },
            onSubmit = { title, category, description, imageUri, fabric, silhouette ->
                onAddCustomGarment?.invoke(title, category, description, imageUri, fabric, silhouette)
                showAddSheet = false
                Toast.makeText(context, "Look '$title' added to Runway Lookbook!", Toast.LENGTH_SHORT).show()
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
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            // Header Bar - RUNWAY LOOKBOOK (Archive word removed)
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
                            imageVector = Icons.Default.ViewInAr,
                            contentDescription = null,
                            tint = CyberGold,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RUNWAY LOOKBOOK",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Live Runway Inspiration Hubs & Custom User / Client Looks (${filteredLooks.size})",
                        fontSize = 11.sp,
                        color = CyberGold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(AtelierSurfaceElevated)
                        .testTag("close_lookbook_sheet")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Live External Runway Inspiration Hubs
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "LIVE RUNWAY INSPIRATION HUBS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Button(
                            onClick = {
                                launchExternalSearch("https://www.google.com/search?tbm=isch&q=haute+couture+runway+fashion+week+trends")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet.copy(alpha = 0.25f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonViolet),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(38.dp).testTag("lookbook_google_inspiration_link")
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Google Runway", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(12.dp))
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                launchExternalSearch("https://www.pinterest.com/search/pins/?q=haute%20couture%20runway%20lookbook")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGold.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyberGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(38.dp).testTag("lookbook_pinterest_inspiration_link")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyberGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pinterest Feed", fontSize = 11.sp, color = CyberGold, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = CyberGold, modifier = Modifier.size(12.dp))
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                launchExternalSearch("https://www.youtube.com/results?search_query=haute+couture+runway+shows+fashion+week")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonMagenta.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonMagenta),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text("YouTube Runway", fontSize = 11.sp, color = NeonMagenta, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = NeonMagenta, modifier = Modifier.size(12.dp))
                        }
                    }

                    item {
                        Button(
                            onClick = {
                                launchExternalSearch("https://www.vogue.com/runway")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan.copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(38.dp)
                        ) {
                            Text("Vogue Runway", fontSize = 11.sp, color = NeonCyan, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.OpenInNew, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // User Action Bar: Add Look from Phone Gallery
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOM RUNWAY LOOKS (${userAddedLooks.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 0.5.sp
                )

                Button(
                    onClick = { showAddSheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("add_look_from_phone_button")
                ) {
                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Look from Phone", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search User Looks TextField
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search your custom runway looks...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("lookbook_search_input"),
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

            Spacer(modifier = Modifier.height(12.dp))

            // User's Custom Looks Grid
            if (filteredLooks.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(24.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AtelierSurfaceCard)
                        .border(1.dp, AtelierBorderGlass, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(NeonViolet.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No Custom Runway Looks Added Yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap 'Add Look from Phone' above to upload a photo from your gallery, or assign garments from your client pages to display them here.",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showAddSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonViolet),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload Look from Phone", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    items(filteredLooks, key = { it.id }) { garment ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(AtelierSurfaceCard)
                                .border(1.dp, AtelierBorderGlass, RoundedCornerShape(14.dp))
                                .clickable {
                                    onDismiss()
                                    onSelectGarment(garment)
                                }
                        ) {
                            Column {
                                Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                                    AsyncImage(
                                        model = garment.imageUrl.ifBlank { "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=400&q=80" },
                                        contentDescription = garment.title,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(NeonViolet)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("User Look", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = garment.title,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${garment.category} • ${garment.silhouette}",
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
