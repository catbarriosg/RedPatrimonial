package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.BotonPrincipal
import com.example.redpatrimonial.ui.components.Encabezado
import com.example.redpatrimonial.ui.components.Etiqueta
import com.example.redpatrimonial.ui.components.ImagenPlaceholder
import com.example.redpatrimonial.ui.components.TituloSeccion
import com.example.redpatrimonial.ui.theme.*
import com.example.redpatrimonial.util.CalculadoraTiempos
import com.example.redpatrimonial.viewmodel.EventosViewModel
import com.example.redpatrimonial.viewmodel.FavoritoViewModel

/**
 * Detalle de un evento: tipo, fecha, hora, recinto, distancia y descripción.
 * El corazón del encabezado guarda o quita el evento de favoritos.
 */
@Composable
fun DetalleEventoScreen(
    navController: NavController,
    idEvento: Int,
    eventosViewModel: EventosViewModel = viewModel(),
    favoritoViewModel: FavoritoViewModel = viewModel()
) {
    LaunchedEffect(idEvento) {
        eventosViewModel.cargarDetalle(idEvento)
    }

    val evento = eventosViewModel.eventoDetalle
    val favoritos by favoritoViewModel.favoritos.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Detalle del evento",
            onVolver = { navController.popBackStack() },
            acciones = {
                if (evento != null) {
                    val esFavorito = favoritoViewModel.esEventoFavorito(favoritos, evento.id)
                    IconButton(onClick = { favoritoViewModel.alternarEvento(evento.id, esFavorito) }) {
                        Icon(
                            if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = Terracota
                        )
                    }
                }
            }
        )

        if (evento == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VerdePatrimonial)
            }
            return
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ImagenPlaceholder(
                texto = evento.tipo,
                icono = Icons.Outlined.MusicNote,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            )
            Etiqueta(evento.tipo, fondo = VerdeClaro, colorTexto = VerdePatrimonial)
            Text(
                evento.titulo,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = VerdePatrimonial
            )
            DatoEvento(Icons.Outlined.CalendarMonth, evento.fecha)
            DatoEvento(Icons.Outlined.Schedule, "${evento.hora} hrs")
            val distancia = CalculadoraTiempos.distanciaDesdeUsuarioKm(evento.latitud, evento.longitud)
            DatoEvento(Icons.Outlined.Church, "${evento.nombreRecinto} · A ${CalculadoraTiempos.formatearDistancia(distancia)}")

            TituloSeccion("Descripción")
            Text(evento.descripcion, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))
            BotonPrincipal(
                texto = "Ver ficha del recinto",
                onClick = { navController.navigate(Routes.detalleRecinto(evento.idRecinto)) }
            )
        }
    }
}

/**
 * Fila con un ícono y un texto, para fecha, hora y recinto.
 */
@Composable
private fun DatoEvento(icono: ImageVector, texto: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, contentDescription = null, tint = DoradoAntiguo, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(texto, style = MaterialTheme.typography.bodyLarge)
    }
}
