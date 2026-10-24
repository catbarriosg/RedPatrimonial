package com.example.redpatrimonial.view.guia

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.*
import com.example.redpatrimonial.ui.theme.FondoTarjeta
import com.example.redpatrimonial.ui.theme.TextoSecundario
import com.example.redpatrimonial.viewmodel.GuiaViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-23: recorridos confirmados del guía, con fecha, recinto, grupo y docente.
 */
@Composable
fun AgendaScreen(
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
    val recintos by guiaViewModel.recintos.collectAsState()
    val usuarios by guiaViewModel.usuarios.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(titulo = "Mi agenda", subtitulo = "Recorridos confirmados", onVolver = { navController.popBackStack() })
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (guiaViewModel.agenda.isEmpty()) {
                item { MensajeVacio("No tienes recorridos confirmados.") }
            }
            items(guiaViewModel.agenda) { solicitud ->
                Card(colors = CardDefaults.cardColors(containerColor = FondoTarjeta), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${solicitud.fecha} · ${solicitud.hora}", fontWeight = FontWeight.SemiBold)
                        Text(guiaViewModel.nombreRecinto(recintos, solicitud.idRecinto))
                        Text(
                            "${solicitud.tamanoGrupo} estudiantes · ${guiaViewModel.nombreUsuario(usuarios, solicitud.idDocente)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                    }
                }
            }
        }
    }
}
