// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.theme

import androidx.compose.ui.graphics.Color

val Brown80 = Color(0xFFBCAAA4)
val Brown60 = Color(0xFF8D6E63)
val Brown40 = Color(0xFF5D4037)
val Brown20 = Color(0xFF3E2723)

val Gold80 = Color(0xFFFFD54F)
val Gold60 = Color(0xFFFFC107)
val Gold40 = Color(0xFFFFA000)

val DarkBackground = Color(0xFF1C1B1F)
val LightBackground = Color(0xFFFFFBFE)

val ButtonYellow = Color(0xFFFFF8E0)
val ButtonYellowDark = Color(0xFFFFD700)
val OnButtonYellow = Color(0xFF333333)

val SkinColorPalette = listOf(
    Color(0xFF0C0704) to "#0C0704",
    Color(0xFF140B06) to "#140B06",
    Color(0xFF1A0F08) to "#1A0F08",
    Color(0xFF1F130B) to "#1F130B",
    Color(0xFF26170F) to "#26170F",
    Color(0xFF2E1C12) to "#2E1C12",
    Color(0xFF362115) to "#362115",
    Color(0xFF3F2A1A) to "#3F2A1A",
    Color(0xFF4A2A1A) to "#4A2A1A",
    Color(0xFF4E2E1B) to "#4E2E1B",
    Color(0xFF5C3B1E) to "#5C3B1E",
    Color(0xFF6A3E1E) to "#6A3E1E",
    Color(0xFF784421) to "#784421",
    Color(0xFF7E4E2B) to "#7E4E2B",
    Color(0xFF8D5524) to "#8D5524",
    Color(0xFFA5672C) to "#A5672C",
    Color(0xFFB36B3C) to "#B36B3C",
    Color(0xFFB97A56) to "#B97A56",
    Color(0xFFC68642) to "#C68642",
    Color(0xFFD99A6C) to "#D99A6C",
    Color(0xFFD9A066) to "#D9A066",
    Color(0xFFDEB887) to "#DEB887",
    Color(0xFFE0AC69) to "#E0AC69",
    Color(0xFFEEC9A3) to "#EEC9A3",
    Color(0xFFF4C2A1) to "#F4C2A1",
    Color(0xFFF5D0A9) to "#F5D0A9",
    Color(0xFFF8D5B0) to "#F8D5B0",
    Color(0xFFFAD5C0) to "#FAD5C0",
    Color(0xFFFCE5D5) to "#FCE5D5",
    Color(0xFFFFE5D9) to "#FFE5D9",
)

fun parseHexColor(hex: String): Color {
    val colorString = hex.removePrefix("#")
    val colorLong = colorString.toLong(16)
    return Color(0xFF000000 or colorLong)
}
