package com.example.redpatrimonial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.redpatrimonial.navigation.AppNavigation
import com.example.redpatrimonial.ui.theme.RedPatrimonialTheme

/**
 * Única Activity de la app. Con Jetpack Compose no se necesita una Activity por pantalla:
 * todas las pantallas son funciones @Composable y la navegación las cambia dentro de esta Activity.
 */
class MainActivity : ComponentActivity() {
    /**
     * Punto de entrada de la app.
     * - enableEdgeToEdge(): la app dibuja detrás de la barra de estado y de la barra de gestos.
     * - setContent { }: define la interfaz con Compose.
     * - RedPatrimonialTheme aplica los colores y tipografía del proyecto a todo lo que está adentro.
     * - AppNavigation() contiene el NavHost con todas las pantallas.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RedPatrimonialTheme {
                AppNavigation()
            }
        }
    }
}
