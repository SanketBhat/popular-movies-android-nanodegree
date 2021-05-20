package com.udacity.sanketbhat.popularmovies.ui.views

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorPalette = darkColors(
    primary = red200,
    primaryVariant = red500,
    secondary = blue100,
    secondaryVariant = blue300,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

val LightColorPalette = lightColors(
    primary = red700,
    primaryVariant = red900,
    secondary = blue500,
    secondaryVariant = blue700,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black
)

@Composable
fun PopMoviesTheme(isDarkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (isDarkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(colors = colors, content = content)
}