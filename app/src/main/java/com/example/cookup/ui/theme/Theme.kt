package com.example.cookup.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Define a bright yellow and white theme
private val DarkColorPalette = darkColors(
    primary = Color(0xFFFFCF50),
    primaryVariant = Color(0xFFFFCF50),
    secondary = Color(0XFF626F47),
    background = Color.White,
    surface = Color(0xFFFEFAE0),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val LightColorPalette = lightColors(
    primary = Color(0xFFFFCF50),
    primaryVariant = Color(0xFFFFCF50),
    secondary = Color(0XFF626F47),
    background = Color.White,
    surface = Color(0xFFFEFAE0),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun CookUpTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        typography = Typography, // Ensure Typography is defined in Typography.kt
        shapes = Shapes, // Ensure Shapes is defined in Shapes.kt
        content = content
    )
}
