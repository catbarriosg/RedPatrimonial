
package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
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
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.ui.theme.FondoTarjeta
import com.example.redpatrimonial.ui.theme.TextoSecundario
import com.example.redpatrimonial.viewmodel.RutaPersonalizadaViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-09: armar una ruta propia. Usa ArmadorDeRuta y, al guardar, navega al detalle de la ruta nueva.
 */
@Composable
fun RutaPersonalizadaScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    rutaViewModel: RutaPersonalizadaViewModel = viewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Ruta personalizada",
            subtitulo = "Selecciona templos para tu recorrido",
            onVolver = { navController.popBackStack() }
        )
        ArmadorDeRuta(
            rutaViewModel = rutaViewModel,
            textoBoton = "Guardar mi ruta",
            onGuardar = {
                rutaViewModel.guardar(TipoRuta.PERSONALIZADA, sesionViewModel.usuario?.id) { idRuta ->
                    navController.navigate(Routes.rutaDetalle(idRuta)) {
                        popUpTo(Routes.RUTAS)
                    }
                }
            }
        )
    }
}

/**
 * Lista para elegir paradas, ordenarlas y ver el tiempo estimado. También la usa el administrador (CU-26).
 * - Arriba van las paradas elegidas con botones para subir, bajar o quitar.
 * - Con dos o más paradas se muestra el cálculo (CU-10).
 * - Abajo está la lista de todos los recintos con checkbox.
 * Cada item tiene una key única para que Compose sepa qué fila es cuál al reordenar.
 * Las claves llevan prefijo ("parada-" y "recinto-") porque el mismo recinto aparece en las dos listas
 * y Compose no acepta claves repetidas dentro de un mismo LazyColumn.
 */
@Composable
fun ArmadorDeRuta(
    rutaViewModel: RutaPersonalizadaViewModel,
    textoBoton: String,
    onGuardar: () -> Unit
) {
    val recintos by rutaViewModel.recintos.collectAsState()
    val resumen = rutaViewModel.resumen()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { TituloSeccion("Tu recorrido (${rutaViewModel.seleccionados.size} paradas)") }
        if (rutaViewModel.seleccionados.isEmpty()) {
            item { MensajeVacio("Marca los recintos de la lista de abajo para agregarlos.") }
        }
        // Las claves llevan prefijo: el mismo recinto aparece en ambas listas y Compose no acepta claves repetidas
        itemsIndexed(rutaViewModel.seleccionados, key = { _, recinto -> "parada-${recinto.id}" }) { indice, recinto ->
            Card(colors = CardDefaults.cardColors(containerColor = FondoTarjeta)) {
                Row(modifier = Modifier.padding(start = 12.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    NumeroParada(indice + 1)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(recinto.nombre, fontWeight = FontWeight.SemiBold)
                        Text(recinto.comuna, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    }
                    IconButton(onClick = { rutaViewModel.subir(indice) }, enabled = indice > 0) {
                        Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Subir")
                    }
                    IconButton(onClick = { rutaViewModel.bajar(indice) }, enabled = indice < rutaViewModel.seleccionados.size - 1) {
                        Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Bajar")
                    }
                    IconButton(onClick = { rutaViewModel.alternar(recinto) }) {
                        Icon(Icons.Outlined.Close, contentDescription = "Quitar")
                    }
                }
            }
        }

        if (rutaViewModel.seleccionados.size >= 2) {
            item { ResumenRuta(distanciaKm = resumen.distanciaKm, duracionMin = resumen.duracionTotalMin) }
            item {
                Text(
                    "${resumen.minutosCaminando} min caminando + ${resumen.minutosVisita} min de visita",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextoSecundario
                )
            }
        }

        item {
            CampoTexto(rutaViewModel.nombre, { rutaViewModel.nombre = it }, "Nombre de la ruta", ejemplo = "Mi recorrido del sábado")
        }
        item {
            CampoTexto(rutaViewModel.descripcion, { rutaViewModel.descripcion = it }, "Descripción (opcional)")
        }
        item { MensajeError(rutaViewModel.mensajeError) }
        item { BotonDorado(texto = textoBoton, onClick = onGuardar) }

        item { TituloSeccion("Recintos de la red") }
        items(recintos, key = { "recinto-${it.id}" }) { recinto ->
            Card(colors = CardDefaults.cardColors(containerColor = FondoTarjeta)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(recinto.nombre)
                        Text("${recinto.tipo} · ${recinto.comuna}", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    }
                    Checkbox(
                        checked = rutaViewModel.estaSeleccionado(recinto),
                        onCheckedChange = { rutaViewModel.alternar(recinto) }
                    )
                }
            }
        }
    }
}
