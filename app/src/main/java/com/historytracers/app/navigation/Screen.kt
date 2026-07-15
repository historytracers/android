// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.navigation

sealed class Screen(val route: String) {
    data object Index : Screen("index")
    data object Content : Screen("content/{fileName}") {
        fun createRoute(fileName: String) = "content/$fileName"
    }
    data object Sources : Screen("sources")
    data object FirstSteps : Screen("first_steps")
    data object Workout : Screen("workout")
    data object Abacus : Screen("abacus")
    data object Settings : Screen("settings")
    data object About : Screen("about")
    data object IsItFree : Screen("is_it_free")
    data object Streak : Screen("streak")
    data object Clap : Screen("clap")
    data object FeetAndHands : Screen("feet_and_hands")
    data object Congratulation : Screen("congratulation")
    data object SorobanWriting : Screen("soroban_writing")
    data object SuanpanWriting : Screen("suanpan_writing")
    data object LargeNumbersWriting : Screen("large_numbers_writing")
    data object PracticingAddition : Screen("practicing_addition")
    data object MultiplicationTable : Screen("multiplication_table")
}
