package com.example.redpatrimonial.view.guia

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Schedule
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
import com.example.redpatrimonial.ui.theme.*
import com.example.redpatrimonial.viewmodel.GuiaViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-22: panel del guía. Muestra los recorridos asignados para aceptar o rechazar,
 * y accesos a su disponibilidad y su agenda.
 */
@Composable
fun PanelGuiaScreen(
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
        guiaViewModel.cargarGuia(usuario.id)
    }
    val recintos by guiaViewModel.recintos.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Panel del guía",
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
            TarjetaAccion("Mi disponibilidad", "Días y horarios en que puedes guiar", Icons.Outlined.Schedule) {
                navController.navigate(Routes.DISPONIBILIDAD)
            }
            TarjetaAccion("Mi agenda", if (guiaViewModel.agenda.size == 1) "1 recorrido confirmado" else "${guiaViewModel.agenda.size} recorridos confirmados", Icons.Outlined.CalendarMonth) {
                navController.navigate(Routes.AGENDA)
            }

            TituloSeccion("Recorridos por responder (${guiaViewModel.asignaciones.size})")
            if (guiaViewModel.asignaciones.isEmpty()) {
                MensajeVacio("No tienes recorridos pendientes de respuesta.")
            }
            guiaViewModel.asignaciones.forEach { solicitud ->
                Card(colors = CardDefaults.cardColors(containerColor = FondoTarjeta), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(guiaViewModel.nombreRecinto(recintos, solicitud.idRecinto), fontWeight = FontWeight.SemiBold)
                        Text(
                            "${solicitud.fecha} · ${solicitud.hora} · ${solicitud.tamanoGrupo} estudiantes",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                        if (solicitud.comentario.isNotBlank()) {
                            Text(solicitud.comentario, style = MaterialTheme.typography.bodySmall)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { guiaViewModel.responder(solicitud, acepta = false) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Rechazar", color = RojoError)
                            }
                            Button(
                                onClick = { guiaViewModel.responder(solicitud, acepta = true) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VerdePatrimonial)
                            ) {
                                Text("Aceptar")
                            }
                        }
                    }
                }
            }
        }
    }
}
