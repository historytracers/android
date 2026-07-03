// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.historytracers.app.navigation.AppNavigation
import com.historytracers.app.ui.theme.HistoryTracersTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HistoryTracersTheme {
                AppNavigation()
            }
        }
    }
}
