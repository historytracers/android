// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class YupanaSharedStrings(
    val yupana: String,
    val handsOnYupana: String,
    val tawantsuyu: String,
    val multiplyingWithYupana: String,
    val ypPerfectMessage: String,
    val ypCorrectMessage: String,
    val ypAddTo: String,
    val ypAddToCarry: String,
    val ypCarrying: String,
    val ypCarryingCarry: String,
    val ypCongratsMessage: String,
    val ypLastLevelMessage: String,
    val ypRedPhase: String,
    val ypBluePhase: String,
    val ypNothingLeft: String,
    val ypNothingRight: String,
    val ypNothingResult: String,
    val ypSumTen: String,
    val ypCarry: String,
    val ypButtonInstruction: String,
)

val EnYupanaSharedStrings = YupanaSharedStrings(
    yupana = "Yupana",
    handsOnYupana = "Hands on Yupana",
    tawantsuyu = "Tawantsuyu",
    multiplyingWithYupana = "Multiplying with Yupana",
    ypPerfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFECT! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\nGreat job using the Yupana!",
    ypCorrectMessage = "\u2705 Correct! Click 'Next step' to continue.",
    ypAddTo = "Add to %s: %d + %d = %d. Place markers for %d in the Yupana.",
    ypAddToCarry = "Add to %s: %d + %d + %d (carry) = %d. Place markers for %d in the Yupana.",
    ypCarrying = "Carrying 1: Adding to %s gives %d + %d = %d (> 10). Place markers for %d in the Yupana. The carry will be added to %s in the next step.",
    ypCarryingCarry = "Carrying 1: Adding to %s gives %d + %d + %d (carry) = %d (> 10). Place markers for %d in the Yupana. The carry will be added to %s in the next step.",
    ypCongratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 CONGRATULATIONS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nYou calculated %d + %d = %d\nClick 'New exercise' to practice more!",
    ypLastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 ALL LEVELS COMPLETE! \uD83C\uDFC6\uD83C\uDF89\nYou mastered all 9 levels! Click \"New exercise\" to start again at level 1.",
    ypRedPhase = "Place RED markers for %d in the %s.",
    ypBluePhase = "Place BLUE markers for %d in the %s.",
    ypNothingLeft = "Nothing to add from the left in the %s column.",
    ypNothingRight = "Nothing to add from the right in the %s column.",
    ypNothingResult = "Nothing to place in the %s column.",
    ypSumTen = "%d + %d + %d = %d → 0 in the %s column, carry %d.",
    ypCarry = "(carry)",
    ypButtonInstruction = "Tap the Yupana columns to place or remove markers. When the row is correct, click \"Next Step\". Use \"New Exercise\" to restart or \"Next Level\" to advance.",
)

val PtYupanaSharedStrings = YupanaSharedStrings(
    yupana = "Yupana",
    handsOnYupana = "M\u00e3os no Yupana",
    tawantsuyu = "Tawantsuyu",
    multiplyingWithYupana = "Multiplicando com a Yupana",
    ypPerfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PERFEITO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\n\u00d3timo trabalho usando a Yupana!",
    ypCorrectMessage = "\u2705 Correto! Clique em 'Pr\u00f3ximo passo' para continuar.",
    ypAddTo = "Adicione a %s: %d + %d = %d. Coloque marcadores para %d na Yupana.",
    ypAddToCarry = "Adicione a %s: %d + %d + %d (transporte) = %d. Coloque marcadores para %d na Yupana.",
    ypCarrying = "Carregando 1: Adicionar a %s resulta em %d + %d = %d (> 10). Coloque marcadores para %d na Yupana. O transporte ser\u00e1 adicionado a %s no pr\u00f3ximo passo.",
    ypCarryingCarry = "Carregando 1: Adicionar a %s resulta em %d + %d + %d (transporte) = %d (> 10). Coloque marcadores para %d na Yupana. O transporte ser\u00e1 adicionado a %s no pr\u00f3ximo passo.",
    ypCongratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 PARAB\u00c9NS! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nVoc\u00ea calculou %d + %d = %d\nClique em 'Novo exerc\u00edcio' para praticar mais!",
    ypLastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 TODOS OS N\u00cdVEIS CONCLU\u00cdDOS! \uD83C\uDFC6\uD83C\uDF89\nVoc\u00ea dominou todos os 9 n\u00edveis! Clique em \"Novo exerc\u00edcio\" para come\u00e7ar novamente no n\u00edvel 1.",
    ypRedPhase = "Coloque marcadores VERMELHOS para %d na %s.",
    ypBluePhase = "Coloque marcadores AZUIS para %d na %s.",
    ypNothingLeft = "Nada a adicionar da esquerda na coluna %s.",
    ypNothingRight = "Nada a adicionar da direita na coluna %s.",
    ypNothingResult = "Nada a colocar na coluna %s.",
    ypSumTen = "%d + %d + %d = %d → 0 na coluna %s, transporte %d.",
    ypCarry = "(transporte)",
    ypButtonInstruction = "Toque nas colunas da Yupana para colocar ou remover marcadores. Quando a linha estiver correta, clique em \"Pr\u00f3ximo passo\". Use \"Novo exerc\u00edcio\" para reiniciar ou \"Pr\u00f3ximo n\u00edvel\" para avan\u00e7ar.",
)

val EsYupanaSharedStrings = YupanaSharedStrings(
    yupana = "Yupana",
    handsOnYupana = "Manos en Yupana",
    tawantsuyu = "Tawantsuyu",
    multiplyingWithYupana = "Multiplicando con la Yupana",
    ypPerfectMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1PERFECTO! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\n%d + %d = %d\n\u00a1Gran trabajo usando la Yupana!",
    ypCorrectMessage = "\u2705 \u00a1Correcto! Haz clic en 'Siguiente paso' para continuar.",
    ypAddTo = "Agrega a %s: %d + %d = %d. Coloca marcadores para %d en la Yupana.",
    ypAddToCarry = "Agrega a %s: %d + %d + %d (llevada) = %d. Coloca marcadores para %d en la Yupana.",
    ypCarrying = "Llevando 1: Agregar a %s da %d + %d = %d (> 10). Coloca marcadores para %d en la Yupana. La llevada se agregar\u00e1 a %s en el siguiente paso.",
    ypCarryingCarry = "Llevando 1: Agregar a %s da %d + %d + %d (llevada) = %d (> 10). Coloca marcadores para %d en la Yupana. La llevada se agregar\u00e1 a %s en el siguiente paso.",
    ypCongratsMessage = "\uD83C\uDF89\uD83C\uDF89\uD83C\uDF89 \u00a1FELICITACIONES! \uD83C\uDF89\uD83C\uDF89\uD83C\uDF89\nHas calculado %d + %d = %d\n\u00a1Haz clic en 'Nuevo ejercicio' para practicar m\u00e1s!",
    ypLastLevelMessage = "\uD83C\uDF89\uD83C\uDFC6 \u00a1TODOS LOS NIVELES COMPLETADOS! \uD83C\uDFC6\uD83C\uDF89\n\u00a1Dominaste los 9 niveles! Haz clic en \"Nuevo ejercicio\" para empezar de nuevo en el nivel 1.",
    ypRedPhase = "Coloca marcadores ROJOS para %d en %s.",
    ypBluePhase = "Coloca marcadores AZULES para %d en %s.",
    ypNothingLeft = "Nada que agregar de la izquierda en la columna %s.",
    ypNothingRight = "Nada que agregar de la derecha en la columna %s.",
    ypNothingResult = "Nada que colocar en la columna %s.",
    ypSumTen = "%d + %d + %d = %d → 0 en la columna %s, llevada %d.",
    ypCarry = "(llevada)",
    ypButtonInstruction = "Toca las columnas de la Yupana para colocar o quitar marcadores. Cuando la fila sea correcta, haz clic en \"Siguiente paso\". Usa \"Nuevo ejercicio\" para reiniciar o \"Siguiente nivel\" para avanzar.",
)

val LocalYupanaSharedStrings = staticCompositionLocalOf { EnYupanaSharedStrings }

fun yupanaSharedStringsForLanguage(language: String): YupanaSharedStrings = when (language) {
    "pt-BR" -> PtYupanaSharedStrings
    "es-ES" -> EsYupanaSharedStrings
    else -> EnYupanaSharedStrings
}
