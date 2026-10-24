package com.example.redpatrimonial.view.encargado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.model.EstadoApertura
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.ui.theme.TextoSecundario
import com.example.redpatrimonial.ui.theme.VerdeExito
import com.example.redpatrimonial.viewmodel.EncargadoViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * CU-17 cambiar el estado de apertura y CU-18 programar una apertura especial.
 * También muestra las aperturas programadas y el historial de cambios.
 * Los RadioButton permiten elegir una sola opción; selectable hace que toda la fila responda al toque.
 * SimpleDateFormat convierte la fecha guardada en milisegundos a texto legible.
 */
@Composable
fun CambiarEstadoScreen(
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
    val formatoFecha = remember { SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Estado del recinto",
            subtitulo = recinto?.nombre,
            onVolver = { navController.popBackStack() }
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (recinto != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Estado actual: ", color = TextoSecundario)
                    EstadoChip(recinto.estadoActual)
                }
            }

            TituloSeccion("Nuevo estado")
            EstadoApertura.entries.forEach { estado ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = encargadoViewModel.estadoElegido == estado,
                            onClick = { encargadoViewModel.estadoElegido = estado }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = encargadoViewModel.estadoElegido == estado,
                        onClick = { encargadoViewModel.estadoElegido = estado }
                    )
                    Text(estado.etiqueta)
                }
            }
            BotonPrincipal(texto = "Guardar estado", onClick = { encargadoViewModel.guardarEstado() })

            MensajeError(encargadoViewModel.mensajeError)
            encargadoViewModel.mensajeExito?.let { Text(it, color = VerdeExito) }

            TituloSeccion("Programar apertura especial")
            Text(
                "Úsala cuando el recinto esté cerrado y vaya a abrir en un horario excepcional.",
                style = MaterialTheme.typography.bodySmall,
                color = TextoSecundario
            )
            CampoTexto(encargadoViewModel.aperturaFecha, { encargadoViewModel.aperturaFecha = it }, "Fecha", ejemplo = "2026-11-20")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoTexto(encargadoViewModel.aperturaInicio, { encargadoViewModel.aperturaInicio = it }, "Desde", Modifier.weight(1f), ejemplo = "15:00")
                CampoTexto(encargadoViewModel.aperturaFin, { encargadoViewModel.aperturaFin = it }, "Hasta", Modifier.weight(1f), ejemplo = "18:00")
            }
            CampoTexto(encargadoViewModel.aperturaMotivo, { encargadoViewModel.aperturaMotivo = it }, "Motivo", ejemplo = "Visita de delegación escolar")
            BotonDorado(texto = "Programar apertura", onClick = { encargadoViewModel.programarApertura() })

            if (encargadoViewModel.aperturas.isNotEmpty()) {
                TituloSeccion("Aperturas programadas")
                encargadoViewModel.aperturas.forEach { apertura ->
                    Text("${apertura.fecha} · ${apertura.horaInicio} a ${apertura.horaFin} — ${apertura.motivo}")
                }
            }

            TituloSeccion("Historial de cambios")
            if (encargadoViewModel.historial.isEmpty()) {
                MensajeVacio("Aún no hay cambios de estado registrados.")
            }
            encargadoViewModel.historial.forEach { cambio ->
                Text(
                    "${formatoFecha.format(Date(cambio.fechaHora))}: ${cambio.estadoAnterior.etiqueta} → ${cambio.estadoNuevo.etiqueta}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
