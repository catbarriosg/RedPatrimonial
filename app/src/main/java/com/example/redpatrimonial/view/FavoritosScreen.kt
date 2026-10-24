package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.Encabezado
import com.example.redpatrimonial.ui.components.MensajeVacio
import com.example.redpatrimonial.ui.components.TarjetaRecinto
import com.example.redpatrimonial.ui.components.TituloSeccion
import com.example.redpatrimonial.viewmodel.FavoritoViewModel

/**
 * CU-05: recintos y eventos favoritos. Se guardan en Room y siguen ahí al cerrar la app.
 */
@Composable
fun FavoritosScreen(
    navController: NavController,
    favoritoViewModel: FavoritoViewModel = viewModel()
) {
    val recintos by favoritoViewModel.recintosFavoritos.collectAsState()
    val eventos by favoritoViewModel.eventosFavoritos.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Mis favoritos",
            subtitulo = "Guardados en este teléfono",
            onVolver = { navController.popBackStack() }
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { TituloSeccion("Recintos (${recintos.size})") }
            if (recintos.isEmpty()) {
                item { MensajeVacio("Aún no guardas recintos. Toca el corazón en una ficha para agregarlo.") }
            }
            items(recintos) { recinto ->
                TarjetaRecinto(
                    recinto = recinto,
                    onClick = { navController.navigate(Routes.detalleRecinto(recinto.id)) }
                )
            }

            item { TituloSeccion("Eventos (${eventos.size})") }
            if (eventos.isEmpty()) {
                item { MensajeVacio("Aún no guardas eventos.") }
            }
            items(eventos) { evento ->
                TarjetaEvento(
                    evento = evento,
                    distanciaKm = null,
                    onClick = { navController.navigate(Routes.detalleEvento(evento.id)) }
                )
            }
        }
    }
}
