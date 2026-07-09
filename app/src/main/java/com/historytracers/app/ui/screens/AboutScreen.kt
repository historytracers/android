// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.R

private data class SocialLink(
    val url: String,
    val color: Color,
    val drawableRes: Int
)

private val socialLinks = listOf(
    SocialLink("https://twitter.com/historytracers", Color(0xFF000000), R.drawable.ic_twitter),
    SocialLink("https://www.facebook.com/groups/1141076993937832", Color(0xFF1877F2), R.drawable.ic_facebook),
    SocialLink("https://www.instagram.com/historytracers/", Color(0xFFE1306C), R.drawable.ic_instagram),
    SocialLink("https://www.threads.net/@historytracers", Color(0xFF000000), R.drawable.ic_threads),
    SocialLink("https://bsky.app/profile/historytracers.bsky.social", Color(0xFF0085FF), R.drawable.ic_bluesky),
    SocialLink("https://www.youtube.com/@HistoryTracers", Color(0xFFFF0000), R.drawable.ic_youtube),
    SocialLink("https://github.com/historytracers/historytracers", Color(0xFF333333), R.drawable.ic_github),
    SocialLink("https://creators.spotify.com/pod/profile/thiago-marques03/", Color(0xFF1DB954), R.drawable.ic_spotify),
)

@Composable
fun AboutScreen() {
    val s = LocalUiStrings.current
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))

        Icon(
            Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = s.aboutDescription,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            socialLinks.forEach { link ->
                SocialIconButton(
                    link = link,
                    onClick = { uriHandler.openUri(link.url) }
                )
            }
        }

        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun SocialIconButton(
    link: SocialLink,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .background(link.color)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val painter: Painter = painterResource(id = link.drawableRes)
        Icon(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = Color.White
        )
    }
}
