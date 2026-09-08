package com.kernel94.pulsoti.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PulsoRedLight,
    onPrimary = Color.White,
    secondary = PulsoGoldLight,
    onSecondary = Color(0xFF3A2600),
    tertiary = PulsoGold,
    background = PulsoSurfaceDark,
    surface = PulsoSurfaceDark,
    onBackground = Color(0xFFF5EAEA),
    onSurface = Color(0xFFF5EAEA)
)

private val LightColorScheme = lightColorScheme(
    primary = PulsoRed,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDAD8),
    onPrimaryContainer = PulsoRedDark,
    secondary = PulsoGold,
    onSecondary = Color(0xFF3A2600),
    secondaryContainer = Color(0xFFFFE7B3),
    onSecondaryContainer = PulsoGoldDark,
    tertiary = PulsoGoldDark,
    background = PulsoSurfaceLight,
    surface = Color.White,
    onBackground = Color(0xFF211919),
    onSurface = Color(0xFF211919)
)

@Composable
fun PulsoTiTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color se mantiene desactivado para respetar siempre la marca Pulso TI
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
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            // targetSdk 36 fuerza edge-to-edge: window.statusBarColor ya es
            // no-op. La barra queda transparente sobre el contenido; aqui
            // solo se ajusta el contraste de los iconos segun el tema.
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
