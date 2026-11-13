// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.components

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

/**
 * The same hand and foot shapes used by the body lessons, drawn with the
 * project's own paths: two hands and two feet, each one with five fingers or
 * five toes.
 *
 * Shared by the body screens and by the multiplication table screens that
 * connect the table of five to our body.
 */
@Composable
fun HandsAndFeetFigure(modifier: Modifier = Modifier) {
    val handPath = remember { buildHandPath() }
    val footResult = remember { buildFootPath() }
    val footPath = footResult.first
    val footBounds = footResult.second

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(2f)
    ) {
        val s = size.width / 600f
        val cx = size.width / 2f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color(0xFFF4C2A1).hashCode()
            style = Paint.Style.FILL
            strokeJoin = Paint.Join.ROUND
        }

        val handScale = 0.55f * s
        val handWristY = 30f * s - 158.5f * handScale
        val restOff = 140f * s
        val leftHandRightPx = with(density) { 30.dp.toPx() }
        val rightHandLeftPx = with(density) { 60.dp.toPx() }

        val leftMatrix = Matrix().apply {
            setTranslate(cx - restOff + leftHandRightPx, handWristY)
            preScale(handScale, handScale)
        }
        val left = Path().apply { addPath(handPath, leftMatrix) }
        drawContext.canvas.nativeCanvas.drawPath(left, paint)

        val rightMatrix = Matrix().apply {
            setTranslate(cx + restOff - rightHandLeftPx, handWristY)
            preScale(-handScale, handScale)
        }
        val right = Path().apply { addPath(handPath, rightMatrix) }
        drawContext.canvas.nativeCanvas.drawPath(right, paint)

        val footScale = s * (0.125f / 0.6f) * 0.7f
        val footCx = (footBounds.minX + footBounds.maxX) / 2f
        val footCy = (footBounds.minY + footBounds.maxY) / 2f
        val footY = 262f * s
        val feetRightPx = with(density) { 30.dp.toPx() }

        fun drawFoot(xOffset: Float, mirrorX: Boolean) {
            val m = Matrix()
            m.setTranslate(cx + xOffset + feetRightPx - footCx * footScale, footY - footCy * footScale)
            m.preScale(if (mirrorX) -footScale else footScale, footScale)
            val fp = Path()
            fp.addPath(footPath, m)
            drawContext.canvas.nativeCanvas.drawPath(fp, paint)
        }

        drawFoot(-55f * s, mirrorX = true)
        drawFoot(55f * s, mirrorX = false)
    }
}

private val footToePaths: List<String> by lazy {
    listOf(
        "M4085 12793 c-172 -22 -364 -101 -472 -194 -249 -216 -357 -646 -272 -1087 54 -282 193 -513 391 -649 95 -65 140 -88 248 -123 237 -77 493 -56 727 62 219 109 355 309 419 613 26 124 26 421 1 549 -68 338 -211 559 -461 708 -160 97 -394 145 -581 121z",
        "M6475 12530 c-133 -28 -252 -96 -360 -205 -159 -159 -231 -332 -242 -586 -19 -422 142 -773 431 -938 208 -119 426 -143 616 -69 111 43 231 136 322 248 292 362 202 1110 -167 1387 -73 54 -219 127 -300 148 -70 19 -243 28 -300 15z",
        "M1431 12289 c-195 -26 -436 -111 -604 -211 -143 -87 -205 -136 -338 -268 -413 -412 -577 -982 -439 -1525 135 -534 568 -929 1118 -1020 152 -25 427 -17 572 18 467 112 849 400 1087 821 89 156 169 393 192 569 14 102 14 324 0 423 -81 588 -511 1051 -1087 1175 -113 24 -383 34 -501 18z",
        "M8183 11316 c-189 -48 -313 -184 -375 -412 -28 -107 -31 -316 -4 -419 25 -97 76 -200 130 -263 244 -284 631 -355 877 -161 139 109 230 395 201 631 -23 187 -82 308 -209 428 -143 136 -288 200 -468 206 -61 2 -120 -2 -152 -10z",
        "M9535 10154 c-265 -67 -470 -327 -515 -656 -35 -250 64 -519 232 -631 153 -101 332 -102 500 -1 377 228 478 860 184 1158 -39 41 -85 74 -129 96 -59 29 -79 33 -160 36 -50 2 -101 1 -112 -2z",
        "M5075 10674 c-433 -34 -787 -104 -1124 -224 -429 -152 -712 -325 -968 -594 -193 -202 -326 -394 -458 -661 -214 -430 -315 -855 -315 -1321 0 -698 265 -1234 747 -1513 164 -94 375 -159 610 -186 799 -93 1448 -394 1788 -830 189 -242 284 -488 316 -815 42 -441 -70 -770 -532 -1570 -84 -146 -209 -362 -277 -480 -233 -404 -405 -779 -467 -1015 -109 -421 -6 -814 286 -1090 414 -390 1188 -481 2077 -245 347 93 635 271 937 581 285 293 518 634 755 1109 523 1044 877 2425 1002 3910 20 243 17 1017 -5 1190 -84 652 -225 1241 -393 1640 -307 730 -823 1239 -1670 1646 -602 289 -1062 418 -1663 464 -121 9 -544 11 -646 4z"
    )
}

private data class FootBounds(var minX: Float, var minY: Float, var maxX: Float, var maxY: Float)

private fun parseSvgPathToAndroidPath(svgD: String): Path {
    val path = Path()
    var i = 0
    var curX = 0f
    var curY = 0f
    while (i < svgD.length) {
        val ch = svgD[i]
        if (ch.isWhitespace()) { i++; continue }
        when (ch) {
            'M' -> {
                i++
                val nums = mutableListOf<Float>()
                while (i < svgD.length) {
                    if (svgD[i].isWhitespace() || svgD[i] == ',') { i++; continue }
                    if (svgD[i] in 'a'..'z' || svgD[i] in 'A'..'Z') break
                    val sb = StringBuilder()
                    while (i < svgD.length && (svgD[i] == '-' || svgD[i] == '.' || svgD[i].isDigit())) {
                        sb.append(svgD[i]); i++
                    }
                    if (sb.isNotEmpty()) nums.add(sb.toString().toFloat())
                }
                if (nums.size >= 2) {
                    curX = nums[0]; curY = nums[1]
                    path.moveTo(curX, curY)
                }
            }
            'c' -> {
                i++
                val nums = mutableListOf<Float>()
                while (i < svgD.length) {
                    if (svgD[i].isWhitespace() || svgD[i] == ',') { i++; continue }
                    if (svgD[i] in 'a'..'z' || svgD[i] in 'A'..'Z') break
                    val sb = StringBuilder()
                    if (svgD[i] == '-') { sb.append('-'); i++ }
                    while (i < svgD.length && (svgD[i] == '.' || svgD[i].isDigit())) {
                        sb.append(svgD[i]); i++
                    }
                    if (sb.isNotEmpty()) nums.add(sb.toString().toFloat())
                }
                var j = 0
                while (j + 5 < nums.size) {
                    path.rCubicTo(nums[j], nums[j + 1], nums[j + 2], nums[j + 3], nums[j + 4], nums[j + 5])
                    curX += nums[j + 4]; curY += nums[j + 5]
                    j += 6
                }
            }
            'z', 'Z' -> { path.close(); i++ }
            else -> i++
        }
    }
    return path
}

private fun buildFootPath(scale: Float = 0.1f, flipY: Boolean = true): Pair<Path, FootBounds> {
    val path = Path()
    val bounds = FootBounds(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
    for (svgD in footToePaths) {
        val sub = parseSvgPathToAndroidPath(svgD)
        val m = Matrix()
        m.preScale(scale, if (flipY) -scale else scale)
        val transformed = Path()
        sub.transform(m, transformed)
        val b = RectF()
        transformed.computeBounds(b, true)
        bounds.minX = minOf(bounds.minX, b.left)
        bounds.minY = minOf(bounds.minY, b.top)
        bounds.maxX = maxOf(bounds.maxX, b.right)
        bounds.maxY = maxOf(bounds.maxY, b.bottom)
        path.addPath(transformed)
    }
    return Pair(path, bounds)
}
