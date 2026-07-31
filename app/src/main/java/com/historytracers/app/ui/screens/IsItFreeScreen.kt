// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.isItFreeScreenStringsForLanguage

@Composable
fun IsItFreeScreen(onNavigateToAbout: () -> Unit) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val xs = isItFreeScreenStringsForLanguage(LocalAppLanguage.current)
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = hts.isItFreeContent,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = hts.isItFreeDonateCall,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            uriHandler.openUri("https://www.patreon.com/bePatron?u=104667333")
        }) {
            Text(xs.patreon)
        }

        Spacer(Modifier.height(8.dp))

        Button(onClick = {
            uriHandler.openUri("https://www.paypal.com/donate/?hosted_button_id=F9SD36K5M3BW6")
        }) {
            Text(xs.paypal)
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = hts.isItFreeFollowPrefix,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = onNavigateToAbout) {
            Text(hts.aboutUs)
        }

        Spacer(Modifier.height(32.dp))
    }
}
