
package com.example.redpatrimonial.view

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.redpatrimonial.navigation.Routes

@Composable
fun BarraNavegacion(navController: NavController) {
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    NavigationBar {
        NavigationBarItem(
            selected = rutaActual == Routes.MAPA,
            onClick = {
                navController.navigate(Routes.MAPA) {
                    launchSingleTop = true
                }
            },
            icon = { Text("⌂") },
            label = { Text("Mapa") }
        )

        NavigationBarItem(
            selected = rutaActual == Routes.BUSCADOR,
            onClick = {
                navController.navigate(Routes.BUSCADOR) {
                    launchSingleTop = true
                }
            },
            icon = { Text("⌕") },
            label = { Text("Buscar") }
        )

        NavigationBarItem(
            selected = rutaActual == Routes.QR,
            onClick = {
                navController.navigate(Routes.QR) {
                    launchSingleTop = true
                }
            },
            icon = { Text("▦") },
            label = { Text("QR") }
        )

        NavigationBarItem(
            selected = rutaActual == Routes.EVENTOS,
            onClick = {
                navController.navigate(Routes.EVENTOS) {
                    launchSingleTop = true
                }
            },
            icon = { Text("▣") },
            label = { Text("Eventos") }
        )

        NavigationBarItem(
            selected = rutaActual == Routes.RUTAS,
            onClick = {
                navController.navigate(Routes.RUTAS) {
                    launchSingleTop = true
                }
            },
            icon = { Text("➤") },
            label = { Text("Rutas") }
        )
    }
}
