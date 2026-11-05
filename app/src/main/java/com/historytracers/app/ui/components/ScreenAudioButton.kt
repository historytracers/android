// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings

private const val AUDIO_BASE_URL = "https://www.historytracers.org/audios/"

/**
 * Returns true when the device currently has network access.
 */
private fun hasInternetConnection(context: Context): Boolean {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        ?: return false
    val network = manager.activeNetwork ?: return false
    val capabilities = manager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

/**
 * Plays the audio narration associated with a screen.
 *
 * The audio files are hosted online and follow the pattern
 * `https://www.historytracers.org/audios/{lessonId}_{contentId}_{language}.ogg`,
 * so playback requires an internet connection. Tapping the button while the
 * audio is playing stops it.
 */
@Composable
fun ScreenAudioButton(
    lessonId: String,
    contentId: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language = LocalAppLanguage.current
    val s = LocalUiStrings.current
    val audioUrl = remember(lessonId, contentId, language) {
        "$AUDIO_BASE_URL${lessonId}_${contentId}_$language.ogg"
    }

    var player by remember(audioUrl) { mutableStateOf<MediaPlayer?>(null) }

    fun releasePlayer() {
        val current = player ?: return
        player = null
        runCatching { if (current.isPlaying) current.stop() }
        runCatching { current.reset() }
        runCatching { current.release() }
    }

    DisposableEffect(audioUrl) {
        onDispose { releasePlayer() }
    }

    IconButton(
        onClick = {
            val current = player
            if (current != null) {
                releasePlayer()
                return@IconButton
            }
            if (!hasInternetConnection(context)) {
                Toast.makeText(context, s.common.audioOfflineMessage, Toast.LENGTH_SHORT).show()
                return@IconButton
            }
            val mediaPlayer = MediaPlayer()
            player = mediaPlayer
            try {
                mediaPlayer.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                mediaPlayer.setDataSource(audioUrl)
                mediaPlayer.setOnPreparedListener { prepared ->
                    if (player === prepared) {
                        runCatching { prepared.start() }
                    }
                }
                mediaPlayer.setOnCompletionListener {
                    releasePlayer()
                }
                mediaPlayer.setOnErrorListener { _, _, _ ->
                    releasePlayer()
                    Toast.makeText(context, s.common.audioErrorMessage, Toast.LENGTH_SHORT).show()
                    true
                }
                mediaPlayer.prepareAsync()
            } catch (exception: Exception) {
                releasePlayer()
                Toast.makeText(context, s.common.audioErrorMessage, Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier
    ) {
        val isActive = player != null
        Icon(
            imageVector = if (isActive) Icons.Filled.Stop else Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = if (isActive) s.common.stop else s.common.audio
        )
    }
}
