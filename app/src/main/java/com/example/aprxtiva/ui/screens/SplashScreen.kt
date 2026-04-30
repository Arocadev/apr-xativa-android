package com.example.aprxtiva.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFC0392B)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "APR",
                color = Color.White,
                fontSize = 100.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 100.sp
            )
            Text(
                text = "Xàtiva",
                color = Color(0xFFC9A84C),
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 40.sp
            )
            Text(
                text = "Nucli Antic",
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp
            )
        }
    }
}