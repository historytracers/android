// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.features

import androidx.compose.runtime.staticCompositionLocalOf

data class AdvancingAndComplementingScreenStrings(
    val introTitle: String,
    val introBody: String,
    val complementTitle: String,
    val complementBody: String,
    val ourselvesTitle: String,
    val ourselvesBody: String,
    val calculationCaption: String,
    val limitTitle: String,
    val limitBody: String,
    val figureCaption: String,
    val exercisingTitle: String,
    val exercisingBody: String,
    val exercisingInstruction: String,
    val msgBelow: String,
    val msgAt: String,
    val msgAbove: String,
    val thinkingTitle: String,
    val thinkingQuestion: String,
    val thinkingCorrect: String,
    val thinkingWrong: String,
    val conclusionTitle: String,
    val conclusionBody: String,
)

val EnAdvancingAndComplementingScreenStrings = AdvancingAndComplementingScreenStrings(
    introTitle = "Introduction",
    introBody = "In previous classes, we learned that we cannot write a number greater than 9 in a single position. When this happens, we need to use a new order — a new family number — to hold the number.\n\nIn this class, we will look at the same idea from another angle: through the complement and the limits of a position.",
    complementTitle = "Complement of 10",
    complementBody = "When we do math with two numbers, before taking any action it is a good idea to evaluate what we can do with them, so that we do not exceed the limits.\n\nSince we cannot write a number greater than 9 in a position, we look at the complement of the number to reach 10. In other words, we check which number we can add to a number without going past the value 10.",
    ourselvesTitle = "Ourselves",
    ourselvesBody = "Let us start with us. Our hands have 5 fingers. If we add 4 more fingers, we get 9 — we still do not reach 10. But if we add 5 or more, the result reaches or passes the limit of the position, as you can see in Calculation 1:",
    calculationCaption = "Calculation 1: Adding 5 to 5, 6, 7, 8 and 9. In every case the result is 10 or more, so a new order is needed.",
    limitTitle = "My Complement, My Limit",
    limitBody = "The complement of a number to 10 is the number needed to reach 10. For example, if we have 6 and we want to reach 10, we add its complement, 4, because 6 + 4 = 10.\n\nLet us draw this to make it even clearer:",
    figureCaption = "Figure 1: The number 6 (blue) and its complement 4 (yellow). Together they fill the ten squares.",
    exercisingTitle = "Exercising",
    exercisingBody = "To confirm what we learned, use the arrows below to check what happens when we add a value smaller than, equal to, or greater than the complement.",
    exercisingInstruction = "Use the arrows to change the number added to 6 and watch when the result advances to the next order.",
    msgBelow = "You are below the complement of 10.",
    msgAt = "You reached the complement of 10.",
    msgAbove = "You are above the complement.",
    thinkingTitle = "Let Us Think!",
    thinkingQuestion = "If a value is smaller than the complement of 10, are we going to carry a number?",
    thinkingCorrect = "Exactly! If we add a value smaller than the complement, the result stays below 10, so nothing moves to the next order — we do not carry.",
    thinkingWrong = "Not yet. If the value is smaller than the complement, the sum stays below 10, so the position does not carry.",
    conclusionTitle = "Conclusion",
    conclusionBody = "In this class, we learned that before adding another number, we should check the complement of the first number to reach 10.\n\nIf we add a smaller value, we will never carry. But if we add a value equal to or greater than the complement, we will carry a number.\n\nIn the next class, we will practice everything we learned to reinforce our knowledge.",
)

val PtAdvancingAndComplementingScreenStrings = AdvancingAndComplementingScreenStrings(
    introTitle = "Introdução",
    introBody = "Nas aulas anteriores, aprendemos que não podemos escrever um número maior que 9 em uma única posição. Quando isso acontece, precisamos usar uma nova ordem — um novo número da família — para guardar o número.\n\nNesta aula, vamos olhar para essa mesma ideia por outro ângulo: por meio do complemento e dos limites de uma posição.",
    complementTitle = "Complemento de 10",
    complementBody = "Quando fazemos contas com dois números, antes de tomar qualquer atitude é uma boa ideia avaliar o que podemos fazer com eles, para não ultrapassar os limites.\n\nComo não podemos escrever um número maior que 9 em uma posição, olhamos para o complemento do número até 10. Em outras palavras, verificamos qual número podemos somar a outro sem passar do valor 10.",
    ourselvesTitle = "Nós mesmos",
    ourselvesBody = "Vamos começar por nós. Nossas mãos têm 5 dedos. Se somarmos mais 4 dedos, chegamos a 9 — ainda não alcançamos 10. Mas se somarmos 5 ou mais, o resultado alcança ou passa o limite da posição, como você pode ver no Cálculo 1:",
    calculationCaption = "Cálculo 1: Somar 5 a 5, 6, 7, 8 e 9. Em todos os casos o resultado é 10 ou mais, por isso precisamos de uma nova ordem.",
    limitTitle = "Meu complemento, meu limite",
    limitBody = "O complemento de um número até 10 é o número necessário para chegar a 10. Por exemplo, se temos 6 e queremos chegar a 10, somamos o seu complemento, 4, porque 6 + 4 = 10.\n\nVamos desenhar isso para ficar ainda mais claro:",
    figureCaption = "Figura 1: O número 6 (azul) e o seu complemento 4 (amarelo). Juntos, eles completam os dez quadrados.",
    exercisingTitle = "Praticando",
    exercisingBody = "Para confirmar o que aprendemos, use as setas abaixo para verificar o que acontece quando somamos um valor menor, igual ou maior que o complemento.",
    exercisingInstruction = "Use as setas para mudar o número somado a 6 e observe quando o resultado avança para a próxima ordem.",
    msgBelow = "Você está abaixo do complemento de 10.",
    msgAt = "Você alcançou o complemento de 10.",
    msgAbove = "Você está acima do complemento.",
    thinkingTitle = "Vamos Pensar!",
    thinkingQuestion = "Se um valor é menor que o complemento de 10, vamos levar um número?",
    thinkingCorrect = "Exatamente! Se somarmos um valor menor que o complemento, o resultado fica abaixo de 10, então nada passa para a próxima ordem — não levamos nada.",
    thinkingWrong = "Ainda não. Se o valor é menor que o complemento, a soma fica abaixo de 10, então a posição não leva nada.",
    conclusionTitle = "Conclusão",
    conclusionBody = "Nesta aula, aprendemos que, antes de somar outro número, devemos verificar o complemento do primeiro número até 10.\n\nSe somarmos um valor menor, nunca vamos levar. Mas se somarmos um valor igual ou maior que o complemento, vamos levar um número.\n\nNa próxima aula, vamos praticar tudo o que aprendemos para reforçar o nosso conhecimento.",
)

val EsAdvancingAndComplementingScreenStrings = AdvancingAndComplementingScreenStrings(
    introTitle = "Introducción",
    introBody = "En las clases anteriores, aprendimos que no podemos escribir un número mayor que 9 en una sola posición. Cuando esto ocurre, necesitamos usar un nuevo orden — un nuevo número de familia — para guardar el número.\n\nEn esta clase, veremos esa misma idea desde otro ángulo: a través del complemento y de los límites de una posición.",
    complementTitle = "Complemento de 10",
    complementBody = "Cuando hacemos cuentas con dos números, antes de tomar cualquier acción es buena idea evaluar qué podemos hacer con ellos, para no superar los límites.\n\nComo no podemos escribir un número mayor que 9 en una posición, miramos el complemento del número hasta 10. En otras palabras, verificamos qué número podemos sumar a otro sin pasar del valor 10.",
    ourselvesTitle = "Nosotros mismos",
    ourselvesBody = "Empecemos por nosotros. Nuestras manos tienen 5 dedos. Si sumamos 4 dedos más, llegamos a 9 — todavía no alcanzamos 10. Pero si sumamos 5 o más, el resultado alcanza o supera el límite de la posición, como puedes ver en el Cálculo 1:",
    calculationCaption = "Cálculo 1: Sumar 5 a 5, 6, 7, 8 y 9. En todos los casos el resultado es 10 o más, por eso se necesita un nuevo orden.",
    limitTitle = "Mi complemento, mi límite",
    limitBody = "El complemento de un número hasta 10 es el número necesario para alcanzar 10. Por ejemplo, si tenemos 6 y queremos llegar a 10, sumamos su complemento, 4, porque 6 + 4 = 10.\n\nVamos a dibujarlo para que quede aún más claro:",
    figureCaption = "Figura 1: El número 6 (azul) y su complemento 4 (amarillo). Juntos completan los diez cuadrados.",
    exercisingTitle = "Practicando",
    exercisingBody = "Para confirmar lo que aprendimos, usa las flechas de abajo para comprobar qué ocurre cuando sumamos un valor menor, igual o mayor que el complemento.",
    exercisingInstruction = "Usa las flechas para cambiar el número que se suma a 6 y observa cuándo el resultado avanza al siguiente orden.",
    msgBelow = "Estás por debajo del complemento de 10.",
    msgAt = "Alcanzaste el complemento de 10.",
    msgAbove = "Estás por encima del complemento.",
    thinkingTitle = "¡Pensemos!",
    thinkingQuestion = "Si un valor es menor que el complemento de 10, ¿vamos a llevar un número?",
    thinkingCorrect = "¡Exacto! Si sumamos un valor menor que el complemento, el resultado se queda por debajo de 10, así que nada pasa al siguiente orden — no llevamos nada.",
    thinkingWrong = "Todavía no. Si el valor es menor que el complemento, la suma se queda por debajo de 10, así que la posición no lleva nada.",
    conclusionTitle = "Conclusión",
    conclusionBody = "En esta clase aprendimos que, antes de sumar otro número, debemos comprobar el complemento del primer número hasta 10.\n\nSi sumamos un valor menor, nunca vamos a llevar. Pero si sumamos un valor igual o mayor que el complemento, vamos a llevar un número.\n\nEn la próxima clase, practicaremos todo lo que aprendimos para reforzar nuestro conocimiento.",
)

val LocalAdvancingAndComplementingScreenStrings = staticCompositionLocalOf { EnAdvancingAndComplementingScreenStrings }

fun advancingAndComplementingScreenStringsForLanguage(language: String): AdvancingAndComplementingScreenStrings = when (language) {
    "pt-BR" -> PtAdvancingAndComplementingScreenStrings
    "es-ES" -> EsAdvancingAndComplementingScreenStrings
    else -> EnAdvancingAndComplementingScreenStrings
}
