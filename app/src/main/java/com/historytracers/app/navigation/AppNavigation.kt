// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.historytracers.app.MainActivity
import com.historytracers.app.ui.screens.ContentScreen
import com.historytracers.app.ui.screens.IndexScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = currentRoute == Screen.Index.route,
                    onClick = {
                        navController.navigate(Screen.Index.route) {
                            popUpTo(Screen.Index.route) { inclusive = true }
                        }
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
                        IconButton(onClick = { /* TODO: open drawer */ }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                    IndexScreen(
                        onContentClick = { fileName ->
                            navController.navigate(Screen.Content.createRoute(fileName))
                        }
                    )
                }
                composable(
                    route = Screen.Content.route,
                    arguments = listOf(navArgument("fileName") { type = NavType.StringType })
                ) { backStackEntry ->
                    val fileName = backStackEntry.arguments?.getString("fileName") ?: return@composable
                    ContentScreen(fileName = fileName)
                }
            }
        }
    }
}
