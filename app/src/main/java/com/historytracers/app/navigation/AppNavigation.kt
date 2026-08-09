// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.ScrollState
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
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
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.uiStringsForLanguage
import com.historytracers.app.ui.screens.AboutScreen
import com.historytracers.app.ui.screens.BuildingGameScreen
import com.historytracers.app.ui.screens.ContentScreen
import com.historytracers.app.ui.screens.FirstStepsScreen
import com.historytracers.app.ui.screens.IndexScreen
import com.historytracers.app.ui.screens.IAmNotLikeYouScreen
import com.historytracers.app.ui.screens.IsItFreeScreen
import com.historytracers.app.ui.screens.LearningInLayersConclusionScreen
import com.historytracers.app.ui.screens.LearningInLayersIntroScreen
import com.historytracers.app.ui.screens.LearningInLayersPlayingScreen
import com.historytracers.app.ui.screens.LearningInLayersQuestionScreen
import com.historytracers.app.ui.screens.LearningInLayersStagesScreen
import com.historytracers.app.ui.screens.LearningInLayersToyScreen
import com.historytracers.app.ui.screens.SettingsScreen
import com.historytracers.app.ui.screens.SequenceGameScreen
import com.historytracers.app.ui.screens.SequenceGameFamiliesScreen
import com.historytracers.app.ui.screens.SequenceGameOrdersScreen
import com.historytracers.app.ui.screens.WorkoutScreen
import com.historytracers.app.ui.screens.AbacusScreen
import com.historytracers.app.ui.screens.ClapScreen
import com.historytracers.app.ui.screens.CongratulationScreen
import com.historytracers.app.ui.screens.ExercisingAdditionScreen
import com.historytracers.app.ui.screens.FeetAndHandsScreen
import com.historytracers.app.ui.screens.HowDoILearnChartScreen
import com.historytracers.app.ui.screens.HowDoILearnChartUnderstandingScreen
import com.historytracers.app.ui.screens.HowDoILearnComparisonsScreen
import com.historytracers.app.ui.screens.HowDoILearnDecisionScreen
import com.historytracers.app.ui.screens.HowDoILearnHorizonScreen
import com.historytracers.app.ui.screens.HowDoILearnIntroScreen
import com.historytracers.app.ui.screens.HowDoILearnQuestionScreen
import com.historytracers.app.ui.screens.MyHandsConclusionScreen
import com.historytracers.app.ui.screens.MyBodyConclusionScreen
import com.historytracers.app.ui.screens.MyBodyEverythingQuestionScreen
import com.historytracers.app.ui.screens.MyBodyFeetQuestionScreen
import com.historytracers.app.ui.screens.MyBodyFeetScreen
import com.historytracers.app.ui.screens.MyBodyImprovementScreen
import com.historytracers.app.ui.screens.MyBodyIntroScreen
import com.historytracers.app.ui.screens.FirstHandsIntroScreen
import com.historytracers.app.ui.screens.FirstHandsQuestionScreen
import com.historytracers.app.ui.screens.FirstHandsKnowledgeScreen
import com.historytracers.app.ui.screens.FirstHandsHabilisScreen
import com.historytracers.app.ui.screens.FirstHandsReflectionScreen
import com.historytracers.app.ui.screens.FirstHandsCountingScreen
import com.historytracers.app.ui.screens.FirstHandsConclusionScreen
import com.historytracers.app.ui.screens.FirstVoiceIntroScreen
import com.historytracers.app.ui.screens.FirstVoiceVoiceScreen
import com.historytracers.app.ui.screens.FirstVoiceReflectionScreen
import com.historytracers.app.ui.screens.FirstVoiceDifferenceScreen
import com.historytracers.app.ui.screens.FirstVoiceConclusionScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingIntroScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingCirclesScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingRectanglesScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingMesoamericansScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingQuestionScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingExamplesScreen
import com.historytracers.app.ui.screens.DrawingAndCoutingConclusionScreen
import com.historytracers.app.ui.screens.NumbersIntroScreen
import com.historytracers.app.ui.screens.NumbersOriginScreen
import com.historytracers.app.ui.screens.NumbersQuestionScreen
import com.historytracers.app.ui.screens.NumbersEqualScreen
import com.historytracers.app.ui.screens.NumbersVisualizingScreen
import com.historytracers.app.ui.screens.NumbersConclusionScreen
import com.historytracers.app.ui.screens.FamilyPart1IntroScreen
import com.historytracers.app.ui.screens.FamilyPart1OrdersScreen
import com.historytracers.app.ui.screens.FamilyPart1OrderScreen
import com.historytracers.app.ui.screens.FamilyPart1AltarScreen
import com.historytracers.app.ui.screens.FamilyPart1ContinuityScreen
import com.historytracers.app.ui.screens.FamilyPart1StagesScreen
import com.historytracers.app.ui.screens.FamilyPart1RuleScreen
import com.historytracers.app.ui.screens.FamilyPart1ConclusionScreen
import com.historytracers.app.ui.screens.MyHandsCountingScreen
import com.historytracers.app.ui.screens.MyHandsFingersScreen
import com.historytracers.app.ui.screens.MyHandsIntroScreen
import com.historytracers.app.ui.screens.MyHandsQuestionScreen
import com.historytracers.app.ui.screens.SocratesConclusionScreen
import com.historytracers.app.ui.screens.SocratesMotivationScreen
import com.historytracers.app.ui.screens.SocratesPhilosophyScreen
import com.historytracers.app.ui.screens.SocratesQuestionScreen
import com.historytracers.app.ui.screens.StreakScreen
import com.historytracers.app.ui.screens.SorobanWritingScreen
import com.historytracers.app.ui.screens.SuanpanWritingScreen
import com.historytracers.app.ui.screens.SchyotyWritingScreen
import com.historytracers.app.ui.screens.LargeNumbersWritingScreen
import com.historytracers.app.ui.screens.PracticingAdditionScreen
import com.historytracers.app.ui.screens.MultiplicationTableScreen
import com.historytracers.app.ui.screens.MultiplyingWithAbacusScreen
import com.historytracers.app.ui.screens.MultiplyingWithAbacusLevel2Screen
import com.historytracers.app.ui.screens.MultiplyingWithoutLimitsScreen
import com.historytracers.app.ui.screens.SubtractingWithAbacusScreen
import com.historytracers.app.ui.screens.AddingWithAbacusScreen
import com.historytracers.app.ui.screens.ComplementToTenScreen
import com.historytracers.app.ui.screens.AddingLargeNumbersScreen
import com.historytracers.app.ui.screens.CarryingScreen
import com.historytracers.app.ui.screens.RelationshipScreen
import com.historytracers.app.ui.screens.ExercisingMultiplicationL2Screen
import com.historytracers.app.ui.screens.YupanaScreen
import com.historytracers.app.ui.screens.PracticingAdditionYupanaScreen
import com.historytracers.app.ui.screens.PracticingMultiplicationYupanaScreen
import com.historytracers.app.ui.screens.HandsOnYupanaScreen
import com.historytracers.app.ui.screens.DrawingToCountScreen
import com.historytracers.app.ui.screens.IskayMovementScreen
import com.historytracers.app.ui.screens.KimsaMovementScreen
import com.historytracers.app.ui.screens.PisqaMovementScreen
import com.historytracers.app.ui.screens.PichanaMovementScreen
import com.historytracers.app.ui.screens.KinkinMovementScreen
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
    val skinColor by preferences.skinColor.collectAsState(initial = "#A5672C")
    val scope = rememberCoroutineScope()
    val simpleRoutes = setOf("index", "i_am_not_like_you", "first_steps", "sequence_game", "sequence_game_orders", "sequence_game_families", "building_game", "socrates", "socrates_question", "socrates_motivation", "socrates_conclusion", "learning_in_layers_intro", "learning_in_layers_toy", "learning_in_layers_stages", "learning_in_layers_question", "learning_in_layers_playing", "learning_in_layers_conclusion", "how_do_i_learn_intro", "how_do_i_learn_comparisons", "how_do_i_learn_question", "how_do_i_learn_horizon", "how_do_i_learn_chart", "how_do_i_learn_chart_understanding", "how_do_i_learn_decision", "my_hands", "my_hands_question", "my_hands_counting", "my_hands_fingers", "my_hands_conclusion", "my_body", "my_body_feet_question", "my_body_feet", "my_body_everything_question", "my_body_improvement", "my_body_conclusion", "first_hands", "first_hands_question", "first_hands_knowledge", "first_hands_habilis", "first_hands_reflection", "first_hands_counting", "first_hands_conclusion", "first_voice", "first_voice_voice", "first_voice_reflection", "first_voice_difference", "first_voice_conclusion", "drawing_and_couting_intro", "drawing_and_couting_circles", "drawing_and_couting_rectangles", "drawing_and_couting_mesoamericans", "drawing_and_couting_question", "drawing_and_couting_examples", "drawing_and_couting_conclusion", "numbers_intro", "numbers_origin", "numbers_question", "numbers_equal", "numbers_visualizing", "numbers_conclusion", "family_part1_intro", "family_part1_orders", "family_part1_order", "family_part1_altar", "family_part1_continuity", "family_part1_stages", "family_part1_rule", "family_part1_conclusion", "workout", "abacus", "yupana", "settings", "about", "is_it_free", "streak", "clap", "feet_and_hands", "congratulation", "exercising_addition", "soroban_writing", "suanpan_writing", "large_numbers_writing", "practicing_addition", "multiplication_table", "multiplying_with_abacus", "multiplying_with_abacus_level2", "multiplying_without_limits", "carrying", "subtracting_with_abacus", "adding_with_abacus", "complement_to_ten", "adding_large_numbers", "relationship", "exercising_multiplication_l2", "practicing_addition_yupana", "practicing_multiplication_yupana", "hands_on_yupana", "drawing_to_count", "iskay_movement", "kimsa_movement", "pisqa_movement", "pichana_movement", "kinkin_movement")
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
    val hts = hubTitleStringsForLanguage(language)

    val startDestination = startDest!!

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    val savedFirstStepsScroll by preferences.firstStepsScroll.collectAsState(initial = 0)
    val savedWorkoutScroll by preferences.workoutScroll.collectAsState(initial = 0)
    val savedAbacusScroll by preferences.abacusScroll.collectAsState(initial = 0)
    val savedYupanaScroll by preferences.yupanaScroll.collectAsState(initial = 0)

    val firstStepsScrollState = remember { ScrollState(0) }
    val workoutScrollState = remember { ScrollState(0) }
    val abacusScrollState = remember { ScrollState(0) }
    val yupanaScrollState = remember { ScrollState(0) }

    LaunchedEffect(savedFirstStepsScroll) {
        if (savedFirstStepsScroll > 0) firstStepsScrollState.scrollTo(savedFirstStepsScroll)
    }
    LaunchedEffect(savedWorkoutScroll) {
        if (savedWorkoutScroll > 0) workoutScrollState.scrollTo(savedWorkoutScroll)
    }
    LaunchedEffect(savedAbacusScroll) {
        if (savedAbacusScroll > 0) abacusScrollState.scrollTo(savedAbacusScroll)
    }
    LaunchedEffect(savedYupanaScroll) {
        if (savedYupanaScroll > 0) yupanaScrollState.scrollTo(savedYupanaScroll)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { firstStepsScrollState.value }
            .collect { preferences.setFirstStepsScroll(it) }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { workoutScrollState.value }
            .collect { preferences.setWorkoutScroll(it) }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { abacusScrollState.value }
            .collect { preferences.setAbacusScroll(it) }
    }
    LaunchedEffect(Unit) {
        snapshotFlow { yupanaScrollState.value }
            .collect { preferences.setYupanaScroll(it) }
    }

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
            uiStrings.common.reminderTitle, uiStrings.common.reminderMessage
        )
    }

    CompositionLocalProvider(LocalUiStrings provides uiStrings, LocalAppLanguage provides language) {
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
                        label = { Text(uiStrings.common.home) },
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
                        label = { Text(uiStrings.common.settings) },
                        selected = currentRoute == Screen.Settings.route,
                        onClick = {
                            navController.navigate(Screen.Settings.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.LocalFireDepartment, contentDescription = null) },
                        label = { Text(uiStrings.common.streak) },
                        selected = currentRoute == Screen.Streak.route,
                        onClick = {
                            navController.navigate(Screen.Streak.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = null) },
                        label = { Text(hts.isItFree) },
                        selected = currentRoute == Screen.IsItFree.route,
                        onClick = {
                            navController.navigate(Screen.IsItFree.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Info, contentDescription = null) },
                        label = { Text(hts.aboutUs) },
                        selected = currentRoute == Screen.About.route,
                        onClick = {
                            navController.navigate(Screen.About.route)
                            scope.launch { drawerState.close() }
                        }
                    )
                    Divider()
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Close, contentDescription = null) },
                        label = { Text(uiStrings.common.close) },
                        selected = false,
                        onClick = {
                            (context as? android.app.Activity)?.finishAndRemoveTask()
                        }
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {},
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = uiStrings.common.menu)
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
                            Spacer(Modifier.padding(end = 8.dp))
                            Text(
                                text = streakCount.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.padding(end = 4.dp))
                            Icon(
                                Icons.Default.LocalFireDepartment,
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
                            onNavigateToIAmNotLikeYou = { navController.navigate(Screen.IAmNotLikeYou.route) { launchSingleTop = true } },
                            onNavigateToWorkout = { navController.navigate(Screen.Workout.route) { launchSingleTop = true } },
                            onNavigateToAbacus = { navController.navigate(Screen.Abacus.route) { launchSingleTop = true } },
                            onNavigateToYupana = { navController.navigate(Screen.Yupana.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.IAmNotLikeYou.route) {
                        IAmNotLikeYouScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.FirstSteps.route) {
                        FirstStepsScreen(
                            scrollState = firstStepsScrollState,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) },
                            onNavigateToSocrates = { navController.navigate(Screen.Socrates.route) { launchSingleTop = true } },
                            onNavigateToLearningInLayers = { navController.navigate(Screen.LearningInLayersIntro.route) { launchSingleTop = true } },
                            onNavigateToHowDoILearn = { navController.navigate(Screen.HowDoILearnIntro.route) { launchSingleTop = true } },
                            onNavigateToMyHands = { navController.navigate(Screen.MyHands.route) { launchSingleTop = true } },
                            onNavigateToMyBody = { navController.navigate(Screen.MyBody.route) { launchSingleTop = true } },
                            onNavigateToFirstHands = { navController.navigate(Screen.FirstHands.route) { launchSingleTop = true } },
                            onNavigateToFirstVoice = { navController.navigate(Screen.FirstVoice.route) { launchSingleTop = true } },
                            onNavigateToDrawingAndCouting = { navController.navigate(Screen.DrawingAndCoutingIntro.route) { launchSingleTop = true } },
                            onNavigateToNumbers = { navController.navigate(Screen.NumbersIntro.route) { launchSingleTop = true } },
                            onNavigateToFamilyPart1 = { navController.navigate(Screen.FamilyPart1Intro.route) { launchSingleTop = true } },
                            onNavigateToSequenceGame = { navController.navigate(Screen.SequenceGame.route) { launchSingleTop = true } },
                            onNavigateToSequenceGameOrders = { navController.navigate(Screen.SequenceGameOrders.route) { launchSingleTop = true } },
                            onNavigateToSequenceGameFamilies = { navController.navigate(Screen.SequenceGameFamilies.route) { launchSingleTop = true } },
                            onNavigateToBuildingGame = { navController.navigate(Screen.BuildingGame.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.SequenceGameOrders.route) {
                        SequenceGameOrdersScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SequenceGame.route) {
                        SequenceGameScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SequenceGameFamilies.route) {
                        SequenceGameFamiliesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.BuildingGame.route) {
                        BuildingGameScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.Socrates.route) {
                        SocratesPhilosophyScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.SocratesQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SocratesQuestion.route) {
                        SocratesQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.Socrates.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.SocratesMotivation.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SocratesMotivation.route) {
                        SocratesMotivationScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.SocratesQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.SocratesConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.SocratesConclusion.route) {
                        SocratesConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.SocratesMotivation.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersIntro.route) {
                        LearningInLayersIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersToy.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersToy.route) {
                        LearningInLayersToyScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersStages.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersStages.route) {
                        LearningInLayersStagesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersToy.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersQuestion.route) {
                        LearningInLayersQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersStages.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersPlaying.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersPlaying.route) {
                        LearningInLayersPlayingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.LearningInLayersConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.LearningInLayersConclusion.route) {
                        LearningInLayersConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.LearningInLayersPlaying.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnIntro.route) {
                        HowDoILearnIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnComparisons.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnComparisons.route) {
                        HowDoILearnComparisonsScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnQuestion.route) {
                        HowDoILearnQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnComparisons.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnHorizon.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnHorizon.route) {
                        HowDoILearnHorizonScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnChart.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnChart.route) {
                        HowDoILearnChartScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnHorizon.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnChartUnderstanding.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnChartUnderstanding.route) {
                        HowDoILearnChartUnderstandingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnChart.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.HowDoILearnDecision.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.HowDoILearnDecision.route) {
                        HowDoILearnDecisionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.HowDoILearnChartUnderstanding.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHands.route) {
                        MyHandsIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MyHandsQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHandsQuestion.route) {
                        MyHandsQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyHands.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyHandsCounting.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHandsCounting.route) {
                        MyHandsCountingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyHandsQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyHandsFingers.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHandsFingers.route) {
                        MyHandsFingersScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyHandsCounting.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyHandsConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyHandsConclusion.route) {
                        MyHandsConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyHandsFingers.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBody.route) {
                        MyBodyIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.MyBodyFeetQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyFeetQuestion.route) {
                        MyBodyFeetQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBody.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyBodyFeet.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyFeet.route) {
                        MyBodyFeetScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBodyFeetQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyBodyEverythingQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyEverythingQuestion.route) {
                        MyBodyEverythingQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBodyFeet.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyBodyImprovement.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyImprovement.route) {
                        MyBodyImprovementScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBodyEverythingQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.MyBodyConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.MyBodyConclusion.route) {
                        MyBodyConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.MyBodyImprovement.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHands.route) {
                        FirstHandsIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsQuestion.route) {
                        FirstHandsQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHands.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsKnowledge.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsKnowledge.route) {
                        FirstHandsKnowledgeScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsHabilis.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsHabilis.route) {
                        FirstHandsHabilisScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsKnowledge.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsReflection.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsReflection.route) {
                        FirstHandsReflectionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsHabilis.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsCounting.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsCounting.route) {
                        FirstHandsCountingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsReflection.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstHandsConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstHandsConclusion.route) {
                        FirstHandsConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstHandsCounting.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoice.route) {
                        FirstVoiceIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.FirstVoiceVoice.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoiceVoice.route) {
                        FirstVoiceVoiceScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstVoice.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstVoiceReflection.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoiceReflection.route) {
                        FirstVoiceReflectionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstVoiceVoice.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstVoiceDifference.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoiceDifference.route) {
                        FirstVoiceDifferenceScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstVoiceReflection.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FirstVoiceConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FirstVoiceConclusion.route) {
                        FirstVoiceConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FirstVoiceDifference.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingIntro.route) {
                        DrawingAndCoutingIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingCircles.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingCircles.route) {
                        DrawingAndCoutingCirclesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingRectangles.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingRectangles.route) {
                        DrawingAndCoutingRectanglesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingCircles.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingMesoamericans.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingMesoamericans.route) {
                        DrawingAndCoutingMesoamericansScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingRectangles.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingQuestion.route) {
                        DrawingAndCoutingQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingMesoamericans.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingExamples.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingExamples.route) {
                        DrawingAndCoutingExamplesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.DrawingAndCoutingConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.DrawingAndCoutingConclusion.route) {
                        DrawingAndCoutingConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.DrawingAndCoutingExamples.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersIntro.route) {
                        NumbersIntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.NumbersOrigin.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersOrigin.route) {
                        NumbersOriginScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersIntro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NumbersQuestion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersQuestion.route) {
                        NumbersQuestionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersOrigin.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NumbersEqual.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersEqual.route) {
                        NumbersEqualScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersQuestion.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NumbersVisualizing.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersVisualizing.route) {
                        NumbersVisualizingScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersEqual.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.NumbersConclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.NumbersConclusion.route) {
                        NumbersConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.NumbersVisualizing.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Intro.route) {
                        FamilyPart1IntroScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Orders.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Orders.route) {
                        FamilyPart1OrdersScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Intro.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Order.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Order.route) {
                        FamilyPart1OrderScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Orders.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Altar.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Altar.route) {
                        FamilyPart1AltarScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Order.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Continuity.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Continuity.route) {
                        FamilyPart1ContinuityScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Altar.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Stages.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Stages.route) {
                        FamilyPart1StagesScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Continuity.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Rule.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Rule.route) {
                        FamilyPart1RuleScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Stages.route) { launchSingleTop = true } },
                            onNavigateNext = { navController.navigate(Screen.FamilyPart1Conclusion.route) { launchSingleTop = true } },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.FamilyPart1Conclusion.route) {
                        FamilyPart1ConclusionScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigatePrev = { navController.navigate(Screen.FamilyPart1Rule.route) { launchSingleTop = true } },
                            onNavigateToFirstSteps = {
                                if (!navController.popBackStack(Screen.FirstSteps.route, false)) {
                                    navController.navigate(Screen.FirstSteps.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.Workout.route) {
                        WorkoutScreen(
                            scrollState = workoutScrollState,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
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
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) },
                            onNavigateToExercisingAddition = { navController.navigate(Screen.ExercisingAddition.route) },
                            onNavigateToRelationship = { navController.navigate(Screen.Relationship.route) },
                            onNavigateToExercisingMultiplicationL2 = { navController.navigate(Screen.ExercisingMultiplicationL2.route) }
                        )
                    }
                    composable(Screen.Abacus.route) {
                        AbacusScreen(
                            scrollState = abacusScrollState,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
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
                            onNavigateToSchyotyWriting = { navController.navigate(Screen.SchyotyWriting.route) },
                            onNavigateToLargeNumbersWriting = { navController.navigate(Screen.LargeNumbersWriting.route) },
                            onNavigateToPracticingAddition = { navController.navigate(Screen.PracticingAddition.route) },
                            onNavigateToMultiplicationTable = { navController.navigate(Screen.MultiplicationTable.route) },
                            onNavigateToMultiplyingWithAbacus = { navController.navigate(Screen.MultiplyingWithAbacus.route) },
                            onNavigateToMultiplyingWithAbacusLevel2 = { navController.navigate(Screen.MultiplyingWithAbacusLevel2.route) },
                            onNavigateToMultiplyingWithoutLimits = { navController.navigate(Screen.MultiplyingWithoutLimits.route) },
                            onNavigateToCarrying = { navController.navigate(Screen.Carrying.route) },
                            onNavigateToSubtractingWithAbacus = { navController.navigate(Screen.SubtractingWithAbacus.route) },
                            onNavigateToAddingWithAbacus = { navController.navigate(Screen.AddingWithAbacus.route) },
                            onNavigateToComplementToTen = { navController.navigate(Screen.ComplementToTen.route) },
                            onNavigateToAddingLargeNumbers = { navController.navigate(Screen.AddingLargeNumbers.route) }
                        )
                    }
                    composable(Screen.Yupana.route) {
                        YupanaScreen(
                            scrollState = yupanaScrollState,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Index.route, false)) {
                                    navController.navigate(Screen.Index.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToCongratulation = { navController.navigate(Screen.Congratulation.route) },
                            onNavigateToPracticingAdditionYupana = { navController.navigate(Screen.PracticingAdditionYupana.route) },
                            onNavigateToPracticingMultiplicationYupana = { navController.navigate(Screen.PracticingMultiplicationYupana.route) },
                            onNavigateToHandsOnYupana = { navController.navigate(Screen.HandsOnYupana.route) { launchSingleTop = true } },
                            onNavigateToMovingInYupana = { navController.navigate(Screen.IskayMovement.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.HandsOnYupana.route) {
                        HandsOnYupanaScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToDrawingToCount = { navController.navigate(Screen.DrawingToCount.route) { launchSingleTop = true } }
                        )
                    }
                    composable(Screen.DrawingToCount.route) {
                        DrawingToCountScreen(
                            skinColor = skinColor,
                            onNavigateToHandsOnYupana = { navController.navigate(Screen.HandsOnYupana.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onNavigateToYupana = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.IskayMovement.route) {
                        IskayMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToKimsa = { navController.navigate(Screen.KimsaMovement.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.KimsaMovement.route) {
                        KimsaMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToIskay = { navController.navigate(Screen.IskayMovement.route) { launchSingleTop = true } },
                            onNavigateToPisqa = { navController.navigate(Screen.PisqaMovement.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.PisqaMovement.route) {
                        PisqaMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToKimsa = { navController.navigate(Screen.KimsaMovement.route) { launchSingleTop = true } },
                            onNavigateToPichana = { navController.navigate(Screen.PichanaMovement.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.PichanaMovement.route) {
                        PichanaMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToPisqa = { navController.navigate(Screen.PisqaMovement.route) { launchSingleTop = true } },
                            onNavigateToKinkin = { navController.navigate(Screen.KinkinMovement.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.KinkinMovement.route) {
                        KinkinMovementScreen(
                            skinColor = skinColor,
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore },
                            onNavigateToPichana = { navController.navigate(Screen.PichanaMovement.route) { launchSingleTop = true } },
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                        )
                    }
                    composable(Screen.PracticingAdditionYupana.route) {
                        PracticingAdditionYupanaScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.PracticingMultiplicationYupana.route) {
                        PracticingMultiplicationYupanaScreen(
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Yupana.route, false)) {
                                    navController.navigate(Screen.Yupana.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
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
                    composable(Screen.SchyotyWriting.route) {
                        SchyotyWritingScreen(
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
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
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
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.ExercisingAddition.route) {
                        ExercisingAdditionScreen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.Relationship.route) {
                        RelationshipScreen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.ExercisingMultiplicationL2.route) {
                        ExercisingMultiplicationL2Screen(
                            skinColor = skinColor,
                            onNavigateBack = {
                                if (!navController.popBackStack(Screen.Workout.route, false)) {
                                    navController.navigate(Screen.Workout.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            currentScore = counter,
                            onScoreChanged = { newScore -> counter = newScore }
                        )
                    }
                    composable(Screen.PracticingAddition.route) {
                        PracticingAdditionScreen(
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
                    composable(Screen.MultiplicationTable.route) {
                        MultiplicationTableScreen(
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
                    composable(Screen.MultiplyingWithAbacus.route) {
                        MultiplyingWithAbacusScreen(
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
                    composable(Screen.MultiplyingWithAbacusLevel2.route) {
                        MultiplyingWithAbacusLevel2Screen(
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
                    composable(Screen.MultiplyingWithoutLimits.route) {
                        MultiplyingWithoutLimitsScreen(
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
                    composable(Screen.Carrying.route) {
                        CarryingScreen(
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
                    composable(Screen.SubtractingWithAbacus.route) {
                        SubtractingWithAbacusScreen(
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
                    composable(Screen.AddingWithAbacus.route) {
                        AddingWithAbacusScreen(
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
                    composable(Screen.ComplementToTen.route) {
                        ComplementToTenScreen(
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
                    composable(Screen.AddingLargeNumbers.route) {
                        AddingLargeNumbersScreen(
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
            title = { Text(uiStrings.common.breakReminderTitle) },
            text = { Text(uiStrings.common.breakMessage) },
            confirmButton = {
                TextButton(onClick = {
                    breakStartTime = System.currentTimeMillis() / 1000L
                    showBreakDialog = false
                }) {
                    Text(uiStrings.common.imBack)
                }
            }
        )
    }
}
