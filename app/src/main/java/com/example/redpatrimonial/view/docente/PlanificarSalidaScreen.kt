package com.example.redpatrimonial.view.docente

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.viewmodel.DocenteViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-12 y CU-13: formulario para planificar una salida pedagógica y, si se quiere, pedir un guía.
 * Al elegir una ruta se muestran sus paradas, y entre ellas se elige el recinto donde se necesita el guía.
 * La parte del guía solo se muestra si el interruptor "Solicitar guía voluntario" está activo.
 */
@Composable
fun PlanificarSalidaScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    docenteViewModel: DocenteViewModel = viewModel()
) {
    val usuario = sesionViewModel.usuario
    if (usuario == null) {
        SinSesion { navController.navigate(Routes.LOGIN) }
        return
    }
    val rutas by docenteViewModel.rutas.collectAsState()
    val paradas by docenteViewModel.paradas.collectAsState()
    val recintos by docenteViewModel.recintos.collectAsState()
    val recintosDeRuta = docenteViewModel.recintosDeRuta(docenteViewModel.idRuta, paradas, recintos)

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(titulo = "Planificar salida", onVolver = { navController.popBackStack() })
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Ruta de la salida", style = MaterialTheme.typography.labelLarge)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(rutas) { ruta ->
                    FilterChip(
                        selected = docenteViewModel.idRuta == ruta.id,
                        onClick = { docenteViewModel.elegirRuta(ruta.id) },
                        label = { Text(ruta.nombre) }
                    )
                }
            }
            if (recintosDeRuta.isNotEmpty()) {
                Text(
                    "Paradas: " + recintosDeRuta.joinToString(" · ") { it.nombre },
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoTexto(docenteViewModel.fecha, { docenteViewModel.fecha = it }, "Fecha", Modifier.weight(1.4f), ejemplo = "2026-11-20")
                CampoTexto(docenteViewModel.hora, { docenteViewModel.hora = it }, "Hora", Modifier.weight(1f), ejemplo = "10:00")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CampoTexto(docenteViewModel.tamanoGrupo, { docenteViewModel.tamanoGrupo = it }, "Estudiantes", Modifier.weight(1f), ejemplo = "30", teclado = KeyboardType.Number)
                CampoTexto(docenteViewModel.nivelEducativo, { docenteViewModel.nivelEducativo = it }, "Nivel", Modifier.weight(1f), ejemplo = "2° medio")
            }
            CampoTexto(docenteViewModel.observaciones, { docenteViewModel.observaciones = it }, "Observaciones", lineas = 2)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Solicitar guía voluntario", modifier = Modifier.weight(1f))
                Switch(
                    checked = docenteViewModel.solicitarGuia,
                    onCheckedChange = { docenteViewModel.solicitarGuia = it }
                )
            }

            if (docenteViewModel.solicitarGuia) {
                Text("¿En qué recinto necesitas el guía?", style = MaterialTheme.typography.labelLarge)
                if (recintosDeRuta.isEmpty()) {
                    MensajeVacio("Primero elige una ruta.")
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(recintosDeRuta) { recinto ->
                        FilterChip(
                            selected = docenteViewModel.idRecintoGuia == recinto.id,
                            onClick = { docenteViewModel.idRecintoGuia = recinto.id },
                            label = { Text(recinto.nombre) }
                        )
                    }
                }
                CampoTexto(docenteViewModel.comentario, { docenteViewModel.comentario = it }, "Comentario para el recinto", lineas = 2)
            }

            MensajeError(docenteViewModel.mensajeError)
            BotonPrincipal(
                texto = "Guardar salida",
                onClick = {
                    if (docenteViewModel.planificar(usuario.id)) {
                        navController.navigate(Routes.MIS_SOLICITUDES) {
                            popUpTo(Routes.PANEL_DOCENTE)
                        }
                    }
                }
            )
        }
    }
}
