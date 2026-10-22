package com.example.redpatrimonial.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VerdeOscuro,
    secondary = VerdeSalvia,
    tertiary = DoradoOscuro,
    background = FondoOscuro,
    surface = SuperficieOscura,
    onPrimary = Color(0xFF173629),
    onSecondary = Blanco,
    onTertiary = Color(0xFF382900),
    onBackground = TextoClaro,
    onSurface = TextoClaro
)

private val LightColorScheme = lightColorScheme(
    primary = VerdePatrimonial,
    secondary = VerdeSalvia,
    tertiary = DoradoAntiguo,
    background = FondoAplicacion,
    surface = FondoTarjeta,
    onPrimary = Blanco,
    onSecondary = Blanco,
    onTertiary = TextoPrincipal,
    onBackground = TextoPrincipal,
    onSurface = TextoPrincipal,
    error = RojoError,
    onError = Blanco
)


@Composable
fun RedPatrimonialTheme(
    darkTheme: Boolean = false,
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