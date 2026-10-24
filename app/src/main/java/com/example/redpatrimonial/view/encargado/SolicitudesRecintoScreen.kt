package com.example.redpatrimonial.view.encargado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.redpatrimonial.view.docente.EstadoSolicitudChip
import com.example.redpatrimonial.viewmodel.GuiaViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-20: el encargado ve las solicitudes de guía de su recinto y asigna un guía o rechaza.
 * En las pendientes se elige un guía con los chips (se guarda en guiaElegido) y se pulsa "Asignar".
 */
@Composable
fun SolicitudesRecintoScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    guiaViewModel: GuiaViewModel = viewModel()
) {
    val usuario = sesionViewModel.usuario
    if (usuario == null) {
        SinSesion { navController.navigate(Routes.LOGIN) }
        return
    }
    LaunchedEffect(usuario.id) {
        guiaViewModel.cargarEncargado(usuario.id)
    }
    val guias by guiaViewModel.guias.collectAsState()
    val usuarios by guiaViewModel.usuarios.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Solicitudes de guía",
            subtitulo = guiaViewModel.recintoEncargado?.nombre,
            onVolver = { navController.popBackStack() }
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { MensajeError(guiaViewModel.mensajeError) }
            if (guiaViewModel.solicitudesRecinto.isEmpty()) {
                item { MensajeVacio("Tu recinto no tiene solicitudes de guía.") }
            }
            items(guiaViewModel.solicitudesRecinto) { solicitud ->
                Card(colors = CardDefaults.cardColors(containerColor = FondoTarjeta), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "${solicitud.fecha} · ${solicitud.hora}",
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f)
                            )
                            EstadoSolicitudChip(solicitud.estado)
                        }
                        Text(
                            "${solicitud.tamanoGrupo} estudiantes · ${guiaViewModel.nombreUsuario(usuarios, solicitud.idDocente)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                        if (solicitud.comentario.isNotBlank()) {
                            Text(solicitud.comentario, style = MaterialTheme.typography.bodySmall)
                        }

                        if (solicitud.estado == EstadoSolicitud.PENDIENTE) {
                            Text("Elige un guía", style = MaterialTheme.typography.labelMedium)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(guias) { guia ->
                                    FilterChip(
                                        selected = guiaViewModel.guiaElegido[solicitud.id] == guia.id,
                                        onClick = { guiaViewModel.guiaElegido[solicitud.id] = guia.id },
                                        label = { Text(guia.nombre) }
                                    )
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { guiaViewModel.rechazar(solicitud.id) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Rechazar", color = RojoError)
                                }
                                Button(
                                    onClick = { guiaViewModel.asignar(solicitud.id) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = VerdePatrimonial)
                                ) {
                                    Text("Asignar")
                                }
                            }
                        } else if (solicitud.idGuia != null) {
                            Text("Guía: ${guiaViewModel.nombreUsuario(usuarios, solicitud.idGuia)}")
                        }
                    }
                }
            }
        }
    }
}
