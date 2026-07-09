package com.epn.expensetracker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SanaGreen,
    onPrimary = SanaBlueDark,
    primaryContainer = SanaGreenDark,
    onPrimaryContainer = SanaGreenLight,
    secondary = SanaBlue,
    onSecondary = White,
    background = DarkBackground,
    onBackground = OffWhite,
    surface = DarkSurface,
    onSurface = OffWhite,
    surfaceVariant = SanaBlueDark,
    onSurfaceVariant = TextSecondary
)

private val LightColorScheme = lightColorScheme(
    primary = SanaGreen,
    onPrimary = White,
    primaryContainer = SanaGreenLight,
    onPrimaryContainer = SanaBlue,
    secondary = SanaBlue,
    onSecondary = White,
    background = White,
    onBackground = TextPrimary,
    surface = OffWhite,
    onSurface = TextPrimary,
    surfaceVariant = SanaGreenLight,
    onSurfaceVariant = TextSecondary
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}