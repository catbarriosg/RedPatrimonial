package com.example.redpatrimonial.view.guia

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.ui.theme.FondoTarjeta
import com.example.redpatrimonial.ui.theme.RojoError
import com.example.redpatrimonial.viewmodel.GuiaViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-21: el guía registra los días y horarios en que puede guiar, y puede eliminar bloques.
 */
@Composable
fun DisponibilidadScreen(
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

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(titulo = "Mi disponibilidad", onVolver = { navController.popBackStack() })
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TituloSeccion("Agregar bloque")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(guiaViewModel.diasSemana) { dia ->
                    FilterChip(
                        selected = guiaViewModel.diaElegido == dia,
                        onClick = { guiaViewModel.diaElegido = dia },
                        label = { Text(dia) }
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoTexto(guiaViewModel.horaInicio, { guiaViewModel.horaInicio = it }, "Desde", Modifier.weight(1f), ejemplo = "09:00")
                CampoTexto(guiaViewModel.horaFin, { guiaViewModel.horaFin = it }, "Hasta", Modifier.weight(1f), ejemplo = "13:00")
            }
            MensajeError(guiaViewModel.mensajeError)
            BotonPrincipal(texto = "Agregar", onClick = { guiaViewModel.agregarDisponibilidad(usuario.id) })

            TituloSeccion("Bloques registrados")
            if (guiaViewModel.disponibilidad.isEmpty()) {
                MensajeVacio("Aún no registras disponibilidad.")
            }
            guiaViewModel.disponibilidad.forEach { bloque ->
                Card(colors = CardDefaults.cardColors(containerColor = FondoTarjeta), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(start = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(bloque.diaSemana, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("${bloque.horaInicio} a ${bloque.horaFin}")
                        IconButton(onClick = { guiaViewModel.eliminarDisponibilidad(bloque.id) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Eliminar bloque", tint = RojoError)
                        }
                    }
                }
            }
        }
    }
}
