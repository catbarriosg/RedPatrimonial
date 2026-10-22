
package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes

@Composable
fun BuscadorScreen(navController: NavController) {
    var busqueda by remember { mutableStateOf("") }
    var comuna by remember { mutableStateOf("Todas") }

    val recintos = listOf(
        "Iglesia de San Francisco" to "Santiago",
        "Catedral Metropolitana" to "Santiago",
        "Iglesia de Santo Domingo" to "Santiago",
        "Basílica de la Merced" to "Recoleta"
    )

    val resultados = recintos.filter { (nombre, ubicacion) ->
        nombre.contains(busqueda, ignoreCase = true) &&
                (comuna == "Todas" || comuna == ubicacion)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Buscar recintos", style = MaterialTheme.typography.headlineSmall)
        TextButton(
            onClick = { navController.navigate(Routes.MAPA) }
        ) {
            Text("← Volver al mapa")
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Nombre del recinto") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text("Comuna", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todas", "Santiago", "Recoleta").forEach { opcion ->
                FilterChip(
                    selected = comuna == opcion,
                    onClick = { comuna = opcion },
                    label = { Text(opcion) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Resultados: ${resultados.size}")

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(resultados) { (recinto, _) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(Routes.DETALLE_RECINTO)
                    }
                ) {
                    Text(
                        text = recinto,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
