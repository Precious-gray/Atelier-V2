package com.example.ui

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AtelierDrawerContent
import com.example.ui.components.AtelierSplashScreen
import com.example.ui.components.DrawerFeature
import com.example.ui.components.DrawerFeatureSheet
import com.example.ui.screens.GenerateScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.theme.AtelierBackground
import com.example.ui.theme.AtelierBorderGlass
import com.example.ui.theme.AtelierSurfaceCard
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.AtelierViewModel
import kotlinx.coroutines.launch

@Composable
fun AtelierApp(
    viewModel: AtelierViewModel = viewModel()
) {
    val currentThemePreset by viewModel.currentThemePreset.collectAsStateWithLifecycle()
    val customAccentColor by viewModel.customAccentColor.collectAsStateWithLifecycle()

    com.example.ui.theme.MyApplicationTheme(
        preset = currentThemePreset,
        customAccent = customAccentColor
    ) {
        var showSplash by remember { mutableStateOf(true) }

        Crossfade(
            targetState = showSplash,
            animationSpec = tween(durationMillis = 600),
            label = "splash_transition"
        ) { isSplash ->
            if (isSplash) {
                AtelierSplashScreen(
                    onFinished = { showSplash = false }
                )
            } else {
                AtelierMainScaffold(viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun AtelierMainScaffold(
    viewModel: AtelierViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: AtelierTab.Home.route

    val context = LocalContext.current
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    var selectedDrawerFeature by remember { mutableStateOf<DrawerFeature?>(null) }

    val currentUid = currentUser?.uid ?: "local_guest_uid"
    val userName by viewModel.userName.collectAsStateWithLifecycle()
    val currentThemePreset by viewModel.currentThemePreset.collectAsStateWithLifecycle()
    val customAccentColor by viewModel.customAccentColor.collectAsStateWithLifecycle()

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.Transparent,
                drawerTonalElevation = 0.dp
            ) {
                AtelierDrawerContent(
                    currentUid = currentUid,
                    userName = userName,
                    onUpdateUserName = { newName -> viewModel.setUserName(newName) },
                    onClose = {
                        coroutineScope.launch { drawerState.close() }
                    },
                    onFeatureSelected = { feature ->
                        coroutineScope.launch { drawerState.close() }
                        selectedDrawerFeature = feature
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(AtelierBackground),
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                // Frosted Leonardo.ai Bottom Navigation Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = AtelierBorderGlass
                        )
                ) {
                    NavigationBar(
                        containerColor = AtelierSurfaceCard.copy(alpha = 0.95f),
                        contentColor = TextPrimary,
                        tonalElevation = 0.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        AtelierTab.items.forEach { tab ->
                            val isSelected = currentRoute == tab.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != tab.route) {
                                        navController.navigate(tab.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                        contentDescription = tab.title,
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color.White,
                                    selectedTextColor = Color.White,
                                    indicatorColor = NeonViolet,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.route}")
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AtelierTab.Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(AtelierTab.Home.route) {
                    HomeScreen(
                        viewModel = viewModel,
                        onOpenMenu = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onNavigateToGenerateTab = {
                            navController.navigate(AtelierTab.Generate.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(AtelierTab.Generate.route) {
                    GenerateScreen(viewModel = viewModel)
                }
                composable(AtelierTab.Library.route) {
                    LibraryScreen(
                        viewModel = viewModel,
                        onNavigateToGenerateTab = {
                            navController.navigate(AtelierTab.Generate.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable(AtelierTab.More.route) {
                    MoreScreen(viewModel = viewModel)
                }
            }
        }
    }

    selectedDrawerFeature?.let { feature ->
        when (feature) {
            DrawerFeature.THEME_CUSTOMIZER -> {
                com.example.ui.components.AtelierThemeDialog(
                    currentPreset = currentThemePreset,
                    customAccent = customAccentColor,
                    onSelectPreset = { preset -> viewModel.setThemePreset(preset) },
                    onSelectAccent = { color -> viewModel.setCustomAccentColor(color) },
                    onDismiss = { selectedDrawerFeature = null }
                )
            }
            DrawerFeature.PATTERN_DRAFTING -> {
                val selectedGarment by viewModel.selectedGarment.collectAsStateWithLifecycle()
                val feedGarments by viewModel.filteredFeed.collectAsStateWithLifecycle()

                com.example.ui.components.PatternDraftingSheet(
                    targetGarment = selectedGarment,
                    availableGarments = feedGarments,
                    onDismiss = { selectedDrawerFeature = null }
                )
            }
            else -> {
                val clients by viewModel.clients.collectAsStateWithLifecycle()
                val feedGarments by viewModel.filteredFeed.collectAsStateWithLifecycle()

                DrawerFeatureSheet(
                    feature = feature,
                    clients = clients,
                    archiveGarments = feedGarments,
                    onOpenClientDossiers = {
                        navController.navigate(AtelierTab.Library.route)
                    },
                    onAssignFabricToClient = { client, fab ->
                        viewModel.updateClientDossier(client)
                    },
                    onApplyMetricToClient = { client, def ->
                        val updatedNotes = if (client.measurements.isBlank()) def.name else "${client.measurements} | ${def.name}"
                        viewModel.updateClientDossier(client.copy(measurements = updatedNotes))
                    },
                    onSelectGarment = { garment ->
                        viewModel.selectGarment(garment)
                    },
                    onAddCustomGarment = { title, cat, desc, uri, fab, sil ->
                        viewModel.createCustomGarment(title, cat, desc, uri, fab, sil, onSuccess = {})
                    },
                    onDismiss = { selectedDrawerFeature = null }
                )
            }
        }
    }
}
