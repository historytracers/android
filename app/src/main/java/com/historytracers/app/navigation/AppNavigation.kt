// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import com.historytracers.app.ui.screens.AboutScreen
import com.historytracers.app.ui.screens.ContentScreen
import com.historytracers.app.ui.screens.FirstStepsScreen
import com.historytracers.app.ui.screens.IndexScreen
import com.historytracers.app.ui.screens.IsItFreeScreen
import com.historytracers.app.ui.screens.SettingsScreen
import com.historytracers.app.ui.screens.WorkoutScreen
import com.historytracers.app.ui.screens.AbacusScreen
import com.historytracers.app.ui.screens.StreakScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val language by preferences.language.collectAsState(initial = "en-US")
    val breakTime by preferences.breakTime.collectAsState(initial = 30)
    val scope = rememberCoroutineScope()
    val simpleRoutes = setOf("index", "first_steps", "settings", "about", "is_it_free", "streak")
    var startDest by remember { mutableStateOf<String?>(null) }
    var savedScore by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        preferences.lastRoute.first().let { saved ->
            startDest = if (saved in simpleRoutes) saved else "index"
        }
        savedScore = preferences.score.first()
    }

    if (startDest == null || savedScore == null) return

    var counter by remember { mutableStateOf(savedScore!!) }

    LaunchedEffect(counter) {
        preferences.setScore(counter)
    }

    val streakCount by preferences.streakCount.collectAsState(initial = 0)
    val completedDates by preferences.completedDates.collectAsState(initial = emptySet())

    val uiStrings = uiStringsForLanguage(language)

    val startDestination = startDest!!

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        val route = currentRoute
        if (route != null && route in simpleRoutes) {
            preferences.setLastRoute(route)
        }
    }

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
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = null) },
                        label = { Text(uiStrings.streak) },
                        selected = currentRoute == Screen.Streak.route,
                        onClick = {
                            navController.navigate(Screen.Streak.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = null) },
                        label = { Text(uiStrings.isItFree) },
                        selected = currentRoute == Screen.IsItFree.route,
                        onClick = {
                            navController.navigate(Screen.IsItFree.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                        label = { Text(uiStrings.aboutUs) },
                        selected = currentRoute == Screen.About.route,
                        onClick = {
                            navController.navigate(Screen.About.route)
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
                        },
                        actions = {
                            Text(
                                text = counter.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.padding(end = 4.dp))
                            Icon(
                                Icons.Default.Psychology,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.padding(end = 12.dp))
                        }
                    )
                }
            ) { padding ->
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.padding(padding)
                ) {
composable(Screen.Index.route) {
                        IndexScreen(
                            onNavigateToFirstSteps = { navController.navigate(Screen.FirstSteps.route) { launchSingleTop = true } },
                            onNavigateToWorkout = { navController.navigate(Screen.Workout.route) { launchSingleTop = true } },
                            onNavigateToAbacus = { navController.navigate(Screen.Abacus.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.FirstSteps.route) {
                        FirstStepsScreen()
                    }
                    composable(Screen.Workout.route) {
                        WorkoutScreen()
                    }
                    composable(Screen.Abacus.route) {
                        AbacusScreen()
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
                    composable(Screen.IsItFree.route) {
                        IsItFreeScreen(
                            onNavigateToAbout = { navController.navigate(Screen.About.route) }
                        )
                    }
                    composable(Screen.About.route) {
                        AboutScreen()
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
                    composable(Screen.Streak.route) {
                        StreakScreen(
                            streakCount = streakCount,
                            completedDates = completedDates,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
