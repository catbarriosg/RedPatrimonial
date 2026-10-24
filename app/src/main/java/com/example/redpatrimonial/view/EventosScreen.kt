
package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.model.EventoConRecinto
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.Encabezado
import com.example.redpatrimonial.ui.components.Etiqueta
import com.example.redpatrimonial.ui.components.ImagenPlaceholder
import com.example.redpatrimonial.ui.components.MensajeVacio
import com.example.redpatrimonial.ui.theme.*
import com.example.redpatrimonial.util.CalculadoraTiempos
import com.example.redpatrimonial.viewmodel.EventosViewModel

/**
 * Eventos cercanos (CU-06), ordenados por distancia y con chips para filtrar por tipo.
 */
@Composable
fun EventosScreen(
    navController: NavController,
    eventosViewModel: EventosViewModel = viewModel()
) {
    val eventos by eventosViewModel.eventos.collectAsState()
    val filtrados = eventosViewModel.filtrar(eventos)

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Eventos cercanos",
            subtitulo = "Actividades litúrgicas, musicales y culturales en tu zona"
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(eventosViewModel.tipos) { tipo ->
                FilterChip(
                    selected = eventosViewModel.tipoFiltro == tipo,
                    onClick = { eventosViewModel.elegirTipo(tipo) },
                    label = { Text(tipo) }
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filtrados.isEmpty()) {
                item { MensajeVacio("No hay eventos de este tipo por ahora.") }
            }
            items(filtrados) { evento ->
                TarjetaEvento(
                    evento = evento,
                    distanciaKm = eventosViewModel.distanciaKm(evento),
                    onClick = { navController.navigate(Routes.detalleEvento(evento.id)) }
                )
            }
        }
    }
}

/**
 * Tarjeta de un evento: tipo, distancia, título, fecha y recinto.
 * Es pública porque la reutiliza la pantalla de favoritos; ahí se pasa distanciaKm = null para no mostrarla.
 */
@Composable
fun TarjetaEvento(evento: EventoConRecinto, distanciaKm: Double?, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        ImagenPlaceholder(
            texto = evento.tipo,
            icono = Icons.Outlined.MusicNote,
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        )
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Etiqueta(evento.tipo, fondo = VerdeClaro, colorTexto = VerdePatrimonial)
                Spacer(modifier = Modifier.weight(1f))
                if (distanciaKm != null) {
                    Text(
                        "A ${CalculadoraTiempos.formatearDistancia(distanciaKm)}",
                        color = Terracota,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Text(evento.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = DoradoAntiguo, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${evento.fecha} · ${evento.hora}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.weight(1f))
                Text(evento.nombreRecinto, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
            }
        }
    }
}
