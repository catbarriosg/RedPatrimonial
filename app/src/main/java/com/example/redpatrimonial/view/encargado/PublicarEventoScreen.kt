package com.example.redpatrimonial.view.encargado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.viewmodel.EncargadoViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-19: formulario para publicar un evento o aviso del recinto.
 * El evento queda guardado en Room y aparece de inmediato en la pantalla Eventos.
 * Al publicar navega a Eventos con popUpTo(PANEL_ENCARGADO): al volver atrás se llega al panel, no al formulario.
 */
@Composable
fun PublicarEventoScreen(
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

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Publicar evento o aviso",
            subtitulo = encargadoViewModel.recinto?.nombre,
            onVolver = { navController.popBackStack() }
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto(encargadoViewModel.eventoTitulo, { encargadoViewModel.eventoTitulo = it }, "Título", ejemplo = "Concierto de Navidad")

            Text("Tipo", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(encargadoViewModel.tiposEvento) { tipo ->
                    FilterChip(
                        selected = encargadoViewModel.eventoTipo == tipo,
                        onClick = { encargadoViewModel.eventoTipo = tipo },
                        label = { Text(tipo) }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoTexto(encargadoViewModel.eventoFecha, { encargadoViewModel.eventoFecha = it }, "Fecha", Modifier.weight(1.4f), ejemplo = "2026-12-24")
                CampoTexto(encargadoViewModel.eventoHora, { encargadoViewModel.eventoHora = it }, "Hora", Modifier.weight(1f), ejemplo = "20:00")
            }
            CampoTexto(encargadoViewModel.eventoDescripcion, { encargadoViewModel.eventoDescripcion = it }, "Descripción", lineas = 4)

            MensajeError(encargadoViewModel.mensajeError)
            BotonPrincipal(
                texto = "Publicar",
                onClick = {
                    if (encargadoViewModel.publicarEvento(usuario.id)) {
                        navController.navigate(Routes.EVENTOS) {
                            popUpTo(Routes.PANEL_ENCARGADO)
                        }
                    }
                }
            )
        }
    }
}
