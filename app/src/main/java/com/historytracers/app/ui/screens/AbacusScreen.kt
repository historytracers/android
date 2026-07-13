// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.R
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow

@Composable
fun AbacusScreen(
    onNavigateToCongratulation: () -> Unit = {},
    onNavigateToSorobanWriting: () -> Unit = {}
) {
    val s = LocalUiStrings.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                onClick = { },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_feather),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.history,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = onNavigateToSorobanWriting,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "\u7B97\u76E4",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.writingToSoroban,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = { },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "\u7B97\u76E4",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.writingToSuanpan,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(32.dp))

            FilledIconButton(
                onClick = { },
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Text(
                    text = "99",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = OnButtonYellow
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = s.largeNumbers,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(48.dp))

            FilledIconButton(
                onClick = onNavigateToCongratulation,
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = ButtonYellow
                )
            ) {
                Icon(
                    painterResource(R.drawable.ic_flag),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp),
                    tint = Color.Unspecified
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Next level",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}