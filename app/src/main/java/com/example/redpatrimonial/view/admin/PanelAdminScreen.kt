package com.example.redpatrimonial.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AddLocationAlt
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.navigation.cerrarSesion
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.ui.theme.FondoTarjeta
import com.example.redpatrimonial.ui.theme.TextoSecundario
import com.example.redpatrimonial.ui.theme.VerdePatrimonial
import com.example.redpatrimonial.viewmodel.AdminViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * Panel del administrador: indicadores de la red y accesos a registrar nodo, moderar y rutas oficiales.
 */
@Composable
fun PanelAdminScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    adminViewModel: AdminViewModel = viewModel()
) {
    val usuario = sesionViewModel.usuario
    if (usuario == null) {
        SinSesion { navController.navigate(Routes.LOGIN) }
        return
    }
    val recintos by adminViewModel.recintos.collectAsState()
    val eventos by adminViewModel.eventos.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Administración de la red",
            subtitulo = usuario.nombre,
            acciones = {
                IconButton(onClick = { cerrarSesion(navController, sesionViewModel) }) {
                    Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Cerrar sesión", tint = VerdePatrimonial)
                }
            }
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Indicador("Recintos en la red", recintos.size.toString(), Modifier.weight(1f))
                Indicador("Eventos ocultos", eventos.count { !it.visible }.toString(), Modifier.weight(1f))
            }

            TituloSeccion("Acciones")
            TarjetaAccion("Registrar nodo", "Templo, museo, colegio o archivo", Icons.Outlined.AddLocationAlt) {
                navController.navigate(Routes.REGISTRAR_NODO)
            }
            TarjetaAccion("Moderar publicaciones", "Ocultar o mostrar eventos y avisos", Icons.Outlined.Shield) {
                navController.navigate(Routes.MODERAR)
            }
            TarjetaAccion("Rutas oficiales", "Crear y eliminar rutas patrimoniales", Icons.Outlined.Explore) {
                navController.navigate(Routes.RUTAS_OFICIALES)
            }
        }
    }
}

/**
 * Tarjeta con un número grande y su descripción.
 */
@Composable
private fun Indicador(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = FondoTarjeta)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(valor, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = VerdePatrimonial)
            Text(titulo, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
        }
    }
}
