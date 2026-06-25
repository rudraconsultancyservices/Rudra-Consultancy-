package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryBlue,
    tertiary = AccentYellow,
    background = SlateBackground,
    surface = SlateCard,
    error = AlertRed,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = SlateText,
    onSurface = SlateText,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    secondary = SecondaryBlue,
    tertiary = AccentYellow,
    background = SoftCream,
    surface = PureWhite,
    error = AlertRed,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkText,
    onSurface = DarkText,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
