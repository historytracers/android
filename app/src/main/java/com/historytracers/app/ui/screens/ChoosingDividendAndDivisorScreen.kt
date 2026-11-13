// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.data.UserPreferences
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.LocalUiStrings
import com.historytracers.app.ui.components.AutoSizeTitleText
import com.historytracers.app.ui.features.choosingDividendAndDivisorScreenStringsForLanguage
import com.historytracers.app.ui.features.hubTitleStringsForLanguage
import com.historytracers.app.ui.theme.ButtonYellow
import com.historytracers.app.ui.theme.OnButtonYellow
import kotlinx.coroutines.launch

private const val SECTION_ID = "choosing_dividend_and_divisor"
private const val POINTS_PER_DIVISION = 2
private const val DEFAULT_DIVIDEND = 100
private const val DEFAULT_DIVISOR = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosingDividendAndDivisorScreen(
    currentScore: Int = 0,
    onScoreChanged: (Int) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToLongDivision: () -> Unit = {}
) {
    val s = LocalUiStrings.current
    val xs = choosingDividendAndDivisorScreenStringsForLanguage(LocalAppLanguage.current)
    val hts = hubTitleStringsForLanguage(LocalAppLanguage.current)
    val context = LocalContext.current
    val preferences = remember { UserPreferences(context) }
    val scope = rememberCoroutineScope()

    val initialScore = remember { currentScore }
    var totalAwarded by remember { mutableIntStateOf(0) }

    val initialExercise = remember { divisionExercise(DEFAULT_DIVIDEND, DEFAULT_DIVISOR) }
    var dividendInput by remember { mutableStateOf(DEFAULT_DIVIDEND.toString()) }
    var selectedDivisor by remember { mutableIntStateOf(DEFAULT_DIVISOR) }
    var setupError by remember { mutableStateOf(false) }
    var showDivisorMenu by remember { mutableStateOf(false) }

    var dividend by remember { mutableIntStateOf(initialExercise.dividend) }
    var divisor by remember { mutableIntStateOf(initialExercise.divisor) }
    var steps by remember { mutableStateOf(initialExercise.steps) }
    var firstPlayedStep by remember { mutableIntStateOf(initialExercise.firstPlayedStep) }
    var stepIndex by remember { mutableIntStateOf(initialExercise.firstPlayedStep) }
    var committed by remember { mutableStateOf(listOf<DivisionStep>()) }
    var pending by remember { mutableStateOf<DivisionStep?>(null) }
    var typedDigit by remember { mutableStateOf("") }
    var remainderTyped by remember { mutableStateOf("") }
    var previewProduct by remember { mutableStateOf<Int?>(null) }
    var feedback by remember { mutableStateOf("") }
    var finished by remember { mutableStateOf(false) }
    var format by remember { mutableStateOf(DivisionFormat.Below) }

    val quotientFocus = remember { FocusRequester() }
    val remainderFocus = remember { FocusRequester() }
    var typing by remember { mutableStateOf(false) }

    fun startDivision() {
        val parsedDividend = dividendInput.toIntOrNull()
        if (parsedDividend == null || parsedDividend < 0 || parsedDividend > 9999) {
            setupError = true
            return
        }
        setupError = false
        val exercise = divisionExercise(parsedDividend, selectedDivisor)
        dividend = exercise.dividend
        divisor = exercise.divisor
        steps = exercise.steps
        firstPlayedStep = exercise.firstPlayedStep
        stepIndex = exercise.firstPlayedStep
        committed = emptyList()
        pending = null
        typedDigit = ""
        remainderTyped = ""
        previewProduct = null
        feedback = ""
        finished = false
    }

    val currentStep = steps.getOrNull(stepIndex)
    val totalSteps = steps.size - firstPlayedStep
    val doneDigits = committed.map { it.quotientDigit } + listOfNotNull(pending?.quotientDigit)
    val working = committed.lastOrNull()?.remainder ?: dividend
    val fullQuotient = if (divisor > 0) dividend / divisor else 0
    val fullRemainder = if (divisor > 0) dividend % divisor else 0

    val density = LocalDensity.current
    val charWidth = rememberBoardCharWidth()
    val boxWidth = with(density) { (charWidth * 1.15f).toDp() }
    val boxHeight = with(density) { BOARD_TEXT_STYLE.lineHeight.toDp() }
    val remainderBoxWidth = with(density) { (charWidth * 4.3f).toDp() }

    LaunchedEffect(pending, stepIndex, format) {
        if (typing && !finished) {
            runCatching {
                if (pending == null) quotientFocus.requestFocus() else remainderFocus.requestFocus()
            }
        }
    }

    fun typeDigit(raw: String) {
        if (finished || pending != null) return
        val step = steps.getOrNull(stepIndex) ?: return
        val digit = raw.filter { it.isDigit() }.takeLast(1)
        if (digit.isEmpty()) {
            typedDigit = ""
            previewProduct = null
            feedback = ""
            return
        }
        val numeric = digit.toInt()
        typedDigit = digit
        if (numeric != step.quotientDigit) {
            previewProduct = scaledProduct(divisor * numeric, working, divisor)
            feedback = xs.wrongDigitMessage
            return
        }
        typedDigit = ""
        previewProduct = null
        feedback = ""
        remainderTyped = ""
        pending = step
    }

    fun typeRemainder(raw: String, submit: Boolean) {
        val current = pending ?: return
        val value = raw.filter { it.isDigit() }.take(4)
        remainderTyped = value
        feedback = ""
        if (value.isEmpty()) return
        if (value.toInt() == current.remainder) {
            committed = committed + current
            stepIndex += 1
            pending = null
            remainderTyped = ""
            feedback = ""
            if (stepIndex >= steps.size) {
                finished = true
                totalAwarded += POINTS_PER_DIVISION
                onScoreChanged(initialScore + totalAwarded)
                scope.launch {
                    preferences.markLongDivisionSectionCompleted(SECTION_ID)
                    preferences.recordLessonCompletion()
                }
            }
            return
        }
        if (submit || value.length >= current.remainder.toString().length) {
            feedback = xs.wrongRemainderMessage
        }
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
                AutoSizeTitleText(
                    text = xs.title,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = dividendInput,
                    onValueChange = { raw ->
                        dividendInput = raw.filter { it.isDigit() }.take(4)
                        setupError = false
                    },
                    label = { Text(xs.dividendLabel) },
                    singleLine = true,
                    isError = setupError,
                    textStyle = MaterialTheme.typography.bodyLarge,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { startDivision() }),
                    modifier = Modifier.weight(1f)
                )

                Box {
                    OutlinedButton(onClick = { showDivisorMenu = true }) {
                        Text("${xs.divisorLabel}: $selectedDivisor")
                    }
                    DropdownMenu(
                        expanded = showDivisorMenu,
                        onDismissRequest = { showDivisorMenu = false }
                    ) {
                        (1..9).forEach { value ->
                            DropdownMenuItem(
                                text = { Text(value.toString()) },
                                onClick = {
                                    selectedDivisor = value
                                    showDivisorMenu = false
                                }
                            )
                        }
                    }
                }

                FilledTonalButton(
                    onClick = { startDivision() },
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = xs.start, fontWeight = FontWeight.Bold)
                }
            }

            if (setupError) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = xs.invalidDividendMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = xs.formatLabel,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(2.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = format == DivisionFormat.Below,
                    onClick = { format = DivisionFormat.Below },
                    label = { Text(xs.resultBelow) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ButtonYellow,
                        selectedLabelColor = OnButtonYellow
                    )
                )
                FilterChip(
                    selected = format == DivisionFormat.Top,
                    onClick = { format = DivisionFormat.Top },
                    label = { Text(xs.resultTop) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ButtonYellow,
                        selectedLabelColor = OnButtonYellow
                    )
                )
            }

            Spacer(Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 0.dp,
                color = Color.White,
                contentColor = Color(0xFF1A1A1A),
                border = BorderStroke(1.dp, Color(0xFFD6D6D6))
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    DivisionBoard(
                        dividend = dividend,
                        divisor = divisor,
                        committed = committed,
                        previewProduct = previewProduct,
                        quotientText = doneDigits.joinToString(""),
                        totalSteps = totalSteps,
                        format = format,
                        quotientContent = {
                            QuotientBoxes(
                                doneDigits = doneDigits,
                                totalSteps = totalSteps,
                                typedDigit = typedDigit,
                                onDigitTyped = { typeDigit(it) },
                                focusRequester = quotientFocus,
                                onFocusChanged = { focused -> if (focused) typing = true },
                                enabled = !finished && pending == null,
                                boxWidth = boxWidth,
                                boxHeight = boxHeight,
                                label = xs.activeDigitLabel
                            )
                        },
                        pendingStep = pending,
                        remainderContent = if (pending != null) {
                            {
                                RemainderField(
                                    value = remainderTyped,
                                    onValueChange = { typeRemainder(it, false) },
                                    onDone = { typeRemainder(remainderTyped, true) },
                                    focusRequester = remainderFocus,
                                    onFocusChanged = { focused -> if (focused) typing = true },
                                    width = remainderBoxWidth,
                                    height = boxHeight,
                                    label = xs.remainderInputLabel
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            if (!finished) {
                val pendingStep = pending
                if (pendingStep != null) {
                    Text(
                        text = "${xs.subtractPrompt} ${xs.subtractFormat.format(working, pendingStep.product)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                } else if (currentStep != null) {
                    Text(
                        text = "${xs.promptPrefix} ${xs.promptDivision.format(currentStep.partialValue, divisor)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                if (feedback.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = feedback,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }

                Spacer(Modifier.height(6.dp))

                TimesTable(
                    titleFormat = xs.timesTableTitle,
                    entryFormat = xs.timesTableEntry,
                    divisor = divisor
                )
            }

            if (finished) {
                Text(
                    text = "\uD83C\uDFC5",
                    fontSize = 48.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = xs.resultFormat.format(dividend, divisor, fullQuotient, fullRemainder),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2E7D32)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "${xs.quotientLabel}: $fullQuotient   ${xs.remainderLabel}: $fullRemainder",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(16.dp))

                FilledTonalButton(
                    onClick = { startDivision() },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = ButtonYellow,
                        contentColor = OnButtonYellow
                    )
                ) {
                    Text(
                        text = xs.newDivision,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            FilledTonalButton(
                onClick = onNavigateToLongDivision,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = hts.longDivision,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun QuotientBoxes(
    doneDigits: List<Int>,
    totalSteps: Int,
    typedDigit: String,
    onDigitTyped: (String) -> Unit,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    enabled: Boolean,
    boxWidth: Dp,
    boxHeight: Dp,
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .clickable(enabled = enabled) {
                runCatching { focusRequester.requestFocus() }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until totalSteps) {
            val digit = doneDigits.getOrNull(index)
            when {
                digit != null -> key("digit$index") {
                    QuotientDigitBox(text = digit.toString(), boxWidth = boxWidth, boxHeight = boxHeight)
                }
                enabled && index == doneDigits.size -> key("active") {
                    ActiveQuotientBox(
                        value = typedDigit,
                        onValueChange = onDigitTyped,
                        focusRequester = focusRequester,
                        onFocusChanged = onFocusChanged,
                        boxWidth = boxWidth,
                        boxHeight = boxHeight,
                        label = label
                    )
                }
                else -> key("empty$index") {
                    QuotientDigitBox(text = "", boxWidth = boxWidth, boxHeight = boxHeight)
                }
            }
        }
    }
}

@Composable
private fun QuotientDigitBox(text: String, boxWidth: Dp, boxHeight: Dp) {
    Box(
        modifier = Modifier
            .width(boxWidth)
            .height(boxHeight),
        contentAlignment = Alignment.Center
    ) {
        if (text.isNotEmpty()) {
            Text(
                text = text,
                style = BOARD_TEXT_STYLE,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }
    }
}

@Composable
private fun ActiveQuotientBox(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    boxWidth: Dp,
    boxHeight: Dp,
    label: String
) {
    Box(
        modifier = Modifier
            .width(boxWidth)
            .height(boxHeight)
            .background(Color(0xFFEAF3FB)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = { raw -> onValueChange(raw.filter { it.isDigit() }.takeLast(1)) },
            singleLine = true,
            textStyle = BOARD_TEXT_STYLE.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1B1F)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChanged(it.isFocused) }
                .semantics { contentDescription = label }
        )
    }
}

@Composable
private fun RemainderField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    width: Dp,
    height: Dp,
    label: String
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .background(Color(0xFFEAF3FB)),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = { raw -> onValueChange(raw.filter { it.isDigit() }.take(4)) },
            singleLine = true,
            textStyle = BOARD_TEXT_STYLE.copy(
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C1B1F)
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChanged(it.isFocused) }
                .semantics { contentDescription = label }
        )
    }
}
