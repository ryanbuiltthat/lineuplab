package com.lineuplab.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PitchGreenLight,
    onPrimary = LineWhite,
    secondary = AccentAmber,
    tertiary = BadgeBlue,
)

private val LightColorScheme = lightColorScheme(
    primary = PitchGreen,
    onPrimary = LineWhite,
    secondary = AccentAmber,
    tertiary = BadgeBlue,
)

@Composable
fun LineupLabTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        content = content,
    )
}
