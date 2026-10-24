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
    onSurface = TextoClaro,
    primaryContainer = VerdeContenedorOscuro,
    onPrimaryContainer = TextoClaro,
    secondaryContainer = VerdeContenedorOscuro,
    onSecondaryContainer = DoradoOscuro,
    surfaceVariant = SuperficieOscura,
    onSurfaceVariant = TextoClaroSecundario,
    outline = TextoClaroSecundario,
    outlineVariant = BordeOscuro
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
    onError = Blanco,
    // Sin estos colores Material usa sus morados por defecto en chips, interruptores y selecciones
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdePatrimonial,
    secondaryContainer = DoradoClaro,
    onSecondaryContainer = CafePatrimonial,
    tertiaryContainer = DoradoClaro,
    onTertiaryContainer = TextoPrincipal,
    surfaceVariant = VerdeMuyClaro,
    onSurfaceVariant = TextoSecundario,
    outline = TextoSecundario,
    outlineVariant = BordeSuave
)


/**
 * Tema Material 3 de la app. Elige el esquema de colores y se lo entrega a MaterialTheme,
 * que lo comparte con todos los componentes (botones, chips, campos de texto).
 * - darkTheme = false: por ahora la app siempre usa el tema claro.
 * - dynamicColor = false: no usa los colores del fondo de pantalla del teléfono (Android 12+),
 *   para mantener la paleta patrimonial.
 * - content es la interfaz que recibe el tema (lambda @Composable).
 */
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