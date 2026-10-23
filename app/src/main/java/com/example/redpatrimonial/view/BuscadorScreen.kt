
package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

    Scaffold(
        bottomBar = {
            BarraNavegacion(navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Buscar recintos",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick = {
                    navController.navigate(Routes.MAPA)
                }
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

            Text(
                "Comuna",
                style = MaterialTheme.typography.titleMedium
            )

            Column {
                androidx.compose.foundation.layout.Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Todas", "Santiago", "Recoleta").forEach { opcion ->
                        FilterChip(
                            selected = comuna == opcion,
                            onClick = { comuna = opcion },
                            label = { Text(opcion) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Resultados: ${resultados.size}")

            LazyColumn(
                modifier = Modifier.weight(1f),
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
}
