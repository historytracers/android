// SPDX-License-Identifier: GPL-3.0-or-later
package com.historytracers.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.historytracers.app.ui.LocalAppLanguage
import com.historytracers.app.ui.features.splashScreenStringsForLanguage
import com.historytracers.app.ui.theme.Brown40

@Composable
fun SplashScreen() {
    val xs = splashScreenStringsForLanguage(LocalAppLanguage.current)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(Brown40, RoundedCornerShape(28.dp))
                .padding(horizontal = 32.dp, vertical = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = xs.title,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = xs.subtitle,
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
