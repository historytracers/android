// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
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
import androidx.compose.runtime.SideEffect
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
 * Owns the [MediaPlayer] and the audio focus request used by [ScreenAudioButton].
 */
private class ScreenAudioController(
    private val audioManager: AudioManager?,
    private val audioAttributes: AudioAttributes
) {
    private var player: MediaPlayer? = null

    var isActive by mutableStateOf(false)
        private set

    var onError: (() -> Unit)? = null

    private val focusRequest: AudioFocusRequest? = audioManager?.let { manager ->
        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
            .setAudioAttributes(audioAttributes)
            .setOnAudioFocusChangeListener { change -> handleFocusChange(change) }
            .build()
    }

    fun toggle(url: String) {
        if (isActive) {
            release()
            return
        }
        if (!requestAudioFocus()) {
            onError?.invoke()
            return
        }
        try {
            val mediaPlayer = MediaPlayer()
            player = mediaPlayer
            isActive = true
            mediaPlayer.setAudioAttributes(audioAttributes)
            mediaPlayer.setDataSource(url)
            mediaPlayer.setOnPreparedListener { prepared ->
                if (player === prepared) {
                    runCatching { prepared.start() }
                }
            }
            mediaPlayer.setOnCompletionListener { release() }
            mediaPlayer.setOnErrorListener { _, _, _ ->
                release()
                onError?.invoke()
                true
            }
            mediaPlayer.prepareAsync()
        } catch (exception: Exception) {
            release()
            onError?.invoke()
        }
    }

    fun release() {
        val current = player
        player = null
        isActive = false
        current?.let {
            runCatching { if (it.isPlaying) it.stop() }
            runCatching { it.reset() }
            runCatching { it.release() }
        }
        abandonAudioFocus()
    }

    private fun handleFocusChange(change: Int) {
        when (change) {
            AudioManager.AUDIOFOCUS_LOSS -> release()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                player?.let { current ->
                    runCatching { if (current.isPlaying) current.pause() }
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                player?.let { current ->
                    runCatching { if (!current.isPlaying) current.start() }
                }
            }
        }
    }

    private fun requestAudioFocus(): Boolean {
        val manager = audioManager ?: return false
        val request = focusRequest ?: return false
        return manager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun abandonAudioFocus() {
        val request = focusRequest ?: return
        audioManager?.abandonAudioFocusRequest(request)
    }
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

    val audioManager = remember(context) {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }
    val audioAttributes = remember {
        AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
    }
    val controller = remember(audioUrl, audioManager) {
        ScreenAudioController(audioManager, audioAttributes)
    }

    SideEffect {
        controller.onError = {
            Toast.makeText(context, s.common.audioErrorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(controller) {
        onDispose { controller.release() }
    }

    IconButton(
        onClick = {
            if (controller.isActive) {
                controller.release()
                return@IconButton
            }
            if (!hasInternetConnection(context)) {
                Toast.makeText(context, s.common.audioOfflineMessage, Toast.LENGTH_SHORT).show()
                return@IconButton
            }
            controller.toggle(audioUrl)
        },
        modifier = modifier
    ) {
        Icon(
            imageVector = if (controller.isActive) {
                Icons.Filled.Stop
            } else {
                Icons.AutoMirrored.Filled.VolumeUp
            },
            contentDescription = if (controller.isActive) s.common.stop else s.common.audio
        )
    }
}
