package com.example.redpatrimonial.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.viewmodel.AdminViewModel

/**
 * CU-24: formulario para dar de alta un recinto (nodo de la red) y asociarle un encargado.
 * KeyboardType.Decimal muestra el teclado numérico con punto para las coordenadas.
 */
@Composable
fun RegistrarNodoScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = viewModel()
) {
    val encargados by adminViewModel.encargados.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(titulo = "Registrar nodo", onVolver = { navController.popBackStack() })
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto(adminViewModel.nombre, { adminViewModel.nombre = it }, "Nombre oficial")

            Text("Tipo de recinto", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(adminViewModel.tiposRecinto) { tipo ->
                    FilterChip(
                        selected = adminViewModel.tipo == tipo,
                        onClick = { adminViewModel.tipo = tipo },
                        label = { Text(tipo) }
                    )
                }
            }

            CampoTexto(adminViewModel.comuna, { adminViewModel.comuna = it }, "Comuna", ejemplo = "Independencia")
            CampoTexto(adminViewModel.direccion, { adminViewModel.direccion = it }, "Dirección")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoTexto(adminViewModel.latitud, { adminViewModel.latitud = it }, "Latitud", Modifier.weight(1f), ejemplo = "-33.4378", teclado = KeyboardType.Decimal)
                CampoTexto(adminViewModel.longitud, { adminViewModel.longitud = it }, "Longitud", Modifier.weight(1f), ejemplo = "-70.6504", teclado = KeyboardType.Decimal)
            }
            CampoTexto(adminViewModel.siglo, { adminViewModel.siglo = it }, "Siglo de construcción", ejemplo = "XIX")
            CampoTexto(adminViewModel.resena, { adminViewModel.resena = it }, "Reseña histórica", lineas = 4)

            Text("Encargado del recinto", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(encargados) { encargado ->
                    FilterChip(
                        selected = adminViewModel.idEncargado == encargado.id,
                        onClick = {
                            adminViewModel.idEncargado = if (adminViewModel.idEncargado == encargado.id) null else encargado.id
                        },
                        label = { Text(encargado.nombre) }
                    )
                }
            }

            MensajeError(adminViewModel.mensajeError)
            BotonPrincipal(
                texto = "Registrar en la red",
                onClick = {
                    if (adminViewModel.registrarNodo()) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
