package com.example.redpatrimonial.view.encargado

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.example.redpatrimonial.util.CodigoQr
import com.example.redpatrimonial.viewmodel.EncargadoViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * Panel del encargado: su recinto, accesos a las funciones de gestión y el código QR del recinto.
 * Si no hay sesión muestra SinSesion. LaunchedEffect(usuario.id) carga el recinto de este encargado.
 * remember(recinto.id) guarda la imagen del QR para no volver a generarla en cada recomposición;
 * solo se regenera si cambia el id.
 */
@Composable
fun PanelEncargadoScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    encargadoViewModel: EncargadoViewModel = viewModel()
) {
    val usuario = sesionViewModel.usuario
    if (usuario == null) {
        SinSesion { navController.navigate(Routes.LOGIN) }
        return
    }

    LaunchedEffect(usuario.id) {
        encargadoViewModel.cargar(usuario.id)
    }
    val recinto = encargadoViewModel.recinto

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Panel del encargado",
            subtitulo = usuario.nombre,
            acciones = {
                IconButton(onClick = { cerrarSesion(navController, sesionViewModel) }) {
                    Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Cerrar sesión", tint = VerdePatrimonial)
                }
            }
        )

        if (recinto == null) {
            MensajeVacio("No tienes un recinto asignado. Pide al administrador de la red que te asocie uno.", Modifier.padding(16.dp))
            return
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TarjetaRecinto(recinto = recinto, detalleExtra = recinto.direccion, onClick = {
                navController.navigate(Routes.detalleRecinto(recinto.id))
            })

            TituloSeccion("Gestión del recinto")
            TarjetaAccion("Editar ficha", "Reseña, dirección y horarios", Icons.Outlined.Edit) {
                navController.navigate(Routes.EDITAR_FICHA)
            }
            TarjetaAccion("Estado y aperturas especiales", "Abierto, cerrado o en celebración", Icons.Outlined.Schedule) {
                navController.navigate(Routes.CAMBIAR_ESTADO)
            }
            TarjetaAccion("Publicar evento o aviso", "Se muestra al instante en Eventos", Icons.Outlined.Campaign) {
                navController.navigate(Routes.PUBLICAR_EVENTO)
            }
            TarjetaAccion("Solicitudes de guía", "Asigna guías a las visitas pedagógicas", Icons.Outlined.Groups) {
                navController.navigate(Routes.SOLICITUDES_RECINTO)
            }
            TarjetaAccion("Ver ficha pública", "Así ven tu recinto los visitantes", Icons.Outlined.Visibility) {
                navController.navigate(Routes.detalleRecinto(recinto.id))
            }

            TituloSeccion("Código QR del recinto")
            Card(
                colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val qr = remember(recinto.id) { CodigoQr.generar(CodigoQr.contenidoRecinto(recinto.id)) }
                    Image(bitmap = qr, contentDescription = "Código QR de ${recinto.nombre}", modifier = Modifier.size(200.dp))
                    Text(CodigoQr.contenidoRecinto(recinto.id), fontWeight = FontWeight.Medium)
                    Text(
                        "Imprímelo y ubícalo en la entrada: al escanearlo se abre la ficha del recinto.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                }
            }
        }
    }
}
