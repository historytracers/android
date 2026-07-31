// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class MovingInYupanaScreenStrings(
    val iskayTitle: String,
    val iskayDescription: String,
    val iskayEquation: String,
    val iskayMethodNote: String,
    val kimsaTitle: String,
    val kimsaDescription: String,
    val kimsaEquation: String,
    val kimsaMethodNote: String,
    val pisqaTitle: String,
    val pisqaDescription: String,
    val pisqaEquation: String,
    val pichanaTitle: String,
    val pichanaDescription: String,
    val pichanaEquation12: String,
    val pichanaEquation23: String,
    val kinkinTitle: String,
    val kinkinDescription: String,
    val kinkinEquation: String,
    val instructions: String,
    val leftHand: String,
    val rightHand: String,
)

val EnMovingInYupanaScreenStrings = MovingInYupanaScreenStrings(
    iskayTitle = "ISKAY",
    iskayDescription = "ISKAY, meaning \"two\" in Quechua, occurs when two circles occupy the square labeled with the number 2. In this movement, one circle is shifted to the square with a value of 1, and the other is moved to the square with a value of 3.",
    iskayEquation = "2 + 2 = 1 + 3",
    iskayMethodNote = "This transformation is also known as the \"short open\" method because the circles are moved to adjacent squares.",
    kimsaTitle = "KIMSA",
    kimsaDescription = "KIMSA, meaning \"three\" in Quechua, occurs when two circles occupy the square labeled with the number 3. In this case, one circle is moved to the square with the number 1, while the other is placed in the square with the number 5.",
    kimsaEquation = "3 + 3 = 1 + 5",
    kimsaMethodNote = "This transformation is known as the \"large open\" method because the circles are moved to squares at the line's extremes.",
    pisqaTitle = "PISQA",
    pisqaDescription = "PISQA, meaning \"five\" in Quechua, occurs when two circles are stacked in the square labeled with the number 5. One circle is moved to the top line in the square labeled with the number 1, while the other is removed.",
    pisqaEquation = "5 + 5 = 10 + 0",
    pichanaTitle = "PICHANA",
    pichanaDescription = "PICHANA, meaning \"broom\" in Quechua, occurs when two circles occupy squares where their sum equals the value of another square.",
    pichanaEquation12 = "1 + 2 = 3 + 0",
    pichanaEquation23 = "2 + 3 = 5 + 0",
    kinkinTitle = "KINKIN",
    kinkinDescription = "KINKIN, the \"equivalence movement,\" happens when multiple circles are placed in the first square, and their sum equals another square in the same line.",
    kinkinEquation = "1 + 1 = 2 + 0",
    instructions = "Use the green arrow to move the circles and update the fingers, and the blue arrow to return to the original position.",
    leftHand = "Left hand",
    rightHand = "Right hand",
)

val PtMovingInYupanaScreenStrings = MovingInYupanaScreenStrings(
    iskayTitle = "ISKAY",
    iskayDescription = "ISKAY, em Quechua, significa \"dois\". Esse movimento ocorre quando dois c\u00edrculos ocupam a posi\u00e7\u00e3o n\u00famero 2. Movemos um deles para o n\u00famero 1 e o outro para o n\u00famero 3.",
    iskayEquation = "2 + 2 = 1 + 3",
    iskayMethodNote = "Essa transforma\u00e7\u00e3o tamb\u00e9m \u00e9 conhecida como m\u00e9todo \"abrir curto\", pois deslocamos os c\u00edrculos para os quadrados adjacentes.",
    kimsaTitle = "KIMSA",
    kimsaDescription = "KIMSA, em Quechua, significa \"tr\u00eas\". Esse movimento ocorre quando dois c\u00edrculos ocupam a posi\u00e7\u00e3o n\u00famero 3. Movemos um deles para o n\u00famero 1 e o outro para o n\u00famero 5.",
    kimsaEquation = "3 + 3 = 1 + 5",
    kimsaMethodNote = "Essa transforma\u00e7\u00e3o \u00e9 conhecida como m\u00e9todo \"abrir largo\", pois os c\u00edrculos s\u00e3o deslocados para os extremos da linha.",
    pisqaTitle = "PISQA",
    pisqaDescription = "PISQA, em Quechua, significa \"cinco\". Esse movimento ocorre quando dois c\u00edrculos ocupam a posi\u00e7\u00e3o n\u00famero 5. Movemos um deles para o n\u00famero 1 na linha superior e removemos o outro da linha atual.",
    pisqaEquation = "5 + 5 = 10 + 0",
    pichanaTitle = "PICHANA",
    pichanaDescription = "PICHANA, em Quechua, significa \"vassoura\". Esse movimento ocorre quando c\u00edrculos est\u00e3o posicionados em dois quadrantes cuja soma equivale ao valor de outro quadrante.",
    pichanaEquation12 = "1 + 2 = 3 + 0",
    pichanaEquation23 = "2 + 3 = 5 + 0",
    kinkinTitle = "KINKIN",
    kinkinDescription = "KINKIN \u00e9 o movimento dos equivalentes. Ele ocorre quando h\u00e1 mais de um c\u00edrculo posicionado no primeiro quadrante e a soma deles equivale a outro quadrante existente.",
    kinkinEquation = "1 + 1 = 2 + 0",
    instructions = "Use a seta verde para mover os c\u00edrculos e atualizar os dedos das m\u00e3os, e a seta azul para retornar \u00e0 posi\u00e7\u00e3o original.",
    leftHand = "M\u00e3o esquerda",
    rightHand = "M\u00e3o direita",
)

val EsMovingInYupanaScreenStrings = MovingInYupanaScreenStrings(
    iskayTitle = "ISKAY",
    iskayDescription = "ISKAY, en Quechua, significa \"dos\". Este movimiento ocurre cuando dos c\u00edrculos ocupan la posici\u00f3n n\u00famero 2. Movemos uno de ellos al n\u00famero 1 y el otro al n\u00famero 3.",
    iskayEquation = "2 + 2 = 1 + 3",
    iskayMethodNote = "Esta transformaci\u00f3n tambi\u00e9n es conocida como m\u00e9todo \"abrir corto\", ya que desplazamos los c\u00edrculos a los cuadrados adyacentes.",
    kimsaTitle = "KIMSA",
    kimsaDescription = "KIMSA, en Quechua, significa \"tres\". Este movimiento ocurre cuando dos c\u00edrculos ocupan la posici\u00f3n n\u00famero 3. Movemos uno de ellos al n\u00famero 1 y el otro al n\u00famero 5.",
    kimsaEquation = "3 + 3 = 1 + 5",
    kimsaMethodNote = "Esta transformaci\u00f3n es conocida como m\u00e9todo \"abrir largo\", pues los c\u00edrculos se desplazan hacia los extremos de la l\u00ednea.",
    pisqaTitle = "PISQA",
    pisqaDescription = "PISQA, en Quechua, significa \"cinco\". Este movimiento ocurre cuando dos c\u00edrculos ocupan la posici\u00f3n n\u00famero 5. Movemos uno de ellos al n\u00famero 1 en la l\u00ednea superior y eliminamos el otro de la l\u00ednea actual.",
    pisqaEquation = "5 + 5 = 10 + 0",
    pichanaTitle = "PICHANA",
    pichanaDescription = "PICHANA, en Quechua, significa \"escoba\". Este movimiento ocurre cuando los c\u00edrculos est\u00e1n posicionados en dos cuadrantes cuya suma equivale al valor de otro cuadrante.",
    pichanaEquation12 = "1 + 2 = 3 + 0",
    pichanaEquation23 = "2 + 3 = 5 + 0",
    kinkinTitle = "KINKIN",
    kinkinDescription = "KINKIN es el movimiento de los equivalentes. Ocurre cuando hay m\u00e1s de un c\u00edrculo posicionado en el primer cuadrante y su suma equivale a otro cuadrante existente.",
    kinkinEquation = "1 + 1 = 2 + 0",
    instructions = "Usa la flecha verde para mover los c\u00edrculos y actualizar los dedos de las manos, y la flecha azul para volver a la posici\u00f3n original.",
    leftHand = "Mano izquierda",
    rightHand = "Mano derecha",
)

val LocalMovingInYupanaScreenStrings = staticCompositionLocalOf { EnMovingInYupanaScreenStrings }

fun movingInYupanaScreenStringsForLanguage(language: String): MovingInYupanaScreenStrings = when (language) {
    "pt-BR" -> PtMovingInYupanaScreenStrings
    "es-ES" -> EsMovingInYupanaScreenStrings
    else -> EnMovingInYupanaScreenStrings
}
