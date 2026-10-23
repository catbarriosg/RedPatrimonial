package com.example.redpatrimonial.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.redpatrimonial.view.BuscadorScreen
import com.example.redpatrimonial.view.DetalleRecintoScreen
import com.example.redpatrimonial.view.EventosScreen
import com.example.redpatrimonial.view.HomeScreen
import com.example.redpatrimonial.view.InicioScreen
import com.example.redpatrimonial.view.LoginScreen
import com.example.redpatrimonial.view.QrScreen
import com.example.redpatrimonial.view.RutaPersonalizadaScreen
import com.example.redpatrimonial.view.RutasScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.INICIO
    ) {
        composable(Routes.INICIO) {
            InicioScreen(navController)
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        composable(Routes.MAPA) {
            HomeScreen(navController)
        }

        composable(Routes.BUSCADOR) {
            BuscadorScreen(navController)
        }

        composable(Routes.DETALLE_RECINTO) {
            DetalleRecintoScreen(navController)
        }

        composable(Routes.EVENTOS) {
            EventosScreen(navController)
        }

        composable(Routes.RUTAS) {
            RutasScreen(navController)
        }

        composable(Routes.RUTA_PERSONALIZADA) {
            RutaPersonalizadaScreen(navController)
        }

        composable(Routes.QR) {
            QrScreen(navController)
        }
    }
}
