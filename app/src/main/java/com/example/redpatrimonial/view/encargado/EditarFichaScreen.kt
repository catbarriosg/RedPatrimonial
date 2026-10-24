package com.example.redpatrimonial.view.encargado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
 * CU-16: formulario para editar la ficha del recinto.
 * Cada CampoTexto lee su valor del ViewModel y, al escribir, lo actualiza ({ encargadoViewModel.nombre = it }).
 * Al guardar, si guardarFicha() devuelve true, popBackStack() vuelve a la pantalla anterior.
 */
@Composable
fun EditarFichaScreen(
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
            titulo = "Editar ficha",
            subtitulo = encargadoViewModel.recinto?.nombre,
            onVolver = { navController.popBackStack() }
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CampoTexto(encargadoViewModel.nombre, { encargadoViewModel.nombre = it }, "Nombre oficial")
            CampoTexto(encargadoViewModel.direccion, { encargadoViewModel.direccion = it }, "Dirección")
            CampoTexto(encargadoViewModel.resena, { encargadoViewModel.resena = it }, "Reseña histórica", lineas = 5)

            TituloSeccion("Horarios de visita")
            CampoTexto(encargadoViewModel.horarioSemana, { encargadoViewModel.horarioSemana = it }, "Lunes a viernes", ejemplo = "09:00 - 18:00")
            CampoTexto(encargadoViewModel.horarioSabado, { encargadoViewModel.horarioSabado = it }, "Sábados", ejemplo = "10:00 - 14:00")
            CampoTexto(encargadoViewModel.horarioDomingo, { encargadoViewModel.horarioDomingo = it }, "Domingos", ejemplo = "11:00 - 13:00")

            MensajeError(encargadoViewModel.mensajeError)
            BotonPrincipal(
                texto = "Guardar cambios",
                onClick = {
                    if (encargadoViewModel.guardarFicha()) {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}
