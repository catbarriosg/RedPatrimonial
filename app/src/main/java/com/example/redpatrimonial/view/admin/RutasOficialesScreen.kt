package com.example.redpatrimonial.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.model.TipoRuta
import com.example.redpatrimonial.ui.components.BotonPrincipal
import com.example.redpatrimonial.ui.components.Encabezado
import com.example.redpatrimonial.ui.components.TituloSeccion
import com.example.redpatrimonial.ui.theme.RojoError
import com.example.redpatrimonial.view.ArmadorDeRuta
import com.example.redpatrimonial.view.TarjetaRuta
import com.example.redpatrimonial.viewmodel.RutaPersonalizadaViewModel
import com.example.redpatrimonial.viewmodel.RutasViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-26: el administrador crea y elimina las rutas oficiales de la red.
 * creando es un estado local de la pantalla (remember): alterna entre la lista y el armador de rutas
 * sin navegar a otra pantalla.
 */
@Composable
fun RutasOficialesScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    rutasViewModel: RutasViewModel = viewModel(),
    armadorViewModel: RutaPersonalizadaViewModel = viewModel()
) {
    val oficiales by rutasViewModel.rutasOficiales.collectAsState()
    var creando by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = if (creando) "Nueva ruta oficial" else "Rutas oficiales",
            onVolver = { if (creando) creando = false else navController.popBackStack() }
        )

        if (creando) {
            ArmadorDeRuta(
                rutaViewModel = armadorViewModel,
                textoBoton = "Publicar ruta oficial",
                onGuardar = {
                    armadorViewModel.guardar(TipoRuta.OFICIAL, sesionViewModel.usuario?.id) {
                        creando = false
                    }
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BotonPrincipal(texto = "Crear ruta oficial", onClick = { creando = true })
                TituloSeccion("Publicadas (${oficiales.size})")
                oficiales.forEach { ruta ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.weight(1f)) {
                            TarjetaRuta(ruta) {}
                        }
                        IconButton(onClick = { rutasViewModel.eliminar(ruta.ruta.id) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "Eliminar ${ruta.ruta.nombre}", tint = RojoError)
                        }
                    }
                }
            }
        }
    }
}
