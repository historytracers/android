// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.media.ExifInterface
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.historytracers.app.ui.LocalUiStrings
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val CUSTOM_IMAGE_FILE = "multiplication_object.png"
private const val CUSTOM_IMAGE_TEMP = "multiplication_object.tmp"
private const val MAX_SOURCE_DIMENSION = 1600
private const val CUSTOM_IMAGE_SIZE = 512

/**
 * Decodes the image chosen by the user, scaling it down and applying the EXIF
 * orientation so portrait photos are not shown sideways.
 */
suspend fun loadMultiplicationBitmap(context: Context, uri: Uri): Bitmap? =
    withContext(Dispatchers.IO) {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        runCatching {
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@withContext null

        var sample = 1
        while (bounds.outWidth / (sample * 2) >= MAX_SOURCE_DIMENSION ||
            bounds.outHeight / (sample * 2) >= MAX_SOURCE_DIMENSION
        ) {
            sample *= 2
        }

        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        val bitmap = runCatching {
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
        }.getOrNull() ?: return@withContext null

        val orientation = runCatching {
            resolver.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            }
        }.getOrNull() ?: ExifInterface.ORIENTATION_NORMAL

        applyOrientation(bitmap, orientation)
    }

private fun applyOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
    val matrix = Matrix()
    when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> {
            matrix.postRotate(90f)
            matrix.postScale(-1f, 1f)
        }
        ExifInterface.ORIENTATION_TRANSVERSE -> {
            matrix.postRotate(270f)
            matrix.postScale(-1f, 1f)
        }
        else -> return bitmap
    }
    val oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    if (oriented !== bitmap) bitmap.recycle()
    return oriented
}

/**
 * Saves the cropped image to the app's private storage. The fixed file name is
 * intentional: only one custom multiplication image is kept at a time.
 */
fun saveCustomMultiplicationImage(context: Context, bitmap: Bitmap): String {
    val file = File(context.filesDir, CUSTOM_IMAGE_FILE)
    val temp = File(context.filesDir, CUSTOM_IMAGE_TEMP)
    FileOutputStream(temp).use { output ->
        if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) {
            throw IOException("Could not encode the multiplication image")
        }
    }
    if (file.exists() && !file.delete()) {
        temp.delete()
        throw IOException("Could not replace the multiplication image")
    }
    if (!temp.renameTo(file)) {
        temp.copyTo(file, overwrite = true)
        temp.delete()
    }
    return file.absolutePath
}

/**
 * Square crop editor shown after the user picks an image. The square frame is
 * the visible area; pinch/zoom and drag (or the slider) adjust it before saving.
 */
@Composable
fun CustomImageAdjustDialog(
    bitmap: Bitmap,
    title: String,
    hint: String,
    onDismiss: () -> Unit,
    onSave: (Bitmap) -> Unit
) {
    val s = LocalUiStrings.current
    val density = LocalDensity.current
    val square = 260.dp
    val squarePx = with(density) { square.toPx() }
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    fun clampOffset(candidate: Offset, scaleValue: Float): Offset {
        val baseScale = max(squarePx / bitmap.width, squarePx / bitmap.height)
        val scaledWidth = bitmap.width * baseScale * scaleValue
        val scaledHeight = bitmap.height * baseScale * scaleValue
        val maxX = ((scaledWidth - squarePx) / 2f).coerceAtLeast(0f)
        val maxY = ((scaledHeight - squarePx) / 2f).coerceAtLeast(0f)
        return Offset(
            candidate.x.coerceIn(-maxX, maxX),
            candidate.y.coerceIn(-maxY, maxY)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .size(square)
                        .clip(RoundedCornerShape(16.dp))
                        .background(androidx.compose.ui.graphics.Color.Black)
                        .pointerInput(bitmap) {
                            detectTransformGestures { _, pan, zoom, _ ->
                                val newScale = (scale * zoom).coerceIn(1f, 4f)
                                scale = newScale
                                offset = clampOffset(offset + pan, newScale)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                    )
                }
                Spacer(Modifier.height(8.dp))
                Slider(
                    value = scale,
                    onValueChange = {
                        scale = it
                        offset = clampOffset(offset, it)
                    },
                    valueRange = 1f..4f
                )
                Text(
                    text = hint,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDismiss) {
                        Text(s.common.cancel)
                    }
                    Button(
                        onClick = {
                            onSave(
                                cropToSquare(
                                    bitmap = bitmap,
                                    scale = scale,
                                    offset = offset,
                                    displaySizePx = squarePx
                                )
                            )
                        }
                    ) {
                        Text(s.common.ok)
                    }
                }
            }
        }
    }
}

private fun cropToSquare(
    bitmap: Bitmap,
    scale: Float,
    offset: Offset,
    displaySizePx: Float
): Bitmap {
    val baseScale = max(displaySizePx / bitmap.width, displaySizePx / bitmap.height)
    val targetScale = baseScale * scale * (CUSTOM_IMAGE_SIZE / displaySizePx)
    val matrix = Matrix()
    matrix.postScale(targetScale, targetScale)
    matrix.postTranslate(
        CUSTOM_IMAGE_SIZE / 2f - bitmap.width * targetScale / 2f +
            offset.x * (CUSTOM_IMAGE_SIZE / displaySizePx),
        CUSTOM_IMAGE_SIZE / 2f - bitmap.height * targetScale / 2f +
            offset.y * (CUSTOM_IMAGE_SIZE / displaySizePx)
    )
    val result = Bitmap.createBitmap(CUSTOM_IMAGE_SIZE, CUSTOM_IMAGE_SIZE, Bitmap.Config.ARGB_8888)
    Canvas(result).drawBitmap(bitmap, matrix, Paint(Paint.FILTER_BITMAP_FLAG))
    return result
}
