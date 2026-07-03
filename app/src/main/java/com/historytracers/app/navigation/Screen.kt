// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.navigation

sealed class Screen(val route: String) {
    data object Index : Screen("index")
    data object Content : Screen("content/{fileName}") {
        fun createRoute(fileName: String) = "content/$fileName"
    }
    data object Sources : Screen("sources")
}
