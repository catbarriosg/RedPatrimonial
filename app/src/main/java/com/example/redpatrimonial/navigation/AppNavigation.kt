package com.example.redpatrimonial.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.redpatrimonial.ui.components.BarraInferior
import com.example.redpatrimonial.ui.theme.FondoAplicacion
import com.example.redpatrimonial.ui.theme.VerdePatrimonial
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * Contenedor de toda la navegación de la app.
 * - rememberNavController() crea el controlador que guarda el historial de pantallas (back stack)
 *   y sobrevive a las recomposiciones.
 * - SesionViewModel se crea aquí, fuera de las pantallas, para que viva a nivel de la Activity:
 *   así todas las pantallas comparten el mismo usuario con sesión.
 * - currentBackStackEntryAsState() entrega la pantalla actual como estado; cuando cambia,
 *   se recompone y decide si mostrar la barra inferior.
 * - Scaffold organiza la pantalla: bottomBar abajo y el contenido en el centro. El padding que entrega
 *   evita que el contenido quede debajo de la barra inferior o de la barra de estado.
 * - NavHost muestra la pantalla de la ruta actual. Empieza en INICIO y registra los dos grafos:
 *   exploracionGraph (Persona A) y rutasVisitasGraph (Persona B).
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // La sesión vive a nivel de la Activity para que todas las pantallas vean al mismo usuario
    val sesionViewModel: SesionViewModel = viewModel()

    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    Scaffold(
        // La pantalla de inicio es verde de borde a borde; el resto usa el fondo marfil
        containerColor = if (rutaActual == Routes.INICIO) VerdePatrimonial else FondoAplicacion,
        bottomBar = {
            if (rutaActual in Routes.conBarraInferior) {
                BarraInferior(navController, rutaActual)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.INICIO,
            modifier = Modifier.padding(padding)
        ) {
            exploracionGraph(navController, sesionViewModel)
            rutasVisitasGraph(navController, sesionViewModel)
        }
    }
}

/**
 * Cierra la sesión y vuelve al mapa.
 * popUpTo(navController.graph.id) { inclusive = true } borra todo el historial, para que el botón
 * "atrás" no pueda volver a una pantalla privada después de cerrar sesión.
 */
fun cerrarSesion(navController: NavController, sesionViewModel: SesionViewModel) {
    sesionViewModel.cerrar()
    navController.navigate(Routes.MAPA) {
        popUpTo(navController.graph.id) { inclusive = true }
    }
}
