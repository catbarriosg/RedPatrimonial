package com.example.redpatrimonial.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.redpatrimonial.view.BuscadorScreen
import com.example.redpatrimonial.view.DetalleEventoScreen
import com.example.redpatrimonial.view.DetalleRecintoScreen
import com.example.redpatrimonial.view.EventosScreen
import com.example.redpatrimonial.view.FavoritosScreen
import com.example.redpatrimonial.view.HomeScreen
import com.example.redpatrimonial.view.admin.ModerarScreen
import com.example.redpatrimonial.view.admin.PanelAdminScreen
import com.example.redpatrimonial.view.admin.RegistrarNodoScreen
import com.example.redpatrimonial.view.encargado.CambiarEstadoScreen
import com.example.redpatrimonial.view.encargado.EditarFichaScreen
import com.example.redpatrimonial.view.encargado.PanelEncargadoScreen
import com.example.redpatrimonial.view.encargado.PublicarEventoScreen
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * Registra las pantallas de exploración y gestión del recinto (Persona A).
 * Es una función de extensión de NavGraphBuilder: se llama dentro del NavHost como si fuera propia.
 * Separar la navegación en dos archivos evita que las dos personas editen el mismo archivo.
 *
 * Cada composable(ruta) { } asocia una ruta con su pantalla.
 * En las rutas con argumentos, navArgument declara el nombre y el tipo (IntType), y la pantalla lo lee
 * con entrada.arguments?.getInt("recintoId"). El "?: 0" da un valor por defecto si no llegara.
 */
fun NavGraphBuilder.exploracionGraph(navController: NavController, sesionViewModel: SesionViewModel) {

    composable(Routes.MAPA) {
        HomeScreen(navController, sesionViewModel)
    }
    composable(Routes.BUSCADOR) {
        BuscadorScreen(navController)
    }
    composable(
        Routes.DETALLE_RECINTO,
        arguments = listOf(navArgument("recintoId") { type = NavType.IntType })
    ) { entrada ->
        DetalleRecintoScreen(navController, entrada.arguments?.getInt("recintoId") ?: 0)
    }
    composable(Routes.EVENTOS) {
        EventosScreen(navController)
    }
    composable(
        Routes.DETALLE_EVENTO,
        arguments = listOf(navArgument("eventoId") { type = NavType.IntType })
    ) { entrada ->
        DetalleEventoScreen(navController, entrada.arguments?.getInt("eventoId") ?: 0)
    }
    composable(Routes.FAVORITOS) {
        FavoritosScreen(navController)
    }

    // Encargado de templo
    composable(Routes.PANEL_ENCARGADO) {
        PanelEncargadoScreen(navController, sesionViewModel)
    }
    composable(Routes.EDITAR_FICHA) {
        EditarFichaScreen(navController, sesionViewModel)
    }
    composable(Routes.CAMBIAR_ESTADO) {
        CambiarEstadoScreen(navController, sesionViewModel)
    }
    composable(Routes.PUBLICAR_EVENTO) {
        PublicarEventoScreen(navController, sesionViewModel)
    }

    // Administrador de red
    composable(Routes.PANEL_ADMIN) {
        PanelAdminScreen(navController, sesionViewModel)
    }
    composable(Routes.REGISTRAR_NODO) {
        RegistrarNodoScreen(navController)
    }
    composable(Routes.MODERAR) {
        ModerarScreen(navController)
    }
}
