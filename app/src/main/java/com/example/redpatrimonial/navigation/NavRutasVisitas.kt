package com.example.redpatrimonial.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.redpatrimonial.view.InicioScreen
import com.example.redpatrimonial.view.LoginScreen
import com.example.redpatrimonial.view.QrScannerScreen
import com.example.redpatrimonial.view.RutaDetalleScreen
import com.example.redpatrimonial.view.RutaPersonalizadaScreen
import com.example.redpatrimonial.view.RutasScreen
import com.example.redpatrimonial.view.admin.RutasOficialesScreen
import com.example.redpatrimonial.view.docente.MisSolicitudesScreen
import com.example.redpatrimonial.view.docente.PanelDocenteScreen
import com.example.redpatrimonial.view.docente.PlanificarSalidaScreen
import com.example.redpatrimonial.view.encargado.SolicitudesRecintoScreen
import com.example.redpatrimonial.view.guia.AgendaScreen
import com.example.redpatrimonial.view.guia.DisponibilidadScreen
import com.example.redpatrimonial.view.guia.PanelGuiaScreen
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * Registra las pantallas de acceso, rutas, visitas y QR (Persona B), incluidas Inicio y Login.
 * Funciona igual que exploracionGraph: un composable() por ruta y navArgument para los parámetros.
 * RUTAS y RUTAS_DE_RECINTO muestran la misma pantalla; la segunda pasa un idRecinto para filtrar.
 */
fun NavGraphBuilder.rutasVisitasGraph(navController: NavController, sesionViewModel: SesionViewModel) {

    composable(Routes.INICIO) {
        InicioScreen(navController)
    }
    composable(Routes.LOGIN) {
        LoginScreen(navController, sesionViewModel)
    }

    // Rutas y QR
    composable(Routes.RUTAS) {
        RutasScreen(navController)
    }
    composable(
        Routes.RUTAS_DE_RECINTO,
        arguments = listOf(navArgument("recintoId") { type = NavType.IntType })
    ) { entrada ->
        RutasScreen(navController, idRecinto = entrada.arguments?.getInt("recintoId"))
    }
    composable(
        Routes.RUTA_DETALLE,
        arguments = listOf(navArgument("rutaId") { type = NavType.IntType })
    ) { entrada ->
        RutaDetalleScreen(navController, entrada.arguments?.getInt("rutaId") ?: 0)
    }
    composable(Routes.RUTA_PERSONALIZADA) {
        RutaPersonalizadaScreen(navController, sesionViewModel)
    }
    composable(Routes.QR) {
        QrScannerScreen(navController)
    }

    // Docente
    composable(Routes.PANEL_DOCENTE) {
        PanelDocenteScreen(navController, sesionViewModel)
    }
    composable(Routes.PLANIFICAR_SALIDA) {
        PlanificarSalidaScreen(navController, sesionViewModel)
    }
    composable(Routes.MIS_SOLICITUDES) {
        MisSolicitudesScreen(navController, sesionViewModel)
    }

    // Guía voluntario
    composable(Routes.PANEL_GUIA) {
        PanelGuiaScreen(navController, sesionViewModel)
    }
    composable(Routes.DISPONIBILIDAD) {
        DisponibilidadScreen(navController, sesionViewModel)
    }
    composable(Routes.AGENDA) {
        AgendaScreen(navController, sesionViewModel)
    }

    // Coordinación de guías del encargado y rutas oficiales del administrador
    composable(Routes.SOLICITUDES_RECINTO) {
        SolicitudesRecintoScreen(navController, sesionViewModel)
    }
    composable(Routes.RUTAS_OFICIALES) {
        RutasOficialesScreen(navController, sesionViewModel)
    }
}
