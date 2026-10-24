
package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.model.EstadoApertura
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.Encabezado
import com.example.redpatrimonial.ui.components.MensajeVacio
import com.example.redpatrimonial.ui.components.TarjetaRecinto
import com.example.redpatrimonial.ui.theme.DoradoAntiguo
import com.example.redpatrimonial.ui.theme.TextoSecundario
import com.example.redpatrimonial.viewmodel.BuscadorViewModel

/**
 * Búsqueda y filtros (CU-02).
 * Las opciones de comuna y tipo salen de los mismos recintos: map toma el campo, distinct quita repetidos y sorted ordena.
 * LazyColumn es una lista que solo dibuja los elementos visibles en pantalla (eficiente para listas largas):
 * item { } agrega un elemento suelto e items(lista) { } agrega uno por cada elemento de la lista.
 */
@Composable
fun BuscadorScreen(
    navController: NavController,
    buscadorViewModel: BuscadorViewModel = viewModel()
) {
    val recintos by buscadorViewModel.recintos.collectAsState()
    val resultados = buscadorViewModel.filtrar(recintos)
    val comunas = recintos.map { it.comuna }.distinct().sorted()
    val tipos = recintos.map { it.tipo }.distinct().sorted()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(titulo = "Búsqueda y filtros")

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                OutlinedTextField(
                    value = buscadorViewModel.busqueda,
                    onValueChange = { buscadorViewModel.cambiarBusqueda(it) },
                    placeholder = { Text("Nombre del recinto") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, tint = DoradoAntiguo) },
                    trailingIcon = {
                        if (buscadorViewModel.busqueda.isNotEmpty()) {
                            IconButton(onClick = { buscadorViewModel.cambiarBusqueda("") }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Borrar búsqueda")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                GrupoFiltros(
                    titulo = "Comuna",
                    opciones = comunas,
                    seleccionada = buscadorViewModel.comuna,
                    onElegir = { buscadorViewModel.elegirComuna(it) }
                )
            }
            item {
                GrupoFiltros(
                    titulo = "Tipo de edificación",
                    opciones = tipos,
                    seleccionada = buscadorViewModel.tipo,
                    onElegir = { buscadorViewModel.elegirTipo(it) }
                )
            }
            item {
                GrupoFiltros(
                    titulo = "Estado de acceso",
                    opciones = EstadoApertura.entries.map { it.etiqueta },
                    seleccionada = buscadorViewModel.estado?.etiqueta,
                    onElegir = { etiqueta ->
                        buscadorViewModel.elegirEstado(EstadoApertura.entries.first { it.etiqueta == etiqueta })
                    }
                )
            }

            item {
                Row {
                    Text(
                        if (resultados.size == 1) "1 recinto coincide con tu búsqueda"
                        else "${resultados.size} recintos coinciden con tu búsqueda",
                        color = TextoSecundario,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { buscadorViewModel.limpiarFiltros() }) {
                        Text("Limpiar")
                    }
                }
            }

            if (resultados.isEmpty()) {
                item { MensajeVacio("No hay recintos con esos filtros. Prueba quitando alguno.") }
            }

            items(resultados) { recinto ->
                TarjetaRecinto(
                    recinto = recinto,
                    detalleExtra = recinto.direccion,
                    onClick = { navController.navigate(Routes.detalleRecinto(recinto.id)) }
                )
            }
        }
    }
}

/**
 * Título y fila de chips de un filtro. LazyRow es la versión horizontal de LazyColumn.
 * FilterChip queda marcado cuando selected es true.
 */
@Composable
private fun GrupoFiltros(
    titulo: String,
    opciones: List<String>,
    seleccionada: String?,
    onElegir: (String) -> Unit
) {
    Column {
        Text(titulo.uppercase(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(opciones) { opcion ->
                FilterChip(
                    selected = seleccionada == opcion,
                    onClick = { onElegir(opcion) },
                    label = { Text(opcion) }
                )
            }
        }
    }
}
