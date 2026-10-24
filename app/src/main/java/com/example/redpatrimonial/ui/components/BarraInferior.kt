package com.example.redpatrimonial.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.theme.DoradoClaro
import com.example.redpatrimonial.ui.theme.FondoTarjeta
import com.example.redpatrimonial.ui.theme.VerdePatrimonial

/**
 * Datos de una pestaña: a qué ruta lleva, su texto y su ícono.
 */
private data class ItemBarra(val ruta: String, val etiqueta: String, val icono: ImageVector)

private val items = listOf(
    ItemBarra(Routes.MAPA, "Mapa", Icons.Outlined.Map),
    ItemBarra(Routes.BUSCADOR, "Buscador", Icons.Outlined.Search),
    ItemBarra(Routes.QR, "QR", Icons.Outlined.QrCodeScanner),
    ItemBarra(Routes.EVENTOS, "Eventos", Icons.Outlined.CalendarMonth),
    ItemBarra(Routes.RUTAS, "Rutas", Icons.Outlined.Explore)
)

/**
 * Barra de pestañas inferior: Mapa, Buscador, QR, Eventos y Rutas.
 * selected marca la pestaña de la pantalla actual. Al tocar una pestaña:
 * - popUpTo(Routes.MAPA) { saveState = true }: vuelve el historial hasta el Mapa, para no apilar pantallas.
 * - launchSingleTop = true: si ya estás en esa pestaña, no la abre de nuevo.
 * - restoreState = true: recupera el estado que tenía la pestaña (por ejemplo, la posición del scroll).
 */
@Composable
fun BarraInferior(navController: NavController, rutaActual: String?) {
    NavigationBar(containerColor = FondoTarjeta) {
        items.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.ruta,
                onClick = {
                    // Cambia de pestaña sin apilar pantallas repetidas: el Mapa queda siempre en la base
                    navController.navigate(item.ruta) {
                        popUpTo(Routes.MAPA) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icono, contentDescription = item.etiqueta) },
                label = { Text(item.etiqueta) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = VerdePatrimonial,
                    selectedTextColor = VerdePatrimonial,
                    indicatorColor = DoradoClaro
                )
            )
        }
    }
}
