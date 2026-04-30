package com.example.aprxtiva.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object TemaManager {
    var oscuro by mutableStateOf(false)
}

private val LightColorScheme = lightColorScheme(
    primary = RojoAPR,
    onPrimary = BlancoAPR,
    secondary = DoradoAPR,
    onSecondary = BlancoAPR,
    background = GrisClaro,
    onBackground = GrisOscuro,
    surface = BlancoAPR,
    onSurface = GrisOscuro,
)

private val DarkColorScheme = darkColorScheme(
    primary = RojoAPROscuro,
    onPrimary = BlancoAPR,
    secondary = DoradoAPR,
    onSecondary = BlancoAPR,
    background = GrisOscuro,
    onBackground = BlancoAPR,
    surface = Color(0xFF3C3C3C),
    onSurface = BlancoAPR,
)

@Composable
fun APRXativaTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = if (TemaManager.oscuro) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}