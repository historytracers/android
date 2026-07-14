// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.historytracers.app.ui.screens.ClapScreen
import com.historytracers.app.ui.screens.CongratulationScreen
import com.historytracers.app.ui.screens.FeetAndHandsScreen
import com.historytracers.app.ui.screens.StreakScreen
import com.historytracers.app.ui.screens.SorobanWritingScreen
import com.historytracers.app.ui.screens.SuanpanWritingScreen
import com.historytracers.app.ui.screens.LargeNumbersWritingScreen
import com.historytracers.app.notification.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val language by preferences.language.collectAsState(initial = "en-US")
    val breakTime by preferences.breakTime.collectAsState(initial = 15)
    val skinColor by preferences.skinColor.collectAsState(initial = "#FFF8E0")
    val scope = rememberCoroutineScope()
    val simpleRoutes = setOf("index", "first_steps", "workout", "abacus", "settings", "about", "is_it_free", "streak", "clap", "feet_and_hands", "congratulation", "soroban_writing", "suanpan_writing", "large_numbers_writing")
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

    var breakStartTime by remember { mutableStateOf(System.currentTimeMillis() / 1000L) }
    var showBreakDialog by remember { mutableStateOf(false) }

    LaunchedEffect(breakStartTime, breakTime) {
        if (breakStartTime == 0L) return@LaunchedEffect
        while (true) {
            delay(1000)
            if ((System.currentTimeMillis() / 1000L) - breakStartTime >= breakTime * 60L) {
                showBreakDialog = true
                break
            }
        }
    }

    val streakCount by preferences.streakCount.collectAsState(initial = 0)
    val completedDates by preferences.completedDates.collectAsState(initial = emptySet())
    val streakDays by preferences.streakDays.collectAsState(initial = emptySet())
    val reminderEnabled by preferences.reminderEnabled.collectAsState(initial = true)
    val reminderHour by preferences.reminderHour.collectAsState(initial = 18)
    val reminderMinute by preferences.reminderMinute.collectAsState(initial = 0)

    val uiStrings = uiStringsForLanguage(language)

    val startDestination = startDest!!

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val firstStepsScrollState = rememberScrollState()
    val workoutScrollState = rememberScrollState()
    val abacusScrollState = rememberScrollState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        val route = currentRoute
        if (route != null && route in simpleRoutes) {
            preferences.setLastRoute(route)
        }
    }

    LaunchedEffect(reminderEnabled, reminderHour, reminderMinute, language) {
        NotificationHelper.scheduleAlarm(
            context, reminderEnabled, reminderHour, reminderMinute,
            uiStrings.reminderTitle, uiStrings.reminderMessage
        )
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
                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Close, contentDescription = null) },
                        label = { Text(uiStrings.close) },
                        selected = false,
                        onClick = {
                            (context as? android.app.Activity)?.finish()
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
                        FirstStepsScreen(
                            scrollState = firstStepsScrollState,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) }
                        )
                    }
                    composable(Screen.Workout.route) {
                        WorkoutScreen(
                            scrollState = workoutScrollState,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToClap = { navController.navigate(Screen.Clap.route) },
                            onNavigateToFeetAndHands = { navController.navigate(Screen.FeetAndHands.route) },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) }
                        )
                    }
                    composable(Screen.Abacus.route) {
                        AbacusScreen(
                            scrollState = abacusScrollState,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) },
                            onNavigateToSorobanWriting = { navController.navigate(Screen.SorobanWriting.route) },
                            onNavigateToSuanpanWriting = { navController.navigate(Screen.SuanpanWriting.route) },
                            onNavigateToLargeNumbersWriting = { navController.navigate(Screen.LargeNumbersWriting.route) }
                        )
                    }
                    composable(Screen.SorobanWriting.route) {
                        SorobanWritingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SuanpanWriting.route) {
                        SuanpanWritingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LargeNumbersWriting.route) {
                        LargeNumbersWritingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Abacus.route, false)) {
                                    navController.navigate(Screen.Abacus.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
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
                            currentSkinColor = skinColor,
                            onLanguageChanged = { lang ->
                                scope.launch { preferences.setLanguage(lang) }
                            },
                            onBreakTimeChanged = { minutes ->
                                scope.launch { preferences.setBreakTime(minutes) }
                            },
                            onSkinColorChanged = { color ->
                                scope.launch { preferences.setSkinColor(color) }
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Streak.route) {
                        StreakScreen(
                            streakCount = streakCount,
                            completedDates = completedDates,
                            streakDays = streakDays,
                            language = language,
                            reminderEnabled = reminderEnabled,
                            reminderHour = reminderHour,
                            reminderMinute = reminderMinute,
                            onStreakDaysChanged = { scope.launch { preferences.setStreakDays(it) } },
                            onReminderEnabledChanged = { scope.launch { preferences.setReminderEnabled(it) } },
                            onReminderTimeChanged = { hour, minute ->
                                scope.launch {
                                    preferences.setReminderHour(hour)
                                    preferences.setReminderMinute(minute)
                                }
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Clap.route) {
                        ClapScreen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.FeetAndHands.route) {
                        FeetAndHandsScreen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.Congratulation.route) {
                        CongratulationScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }

    if (showBreakDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text(uiStrings.breakReminderTitle) },
            text = { Text(uiStrings.breakMessage) },
            confirmButton = {
                TextButton(onClick = {
                    breakStartTime = System.currentTimeMillis() / 1000L
                    showBreakDialog = false
                }) {
                    Text(uiStrings.imBack)
                }
            }
        )
    }
}
