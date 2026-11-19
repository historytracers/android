// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.historytracers.app.navigation.AppNavigation
import com.historytracers.app.ui.screens.SplashScreen
import com.historytracers.app.ui.theme.HistoryTracersTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // The app always draws on a white background, so the system bars must keep
        // dark icons even when the device is set to dark mode.
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        setContent {
            HistoryTracersTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    LaunchedEffect(Unit) {
                        delay(2000L)
                        showSplash = false
                    }
                    SplashScreen()
                } else {
                    AppNavigation()
                }
            }
        }
    }
}
