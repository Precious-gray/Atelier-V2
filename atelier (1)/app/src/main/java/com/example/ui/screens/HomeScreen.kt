package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.components.AtelierThemeDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.model.Garment
import com.example.ui.components.AddCategoryDialog
import com.example.ui.components.AddGarmentSheet
import com.example.ui.components.FrostedGlassBox
import com.example.ui.components.GarmentDetailSheet
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
fun HomeScreen(
    viewModel: AtelierViewModel,
    onOpenMenu: () -> Unit = {},
    onNavigateToGenerateTab: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val feed by viewModel.filteredFeed.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val selectedGarment by viewModel.selectedGarment.collectAsStateWithLifecycle()
    val isUploading by viewModel.isUploadingImage.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val customCategories by viewModel.categories.collectAsStateWithLifecycle()
    val currentThemePreset by viewModel.currentThemePreset.collectAsStateWithLifecycle()
    val customAccentColor by viewModel.customAccentColor.collectAsStateWithLifecycle()

    var showAddSheet by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    if (showThemeDialog) {
        AtelierThemeDialog(
            currentPreset = currentThemePreset,
            customAccent = customAccentColor,
            onSelectPreset = { preset -> viewModel.setThemePreset(preset) },
            onSelectAccent = { color -> viewModel.setCustomAccentColor(color) },
            onDismiss = { showThemeDialog = false }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AtelierBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top App Bar with Three Horizontal Lines (Menu), Atelier Scissors Emblem, Theme Switcher & Editable User Profile Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Three horizontal lines menu button at top left
                    IconButton(
                        onClick = onOpenMenu,
                        modifier = Modifier.testTag("top_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Studio Menu",
                            tint = TextPrimary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Dressmaker Scissors Emblem Logo
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(NeonViolet.copy(alpha = 0.2f))
                            .border(1.dp, NeonViolet, CircleShape)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_atelier_scissors_logo_1792658287419),
                            contentDescription = "Dressmaker Scissors logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "ATELIER",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "Dressmaker & Couture",
                            fontSize = 10.sp,
                            color = NeonCyan
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Theme Switcher Button
                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AtelierSurfaceElevated)
                            .border(1.dp, AtelierBorderGlass, CircleShape)
                            .size(32.dp)
                            .testTag("theme_picker_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Themes & Colors",
                            tint = CyberGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // User Identity indicator badge (displays custom name entered in hamburger area)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AtelierSurfaceElevated)
                            .border(1.dp, AtelierBorderGlass, RoundedCornerShape(20.dp))
                            .clickable { onOpenMenu() }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("top_user_profile_badge")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Authenticated Atelier User",
                                tint = NeonCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = userName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            // Search Bar (Frosted glass look)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Search garments, fabrics, designers...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setSearchQuery("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("home_search_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonViolet,
                    unfocusedBorderColor = AtelierBorderGlass,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = AtelierSurfaceCard,
                    unfocusedContainerColor = AtelierSurfaceCard
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" filter chip
                item {
                    val isSelected = selectedCategory == "All"
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedCategory("All") },
                        label = {
                            Text(
                                text = "All",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonViolet,
                            containerColor = AtelierSurfaceElevated
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Color.Transparent else AtelierBorderGlass
                        )
                    )
                }

                // "My Creations" filter chip
                item {
                    val isSelected = selectedCategory == "My Creations"
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedCategory("My Creations") },
                        label = {
                            Text(
                                text = "My Creations",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonCyan.copy(alpha = 0.8f),
                            containerColor = AtelierSurfaceElevated
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Color.Transparent else AtelierBorderGlass
                        )
                    )
                }

                // Dynamic Categories
                items(customCategories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedCategory(category) },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonViolet,
                            containerColor = AtelierSurfaceElevated
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) Color.Transparent else AtelierBorderGlass
                        )
                    )
                }

                // "+ Add Category" Chip Button
                item {
                    FilterChip(
                        selected = false,
                        onClick = { showAddCategoryDialog = true },
                        label = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Category",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add Category",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NeonCyan
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = AtelierSurfaceElevated
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = false,
                            borderColor = NeonCyan.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.testTag("add_category_filter_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Masonry Feed: LazyVerticalStaggeredGrid (as requested in spec)
            if (feed.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No garments found in this view",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try switching categories or tap '+' to create one",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("masonry_garment_feed"),
                    contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 88.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalItemSpacing = 10.dp
                ) {
                    items(feed, key = { it.id }) { garment ->
                        GarmentMasonryCard(
                            garment = garment,
                            isOwner = viewModel.isUserOwner(garment),
                            onClick = { viewModel.selectGarment(garment) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Custom Garment
        FloatingActionButton(
            onClick = { showAddSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp, end = 20.dp)
                .testTag("add_garment_fab"),
            containerColor = NeonViolet,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add custom garment")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Design", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        // Sheets & Dialogs
        if (showAddSheet) {
            AddGarmentSheet(
                isUploading = isUploading,
                categories = customCategories,
                onAddCategory = { newCategory ->
                    viewModel.addCategory(newCategory, selectAfterAdding = false)
                },
                onDismiss = { showAddSheet = false },
                onSubmit = { title, category, description, imageUri, fabric, silhouette ->
                    viewModel.createCustomGarment(
                        title = title,
                        category = category,
                        description = description,
                        imageUri = imageUri,
                        fabric = fabric,
                        silhouette = silhouette,
                        onSuccess = { showAddSheet = false }
                    )
                }
            )
        }

        val clients by viewModel.clients.collectAsStateWithLifecycle()

        selectedGarment?.let { garment ->
            GarmentDetailSheet(
                garment = garment,
                isOwner = viewModel.isUserOwner(garment),
                categories = customCategories,
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

        if (showAddCategoryDialog) {
            AddCategoryDialog(
                onDismiss = { showAddCategoryDialog = false },
                onAddCategory = { newCategory ->
                    viewModel.addCategory(newCategory, selectAfterAdding = true)
                }
            )
        }
    }
}

@Composable
fun GarmentMasonryCard(
    garment: Garment,
    isOwner: Boolean,
    onClick: () -> Unit
) {
    // Varies card aspect ratio based on ID hash for realistic masonry feel
    val aspect = remember(garment.id) {
        val hash = garment.id.hashCode()
        when (kotlin.math.abs(hash) % 3) {
            0 -> 0.72f
            1 -> 0.85f
            else -> 0.68f
        }
    }

    FrostedGlassBox(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("garment_card_${garment.id}"),
        shape = RoundedCornerShape(14.dp),
        borderColor = if (garment.isCustom) NeonCyan.copy(alpha = 0.5f) else AtelierBorderGlass,
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspect)
            ) {
                AsyncImage(
                    model = garment.imageUrl,
                    contentDescription = garment.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top badges
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (garment.isCustom) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonCyan.copy(alpha = 0.9f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "CUSTOM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AtelierBackground
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AtelierBackground.copy(alpha = 0.75f))
                                .border(0.5.dp, AtelierBorderGlass, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = garment.category.uppercase(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CyberGold
                            )
                        }
                    }

                    // Like counter pill
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AtelierBackground.copy(alpha = 0.7f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = null,
                                tint = NeonMagenta,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${garment.likes}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }

            // Text Info
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = garment.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = garment.silhouette,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
