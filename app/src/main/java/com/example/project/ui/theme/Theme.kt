package com.example.project.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = OnGreenContainer10,
    primaryContainer = GreenContainer30,
    onPrimaryContainer = GreenContainer90,
    secondary = Sand80,
    onSecondary = OnSandContainer10,
    secondaryContainer = SandContainer30,
    onSecondaryContainer = SandContainer90,
    tertiary = Clay80,
    tertiaryContainer = ClayContainer30,
    onTertiaryContainer = ClayContainer90,
    background = Neutral6,
    onBackground = Neutral90,
    surface = Neutral6,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    surfaceContainerLow = NeutralVariant20,
    surfaceContainerHigh = SurfaceHigh17,
    surfaceContainerHighest = SurfaceHighest22,
)

private val LightColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = GreenContainer90,
    onPrimaryContainer = OnGreenContainer10,
    secondary = Sand40,
    onSecondary = Color.White,
    secondaryContainer = SandContainer90,
    onSecondaryContainer = OnSandContainer10,
    tertiary = Clay40,
    tertiaryContainer = ClayContainer90,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    surfaceContainerLow = NeutralVariant90,
    surfaceContainerHigh = SurfaceHigh95,
    surfaceContainerHighest = SurfaceHighest92,
)

@Composable
fun ProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
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
        content = content
    )
}
