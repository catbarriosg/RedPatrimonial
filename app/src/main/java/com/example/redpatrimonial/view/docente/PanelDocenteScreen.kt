package com.example.redpatrimonial.view.docente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.outlined.Map
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
import com.example.redpatrimonial.viewmodel.DocenteViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * Panel del docente: accesos a planificar salida, ver solicitudes y explorar el mapa,
 * más la lista de sus salidas planificadas con el nombre de la ruta de cada una.
 */
@Composable
fun PanelDocenteScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    docenteViewModel: DocenteViewModel = viewModel()
) {
    val usuario = sesionViewModel.usuario
    if (usuario == null) {
        SinSesion { navController.navigate(Routes.LOGIN) }
        return
    }
    LaunchedEffect(usuario.id) {
        docenteViewModel.cargar(usuario.id)
    }
    val rutas by docenteViewModel.rutas.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Panel docente",
            subtitulo = "${usuario.nombre} · ${usuario.institucion}",
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
            TarjetaAccion("Planificar salida pedagógica", "Elige una ruta, la fecha y el grupo", Icons.AutoMirrored.Outlined.EventNote) {
                navController.navigate(Routes.PLANIFICAR_SALIDA)
            }
            TarjetaAccion("Mis solicitudes de guía", "Revisa si ya tienen guía asignado", Icons.AutoMirrored.Outlined.Assignment) {
                navController.navigate(Routes.MIS_SOLICITUDES)
            }
            TarjetaAccion("Explorar el mapa", "Descubre recintos para tus clases", Icons.Outlined.Map) {
                navController.navigate(Routes.MAPA)
            }

            TituloSeccion("Mis salidas planificadas")
            if (docenteViewModel.salidas.isEmpty()) {
                MensajeVacio("Aún no planificas salidas.")
            }
            docenteViewModel.salidas.forEach { salida ->
                val nombreRuta = rutas.find { it.id == salida.idRuta }?.nombre ?: "Ruta #${salida.idRuta}"
                Card(colors = CardDefaults.cardColors(containerColor = FondoTarjeta), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(nombreRuta, fontWeight = FontWeight.SemiBold)
                        Text(
                            "${salida.fecha} · ${salida.hora} · ${salida.tamanoGrupo} estudiantes de ${salida.nivelEducativo}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                        if (salida.observaciones.isNotBlank()) {
                            Text(salida.observaciones, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
