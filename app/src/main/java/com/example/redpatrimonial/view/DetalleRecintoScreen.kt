
package com.example.redpatrimonial.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.EstadoChip
import com.example.redpatrimonial.ui.components.ImagenPlaceholder
import com.example.redpatrimonial.ui.components.TituloSeccion
import com.example.redpatrimonial.ui.theme.*
import com.example.redpatrimonial.viewmodel.DetalleViewModel
import com.example.redpatrimonial.viewmodel.FavoritoViewModel

/**
 * Ficha patrimonial de un recinto (CU-03 y CU-04): reseña, galería, horarios, aperturas especiales,
 * próximos eventos y botón para ver las rutas que lo incluyen.
 * - idRecinto llega como argumento de la navegación.
 * - LaunchedEffect(idRecinto) ejecuta la carga una sola vez (y de nuevo solo si cambia el id),
 *   no en cada recomposición.
 * - Mientras el recinto es null se muestra un indicador de carga y "return" termina la función.
 * - verticalScroll(rememberScrollState()) permite desplazar la pantalla cuando el contenido no cabe.
 */
@Composable
fun DetalleRecintoScreen(
    navController: NavController,
    idRecinto: Int,
    detalleViewModel: DetalleViewModel = viewModel(),
    favoritoViewModel: FavoritoViewModel = viewModel()
) {
    LaunchedEffect(idRecinto) {
        detalleViewModel.cargar(idRecinto)
    }

    val recinto = detalleViewModel.recinto
    val favoritos by favoritoViewModel.favoritos.collectAsState()

    if (recinto == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = VerdePatrimonial)
        }
        return
    }

    val esFavorito = favoritoViewModel.esRecintoFavorito(favoritos, recinto.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Portada con botones de volver y favorito
        Box {
            ImagenPlaceholder(
                texto = recinto.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BotonCircular(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Blanco)
                }
                BotonCircular(onClick = { favoritoViewModel.alternarRecinto(recinto.id, esFavorito) }) {
                    Icon(
                        if (esFavorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (esFavorito) "Quitar de favoritos" else "Agregar a favoritos",
                        tint = DoradoAntiguo
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "${recinto.categoria.uppercase()} · ${recinto.estilo.uppercase()}",
                    fontSize = 12.sp,
                    color = DoradoAntiguo,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                EstadoChip(recinto.estadoActual)
            }
            Text(
                recinto.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = VerdePatrimonial
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Terracota, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(recinto.direccion, color = TextoSecundario)
            }

            TituloSeccion("Reseña histórica")
            Text(recinto.resenaHistorica, style = MaterialTheme.typography.bodyMedium)

            TituloSeccion("Galería de detalles")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("Fachada", "Interior", "Altar", "Detalle")) { foto ->
                    ImagenPlaceholder(texto = foto, icono = Icons.Outlined.Photo, modifier = Modifier.size(110.dp, 90.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Horarios de visita", fontWeight = FontWeight.SemiBold)
                    FilaHorario("Lunes a viernes", recinto.horarioSemana)
                    FilaHorario("Sábados", recinto.horarioSabado)
                    FilaHorario("Domingos (misa)", recinto.horarioDomingo)
                }
            }

            if (detalleViewModel.aperturas.isNotEmpty()) {
                TituloSeccion("Aperturas especiales")
                detalleViewModel.aperturas.forEach { apertura ->
                    Text(
                        "${apertura.fecha} · ${apertura.horaInicio} a ${apertura.horaFin} — ${apertura.motivo}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (detalleViewModel.eventos.isNotEmpty()) {
                TituloSeccion("Próximos eventos")
                detalleViewModel.eventos.forEach { evento ->
                    Card(
                        onClick = { navController.navigate(Routes.detalleEvento(evento.id)) },
                        colors = CardDefaults.cardColors(containerColor = VerdeMuyClaro),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = VerdePatrimonial)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(evento.titulo, fontWeight = FontWeight.SemiBold)
                                Text("${evento.tipo} · ${evento.fecha} · ${evento.hora}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { navController.navigate(Routes.rutasDeRecinto(recinto.id)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePatrimonial)
            ) {
                Text("Ver rutas que incluyen este templo", modifier = Modifier.weight(1f))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

/**
 * Botón redondo semitransparente que se dibuja sobre la imagen de portada.
 * contenido es un slot: recibe el ícono que va adentro.
 */
@Composable
private fun BotonCircular(onClick: () -> Unit, contenido: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(TextoPrincipal.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) { contenido() }
    }
}

/**
 * Una fila de la tabla de horarios: día a la izquierda, horario a la derecha.
 */
@Composable
private fun FilaHorario(dia: String, horario: String) {
    Row {
        Text(dia, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(horario, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
    }
}
