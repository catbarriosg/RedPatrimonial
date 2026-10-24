package com.example.redpatrimonial.view.docente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.model.EstadoSolicitud
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.ui.theme.*
import com.example.redpatrimonial.viewmodel.DocenteViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-14: estado de las solicitudes de guía del docente, con el guía asignado si ya lo hay.
 */
@Composable
fun MisSolicitudesScreen(
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
    val recintos by docenteViewModel.recintos.collectAsState()
    val guias by docenteViewModel.guias.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(titulo = "Mis solicitudes de guía", onVolver = { navController.popBackStack() })
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (docenteViewModel.solicitudes.isEmpty()) {
                item { MensajeVacio("Aún no solicitas guías. Hazlo al planificar una salida.") }
            }
            items(docenteViewModel.solicitudes) { solicitud ->
                val recinto = recintos.find { it.id == solicitud.idRecinto }?.nombre ?: "Recinto #${solicitud.idRecinto}"
                val guia = guias.find { it.id == solicitud.idGuia }?.nombre
                Card(colors = CardDefaults.cardColors(containerColor = FondoTarjeta), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(recinto, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                            EstadoSolicitudChip(solicitud.estado)
                        }
                        Text(
                            "${solicitud.fecha} · ${solicitud.hora} · ${solicitud.tamanoGrupo} estudiantes",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                        if (guia != null) {
                            Text("Guía: $guia", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Etiqueta de color según el estado de la solicitud. La reutiliza la pantalla de solicitudes del encargado.
 */
@Composable
fun EstadoSolicitudChip(estado: EstadoSolicitud) {
    val (fondo, texto) = when (estado) {
        EstadoSolicitud.PENDIENTE -> FondoAviso to NaranjaAviso
        EstadoSolicitud.ASIGNADA -> VerdeClaro to VerdePatrimonial
        EstadoSolicitud.ACEPTADA -> FondoExito to VerdeExito
        EstadoSolicitud.RECHAZADA -> FondoError to RojoError
    }
    Etiqueta(estado.etiqueta, fondo = fondo, colorTexto = texto)
}
