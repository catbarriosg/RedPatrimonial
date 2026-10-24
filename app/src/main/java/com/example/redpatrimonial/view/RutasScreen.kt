
package com.example.redpatrimonial.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.Encabezado
import com.example.redpatrimonial.ui.components.ImagenPlaceholder
import com.example.redpatrimonial.ui.components.MensajeVacio
import com.example.redpatrimonial.ui.components.TituloSeccion
import com.example.redpatrimonial.ui.theme.*
import com.example.redpatrimonial.util.CalculadoraTiempos
import com.example.redpatrimonial.viewmodel.RutaConParadas
import com.example.redpatrimonial.viewmodel.RutasViewModel

/**
 * CU-08: rutas sugeridas y rutas personalizadas, más el acceso para crear una ruta propia.
 * Si llega un idRecinto (desde la ficha de un recinto), solo se muestran las rutas que pasan por ese recinto;
 * el chip "Incluyen: ..." permite quitar el filtro.
 */
@Composable
fun RutasScreen(
    navController: NavController,
    idRecinto: Int? = null,
    rutasViewModel: RutasViewModel = viewModel()
) {
    val oficiales by rutasViewModel.rutasOficiales.collectAsState()
    val personalizadas by rutasViewModel.rutasPersonalizadas.collectAsState()
    val oficialesFiltradas = rutasViewModel.filtrarPorRecinto(oficiales, idRecinto)
    val personalizadasFiltradas = rutasViewModel.filtrarPorRecinto(personalizadas, idRecinto)
    val nombreRecinto = (oficiales + personalizadas)
        .flatMap { it.paradas }
        .find { it.id == idRecinto }?.nombre

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Rutas sugeridas",
            subtitulo = "Circuitos temáticos diseñados para recorrer a pie",
            onVolver = if (idRecinto != null) ({ navController.popBackStack() }) else null
        )

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (idRecinto != null) {
                item {
                    InputChip(
                        selected = true,
                        onClick = { navController.navigate(Routes.RUTAS) { popUpTo(Routes.MAPA) } },
                        label = { Text("Incluyen: ${nombreRecinto ?: "este recinto"}") },
                        trailingIcon = { Icon(Icons.Outlined.Close, contentDescription = "Ver todas las rutas") }
                    )
                }
            }

            if (oficialesFiltradas.isEmpty()) {
                item { MensajeVacio("No hay rutas oficiales que pasen por este recinto.") }
            }
            items(oficialesFiltradas) { ruta ->
                TarjetaRuta(ruta) { navController.navigate(Routes.rutaDetalle(ruta.ruta.id)) }
            }

            item {
                Card(
                    onClick = { navController.navigate(Routes.RUTA_PERSONALIZADA) },
                    colors = CardDefaults.cardColors(containerColor = FondoAviso.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, DoradoAntiguo),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AddCircleOutline, contentDescription = null, tint = DoradoAntiguo)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("¿Quieres armar tu propio recorrido?", fontWeight = FontWeight.SemiBold)
                            Text(
                                "Crea una ruta personalizada con los templos que elijas",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                        }
                    }
                }
            }

            if (personalizadasFiltradas.isNotEmpty()) {
                item { TituloSeccion("Mis rutas personalizadas") }
                items(personalizadasFiltradas) { ruta ->
                    TarjetaRuta(ruta) { navController.navigate(Routes.rutaDetalle(ruta.ruta.id)) }
                }
            }
        }
    }
}

/**
 * Tarjeta de una ruta: nombre, paradas, distancia y duración.
 * joinToString(" · ") une los nombres de las paradas en un solo texto.
 */
@Composable
fun TarjetaRuta(rutaConParadas: RutaConParadas, onClick: () -> Unit) {
    val ruta = rutaConParadas.ruta
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ImagenPlaceholder(texto = "${rutaConParadas.paradas.size} paradas", modifier = Modifier.size(80.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(ruta.nombre, fontWeight = FontWeight.SemiBold)
                Text(
                    rutaConParadas.paradas.joinToString(" · ") { it.nombre },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Terracota, modifier = Modifier.size(16.dp))
                    Text(" ${ruta.distanciaKm} km", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Outlined.Schedule, contentDescription = null, tint = Terracota, modifier = Modifier.size(16.dp))
                    Text(" ${CalculadoraTiempos.formatearDuracion(ruta.duracionMin)}", fontSize = 13.sp)
                }
            }
        }
    }
}
