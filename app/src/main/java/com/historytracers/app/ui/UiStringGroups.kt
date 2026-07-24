// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui

data class AppCommonStrings(
    val home: String, val settings: String, val menu: String, val back: String,
    val open: String, val error: String, val language: String,
    val breakTime: String, val breakTimeDesc: String, val minutes: String,
    val all: String, val exercises: String, val yes: String, val no: String,
    val correct: String, val incorrect: String, val retry: String,
    val runningAndGrowing: String, val tryAgain: String, val score: String,
    val step: String, val previous: String, val next: String, val jump: String,
    val selectLevel: String, val answer: String, val unknown: String,
    val copyUrl: String, val goToUrl: String, val openApp: String,
    val close: String, val streak: String, val days: String,
    val breakReminderTitle: String, val breakMessage: String, val imBack: String,
    val voice: String, val evaluateOn: String, val reminder: String,
    val reminderTime: String, val reminderTitle: String, val reminderMessage: String,
    val ok: String, val cancel: String, val selectTime: String,
    val previousMonth: String, val nextMonth: String, val skinColor: String,
    val value: String, val reset: String, val resetDesc: String,
    val write: String, val level: String, val levelComplete: String,
    val levelCompleteMax: String, val resetHint: String, val resetToZero: String,
    val tracers: String, val building: String, val nextLevel: String,
    val numberOfClaps: String, val unsupportedContentType: String,
    val appNotInstalled: String, val levelPrefix: String, val valuePrefix: String,
    val stepPrefix: String, val newExercise: String, val doExercise: String,
    val nextStep: String, val number: String, val auto: String, val stop: String,
    val complete: String, val patreon: String, val paypal: String,
    val thinking: String, val randomly: String, val sources: String,
    val originalText: String, val slowly: String, val fast: String,
)

data class HubTitleStrings(
    val aRoadToSomewhere: String, val birth: String, val death: String,
    val firstSteps: String, val workout: String, val abacus: String,
    val iDontKnow: String, val iAmNotLikeYou: String, val myHands: String,
    val myBody: String, val drawing: String, val numbers: String,
    val sequenceGame: String, val familyPart1: String, val aboutUs: String,
    val aboutDescription: String, val isItFree: String,
    val isItFreeContent: String, val isItFreeDonateCall: String,
    val isItFreeFollowPrefix: String, val isItFreeFollowSuffix: String,
    val history: String, val naturalFamiliesPart2: String,
    val goingToInfinity: String, val whereAreWeFrom: String,
    val returning: String, val tomokoHoult: String, val aPal: String,
    val langEnUs: String, val langPtBr: String, val langEsEs: String,
    val congratulationTitle: String, val congratulationMessage: String,
    val whoWalkFirst: String, val whoWalkFirstButton: String,
)

data class PlaceValueStrings(
    val levelUnits: String, val levelTens: String, val levelHundreds: String,
    val levelThousands: String, val levelTenThousands: String,
    val levelHundredThousands: String, val levelMillions: String,
    val levelTenMillions: String, val placeUnits: String, val placeTens: String,
    val placeHundreds: String, val placeThousands: String,
    val placeTenThousands: String, val placeHundredThousands: String,
    val placeMillions: String, val placeTenMillions: String, val placeNext: String,
)

data class BodyExerciseStrings(
    val clapInstructions: String, val clapReinforce: String,
    val feetAndHandsReinforce: String, val relationshipReinforce: String,
    val feetAndHandsInstructions: String, val speedSlow: String,
    val speedFast: String, val clapSkinColorHint: String,
    val feetAndHandsSkinColorHint: String, val clap: String, val steps: String,
    val jumps: String, val numberOfClapsStepsJumps: String,
    val clapCounter: String, val stepsCounter: String, val jumpsCounter: String,
    val clapCompletionMessage: String,
    val exercisingHandsCompletionMessage: String,
    val exercisingFeetAndHandsCompletionMessage: String,
    val exercisingAdditionCompletionMessage: String,
    val exercisingAddition: String, val exercisingAdditionInstruction: String,
    val addingTwoNumbers: String, val addingLargeNumbers: String,
    val exercisingHands: String, val walking: String,
    val exercisingFeetAndHands: String,
    val exercisingMultiplication: String,
    val exercisingMultiplicationL2: String,
    val exercisingMultiplicationL2Description: String,
    val relationship: String,
)

data class AbacusWriteStrings(
    val writingToSoroban: String, val writingToSuanpan: String,
    val writingLargeNumbers: String, val largeNumbers: String,
    val sorobanWritingInstruction: String,
    val suanpanWritingInstruction: String,
    val largeNumbersWritingInstruction: String,
    val sorobanAbbr: String, val suanpanAbbr: String,
    val sorobanMode: String, val suanpanMode: String,
)

data class PracticingAdditionStrings(
    val practicingAddition: String,
    val practicingAdditionInstruction: String,
    val stepWriteFirst: String, val stepAddTo: String,
    val stepCarrying: String, val stepFinal: String,
    val feedbackPerfect: String, val feedbackCorrect: String,
    val feedbackCongratulations: String,
)

data class MwStrings(
    val multiplyingWithAbacus: String,
    val multiplyingWithAbacusDescription: String,
    val multiplyingWithAbacusInstruction: String,
    val mwStepPrefix: String, val mwWelcomeMessage: String,
    val mwStepWriteFirst: String, val mwUnitsProductHeader: String,
    val mwStepAdd: String, val mwStepCarry: String,
    val mwStepFinal: String, val mwCongratulations: String,
    val mwStepStatus: String, val mwCorrectMessage: String,
    val mwPerfectMessage: String, val mwLastLevelMessage: String,
    val mw2Title: String, val mw2Description: String, val mw3Title: String,
    val mwlInstruction: String, val mwlShiftInstruction: String,
    val mwlStoreInstruction: String, val mwlResetInstruction: String,
    val mwlAddStoredPrefix: String, val mwlLastLevelMessage: String,
    val mw2Instruction: String, val mw2StepWriteFirst: String,
    val mw2LastLevelMessage: String,
)

data class SbwStrings(
    val returningWithAbacus: String, val subtractingWithAbacus: String,
    val sbwTitle: String, val sbwInstruction: String,
    val sbwStepPrefix: String, val sbwSetupInstruction: String,
    val sbwSubStepInstruction: String, val sbwSubStepDesc: String,
    val sbwBorrowSetNine: String, val sbwBorrowReduce: String,
    val sbwBorrowSubUnits: String, val sbwFinalInstruction: String,
    val sbwCorrectMessage: String, val sbwPerfectMessage: String,
    val sbwCongratsMessage: String, val sbwLastLevelMessage: String,
    val sbwWelcomeMessage: String, val sbwStepStatus: String,
)

data class YupanaStrings(
    val yupana: String, val quipu: String,
    val handsOnYupana: String, val movingInYupana: String,
    val practicingWithYupana: String,
    val practicingAdditionYupanaInstruction: String,
    val yupanaValues: String, val yupanaResult: String,
    val yupanaStepByStep: String, val moves: String,
    val ypPerfectMessage: String, val ypCorrectMessage: String,
    val ypAddTo: String, val ypAddToCarry: String,
    val ypCarrying: String, val ypCarryingCarry: String,
    val ypCongratsMessage: String, val ypLastLevelMessage: String,
    val ypRedPhase: String, val ypBluePhase: String,
)

data class MiscStrings(
    val multiplicationTable: String,
    val multiplicationTableDescription: String,
    val abacusInOrdersReading: String, val abacusInRereading: String,
)
