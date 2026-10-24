package com.example.redpatrimonial.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.ui.components.Encabezado
import com.example.redpatrimonial.ui.components.MensajeVacio
import com.example.redpatrimonial.ui.theme.FondoTarjeta
import com.example.redpatrimonial.ui.theme.TextoSecundario
import com.example.redpatrimonial.ui.theme.VerdePatrimonial
import com.example.redpatrimonial.viewmodel.AdminViewModel

/**
 * CU-25: lista de todas las publicaciones con un interruptor (Switch) por cada una.
 * Un evento oculto deja de aparecer en la pantalla pública de Eventos.
 */
@Composable
fun ModerarScreen(
    navController: NavController,
    adminViewModel: AdminViewModel = viewModel()
) {
    val eventos by adminViewModel.eventos.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Moderar publicaciones",
            subtitulo = "Desactiva el interruptor para ocultar un evento",
            onVolver = { navController.popBackStack() }
        )
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (eventos.isEmpty()) {
                item { MensajeVacio("No hay publicaciones para revisar.") }
            }
            items(eventos) { evento ->
                Card(colors = CardDefaults.cardColors(containerColor = FondoTarjeta)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(evento.titulo, fontWeight = FontWeight.SemiBold)
                            Text(
                                "${evento.tipo} · ${evento.nombreRecinto} · ${evento.fecha}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                            Text(
                                if (evento.visible) "Visible" else "Oculto",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (evento.visible) VerdePatrimonial else TextoSecundario
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Switch(
                            checked = evento.visible,
                            onCheckedChange = { adminViewModel.cambiarVisibilidad(evento.id, it) }
                        )
                    }
                }
            }
        }
    }
}
