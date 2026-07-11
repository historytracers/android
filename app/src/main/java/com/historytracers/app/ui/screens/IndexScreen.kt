// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow

@Composable
fun IndexScreen(
    onNavigateToFirstSteps: () -> Unit = {},
    onNavigateToWorkout: () -> Unit = {},
    onNavigateToAbacus: () -> Unit = {}
) {
    val s = LocalUiStrings.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FilledTonalButton(
                onClick = onNavigateToFirstSteps,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.DirectionsWalk,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = s.firstSteps,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onNavigateToWorkout,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = s.workout,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            FilledTonalButton(
                onClick = onNavigateToAbacus,
                modifier = Modifier.padding(horizontal = 32.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = ButtonYellow,
                    contentColor = OnButtonYellow
                )
            ) {
                Icon(
                    Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = s.abacus,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
