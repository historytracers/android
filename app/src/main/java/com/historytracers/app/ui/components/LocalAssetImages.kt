// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

private const val ANDROID_ASSET_PREFIX = "file:///android_asset/"

private val LOCAL_ASSET_IMAGES = mapOf(
    "https://www.historytracers.org/images/ESA/Planck_history_of_Universe.jpg"
        to "ESA/planck_history_of_universe.jpg"
)

fun resolveImageSource(url: String): Any =
    LOCAL_ASSET_IMAGES[url]?.let { ANDROID_ASSET_PREFIX + it } ?: url
