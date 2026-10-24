package com.example.redpatrimonial.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.model.TipoRuta
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.BotonDorado
import com.example.redpatrimonial.ui.components.Encabezado
import com.example.redpatrimonial.ui.components.TituloSeccion
import com.example.redpatrimonial.ui.theme.*
import com.example.redpatrimonial.util.CalculadoraTiempos
import com.example.redpatrimonial.viewmodel.RutasViewModel

/**
 * Detalle de una ruta: resumen de distancia y duración, y la lista de paradas.
 * Las paradas cuyo QR ya se escaneó aparecen en verde con un check.
 * Si la ruta es personalizada, el encabezado muestra un botón para eliminarla.
 * itemsIndexed entrega la posición de cada parada para numerarla.
 */
@Composable
fun RutaDetalleScreen(
    navController: NavController,
    idRuta: Int,
    rutasViewModel: RutasViewModel = viewModel()
) {
    LaunchedEffect(idRuta) {
        rutasViewModel.cargarDetalle(idRuta)
    }
    val detalle = rutasViewModel.rutaDetalle
    val visitados = rutasViewModel.recintosVisitados

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = detalle?.ruta?.nombre ?: "Ruta",
            subtitulo = detalle?.ruta?.descripcion,
            onVolver = { navController.popBackStack() },
            acciones = {
                if (detalle?.ruta?.tipo == TipoRuta.PERSONALIZADA) {
                    IconButton(onClick = {
                        rutasViewModel.eliminar(detalle.ruta.id)
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Outlined.Delete, contentDescription = "Eliminar ruta", tint = RojoError)
                    }
                }
            }
        )

        if (detalle == null) return

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                ResumenRuta(
                    distanciaKm = detalle.ruta.distanciaKm,
                    duracionMin = detalle.ruta.duracionMin,
                    visitadas = detalle.paradas.count { it.id in visitados },
                    total = detalle.paradas.size
                )
            }
            item { TituloSeccion("Paradas del recorrido") }
            itemsIndexed(detalle.paradas) { indice, recinto ->
                val visitado = recinto.id in visitados
                Card(
                    onClick = { navController.navigate(Routes.detalleRecinto(recinto.id)) },
                    colors = CardDefaults.cardColors(containerColor = if (visitado) FondoExito else FondoTarjeta),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        NumeroParada(indice + 1)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(recinto.nombre, fontWeight = FontWeight.SemiBold)
                            Text(recinto.direccion, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                            Text(
                                "Visita sugerida: ${CalculadoraTiempos.MINUTOS_VISITA_POR_PARADA} min",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                        }
                        if (visitado) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = "Visitado", tint = VerdeExito)
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Escanea el código QR en la entrada de cada recinto para marcar la parada como visitada.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
                Spacer(modifier = Modifier.height(8.dp))
                BotonDorado(texto = "Escanear QR de una parada", onClick = { navController.navigate(Routes.QR) })
            }
        }
    }
}

/**
 * Círculo verde con el número de la parada.
 */
@Composable
fun NumeroParada(numero: Int) {
    Box(
        modifier = Modifier
            .size(30.dp)
            .background(VerdePatrimonial, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text("$numero", color = Blanco, fontWeight = FontWeight.Bold)
    }
}

/**
 * Tarjeta oscura con la distancia y la duración estimada, como en el mockup.
 * visitadas y total son opcionales: si llegan, se muestra cuántas paradas ya se visitaron.
 */
@Composable
fun ResumenRuta(distanciaKm: Double, duracionMin: Int, visitadas: Int? = null, total: Int? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VerdePatrimonial, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("CÁLCULO ESTIMADO", color = DoradoAntiguo, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.labelMedium)
        Row {
            Column(modifier = Modifier.weight(1f)) {
                Text("Distancia total", color = TextoClaroSecundario, style = MaterialTheme.typography.bodySmall)
                Text("$distanciaKm kilómetros", color = Blanco, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Duración estimada", color = TextoClaroSecundario, style = MaterialTheme.typography.bodySmall)
                Text(CalculadoraTiempos.formatearDuracion(duracionMin), color = Blanco, fontWeight = FontWeight.Bold)
            }
        }
        if (visitadas != null && total != null) {
            Text("Paradas visitadas: $visitadas de $total", color = Marfil, style = MaterialTheme.typography.bodySmall)
        }
    }
}
