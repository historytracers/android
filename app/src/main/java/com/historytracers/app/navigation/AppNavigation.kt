// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.uiStringsForLanguage
import com.historytracers.app.ui.screens.ContentScreen
import com.historytracers.app.ui.screens.IndexScreen
import com.historytracers.app.ui.screens.SettingsScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val language by preferences.language.collectAsState(initial = "en-US")
    val breakTime by preferences.breakTime.collectAsState(initial = 30)
    val scope = rememberCoroutineScope()

    val uiStrings = uiStringsForLanguage(language)

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    CompositionLocalProvider(LocalUiStrings provides uiStrings) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.padding(top = 24.dp))
                    Text(
                        text = "History Tracers",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text(uiStrings.home) },
                        selected = currentRoute == Screen.Index.route,
                        onClick = {
                            navController.navigate(Screen.Index.route) {
                                popUpTo(Screen.Index.route) { inclusive = true }
                            }
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(uiStrings.settings) },
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            navController.navigate(Screen.Settings.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("History Tracers") },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = uiStrings.menu)
                            }
                        }
                    )
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Index.route,
                    modifier = Modifier.padding(padding)
                ) {
                composable(Screen.Index.route) {
                    IndexScreen(language = language)
                }
                    composable(
                        route = Screen.Content.route,
                        arguments = listOf(navArgument("fileName") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val fileName = backStackEntry.arguments?.getString("fileName") ?: return@composable
                        ContentScreen(
                            fileName = fileName,
                            language = language,
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateHome = {
                                navController.navigate(Screen.Index.route) {
                                    popUpTo(Screen.Index.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Screen.Settings.route) {
                        SettingsScreen(
                            currentLanguage = language,
                            currentBreakTime = breakTime,
                            onLanguageChanged = { lang ->
                                scope.launch { preferences.setLanguage(lang) }
                            },
                            onBreakTimeChanged = { minutes ->
                                scope.launch { preferences.setBreakTime(minutes) }
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
