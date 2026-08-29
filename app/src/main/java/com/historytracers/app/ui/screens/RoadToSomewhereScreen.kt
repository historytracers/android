// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.historytracers.app.R
import com.historytracers.app.data.LevelGroupController
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.features.roadToSomewhereScreenStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.ButtonYellowDark
import com.historytracers.app.ui.theme.FlagBlueDark
import com.historytracers.app.ui.theme.FlagBlueLight
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

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
                    if (svgD[i] == '-') { sb.append('-'); i++ }
                    while (i < svgD.length && (svgD[i] == '.' || svgD[i].isDigit())) {
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

private val footPaths: List<String> by lazy {
    listOf(
        "M4085 12793 c-172 -22 -364 -101 -472 -194 -249 -216 -357 -646 -272 -1087 54 -282 193 -513 391 -649 95 -65 140 -88 248 -123 237 -77 493 -56 727 62 219 109 355 309 419 613 26 124 26 421 1 549 -68 338 -211 559 -461 708 -160 97 -394 145 -581 121z",
        "M6475 12530 c-133 -28 -252 -96 -360 -205 -159 -159 -231 -332 -242 -586 -19 -422 142 -773 431 -938 208 -119 426 -143 616 -69 111 43 231 136 322 248 292 362 202 1110 -167 1387 -73 54 -219 127 -300 148 -70 19 -243 28 -300 15z",
        "M1431 12289 c-195 -26 -436 -111 -604 -211 -143 -87 -205 -136 -338 -268 -413 -412 -577 -982 -439 -1525 135 -534 568 -929 1118 -1020 152 -25 427 -17 572 18 467 112 849 400 1087 821 89 156 169 393 192 569 14 102 14 324 0 423 -81 588 -511 1051 -1087 1175 -113 24 -383 34 -501 18z",
        "M8183 11316 c-189 -48 -313 -184 -375 -412 -28 -107 -31 -316 -4 -419 25 -97 76 -200 130 -263 244 -284 631 -355 877 -161 139 109 230 395 201 631 -23 187 -82 308 -209 428 -143 136 -288 200 -468 206 -61 2 -120 -2 -152 -10z",
        "M9535 10154 c-265 -67 -470 -327 -515 -656 -35 -250 64 -519 232 -631 153 -101 332 -102 500 -1 377 228 478 860 184 1158 -39 41 -85 74 -129 96 -59 29 -79 33 -160 36 -50 2 -101 1 -112 -2z",
        "M5075 10674 c-433 -34 -787 -104 -1124 -224 -429 -152 -712 -325 -968 -594 -193 -202 -326 -394 -458 -661 -214 -430 -315 -855 -315 -1321 0 -698 265 -1234 747 -1513 164 -94 375 -159 610 -186 799 -93 1448 -394 1788 -830 189 -242 284 -488 316 -815 42 -441 -70 -770 -532 -1570 -84 -146 -209 -362 -277 -480 -233 -404 -405 -779 -467 -1015 -109 -421 -6 -814 286 -1090 414 -390 1188 -481 2077 -245 347 93 635 271 937 581 285 293 518 634 755 1109 523 1044 877 2425 1002 3910 20 243 17 1017 -5 1190 -84 652 -225 1241 -393 1640 -307 730 -823 1239 -1670 1646 -602 289 -1062 418 -1663 464 -121 9 -544 11 -646 4z"
    )
}

private data class RoadFootBounds(var minX: Float, var minY: Float, var maxX: Float, var maxY: Float)

private fun buildFootPath(scale: Float = 0.1f, flipY: Boolean = true): Pair<Path, RoadFootBounds> {
    val path = Path()
    val bounds = RoadFootBounds(Float.MAX_VALUE, Float.MAX_VALUE, Float.MIN_VALUE, Float.MIN_VALUE)
    for (svgD in footPaths) {
        val sub = parseSvgPathToAndroidPath(svgD)
        val m = Matrix()
        m.preScale(scale, if (flipY) -scale else scale)
        val transformed = Path()
        sub.transform(m, transformed)
        val b = android.graphics.RectF()
        transformed.computeBounds(b, true)
        bounds.minX = minOf(bounds.minX, b.left)
        bounds.minY = minOf(bounds.minY, b.top)
        bounds.maxX = maxOf(bounds.maxX, b.right)
        bounds.maxY = maxOf(bounds.maxY, b.bottom)
        path.addPath(transformed)
    }
    return Pair(path, bounds)
}

@Composable
private fun FootIcon(color: Color, modifier: Modifier = Modifier) {
    val foot = remember { buildFootPath() }
    Canvas(modifier = modifier) {
        val b = foot.second
        val w = b.maxX - b.minX
        val h = b.maxY - b.minY
        if (w <= 0f || h <= 0f) return@Canvas
        val scale = minOf(size.width / w, size.height / h)
        val m = Matrix()
        m.setTranslate(
            (size.width - w * scale) / 2f - b.minX * scale,
            (size.height - h * scale) / 2f - b.minY * scale
        )
        m.preScale(scale, scale)
        val p = Path()
        p.addPath(foot.first, m)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.toArgb()
            style = Paint.Style.FILL
        }
        drawContext.canvas.nativeCanvas.drawPath(p, paint)
    }
}

private fun buildStairPath(): Path {
    return Path().apply {
        moveTo(10f, 70f)
        lineTo(26f, 70f)
        lineTo(26f, 76f)
        lineTo(42f, 76f)
        lineTo(42f, 82f)
        lineTo(58f, 82f)
        lineTo(58f, 88f)
        lineTo(74f, 88f)
        lineTo(74f, 94f)
        lineTo(10f, 94f)
        close()
    }
}

private val stairPath: Path by lazy { buildStairPath() }

@Composable
internal fun NumberOneOnStairs(color: Color, label: String, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val scale = minOf(size.width, size.height) / 100f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.toArgb()
            style = Paint.Style.FILL
        }
        val m = Matrix()
        m.setScale(scale, scale)
        val stair = Path()
        stair.addPath(stairPath, m)
        drawContext.canvas.nativeCanvas.drawPath(stair, paint)

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color.toArgb()
            textSize = 64f * scale
            textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        val fm = textPaint.fontMetrics
        val baseline = 70f * scale - fm.descent
        drawContext.canvas.nativeCanvas.drawText(label, size.width / 2f, baseline, textPaint)
    }
}

@Composable
fun RoadToSomewhereScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToWalkAmongNumbers: () -> Unit = {},
    onNavigateToCarryingInAddition: () -> Unit = {},
    onNavigateToPracticingAddition: () -> Unit = {},
    onNavigateToCongratulation: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val xs = roadToSomewhereScreenStringsForLanguage(LocalAppLanguage.current)

    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val completedSections by preferences.completedRoadToSomewhereSections.collectAsState(initial = emptySet())
    val scope = rememberCoroutineScope()

    val roadSectionIds = listOf("walk_among_numbers", "carrying_in_addition", "practicing_addition")
    val controller = remember {
        LevelGroupController(roadSectionIds, completedSections)
    }
    LaunchedEffect(completedSections) {
        controller.syncFromPersisted(completedSections)
    }
    val controllerCompleted by controller.completed.collectAsState()
    val allRoadSectionsDone = roadSectionIds.all { it in controllerCompleted }

    val claimedLevels by preferences.claimedLevels.collectAsState(initial = emptySet())

    fun claimRoadToSomewhereLevel() {
        if ("road_to_somewhere" in claimedLevels) return
        onScoreChanged(currentScore + 10)
        scope.launch { preferences.markLevelClaimed("road_to_somewhere") }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = s.common.back)
                }
                Text(
                    text = hts.aRoadToSomewhere,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

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
                    onClick = {
                        onNavigateToWalkAmongNumbers()
                    },
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (completedSections.contains("walk_among_numbers")) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    FootIcon(
                        color = OnButtonYellow,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.walkAmongNumbers,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToCarryingInAddition,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (completedSections.contains("carrying_in_addition")) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    NumberOneOnStairs(
                        color = OnButtonYellow,
                        label = xs.numberOne,
                        modifier = Modifier.size(52.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.carryingInAddition,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = {},
                    enabled = false,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = ButtonYellow,
                        disabledContainerColor = ButtonYellow,
                        disabledContentColor = OnButtonYellow
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.DirectionsRun,
                        contentDescription = null,
                        modifier = Modifier.size(52.dp),
                        tint = OnButtonYellow
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.runningAmongNumbers,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(32.dp))

                FilledIconButton(
                    onClick = onNavigateToPracticingAddition,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (completedSections.contains("practicing_addition")) ButtonYellowDark else ButtonYellow
                    )
                ) {
                    Text(
                        text = xs.practicingExpression,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = OnButtonYellow
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = xs.practicingAddition,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(Modifier.height(48.dp))

                FilledIconButton(
                    onClick = {
                        claimRoadToSomewhereLevel()
                        onNavigateToCongratulation()
                    },
                    enabled = allRoadSectionsDone,
                    modifier = Modifier.size(96.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if ("road_to_somewhere" in claimedLevels) FlagBlueDark else FlagBlueLight,
                        disabledContainerColor = FlagBlueLight
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
                    text = s.common.nextLevel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }
    }
}
