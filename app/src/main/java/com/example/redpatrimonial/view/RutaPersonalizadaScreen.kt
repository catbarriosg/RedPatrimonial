
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
fun RutaPersonalizadaScreen(navController: NavController) {
    val recintos = listOf(
        "Iglesia de San Francisco",
        "Catedral Metropolitana",
        "Iglesia de Santo Domingo",
        "Basílica de la Merced"
    )

    val seleccionados = remember { mutableStateListOf<String>() }
    var mostrarRuta by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextButton(
            onClick = { navController.navigate(Routes.RUTAS) }
        ) {
            Text("← Volver a rutas")
        }

        Text(
            "Crear ruta personalizada",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Selecciona los recintos que deseas visitar")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(recintos) { recinto ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            recinto,
                            modifier = Modifier.weight(1f)
                        )

                        Checkbox(
                            checked = recinto in seleccionados,
                            onCheckedChange = { seleccionado ->
                                if (seleccionado) {
                                    if (recinto !in seleccionados) {
                                        seleccionados.add(recinto)
                                    }
                                } else {
                                    seleccionados.remove(recinto)
                                }
                                mostrarRuta = false
                            }
                        )
                    }
                }
            }
        }

        Text("Recintos seleccionados: ${seleccionados.size}")

        Button(
            onClick = { mostrarRuta = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = seleccionados.isNotEmpty()
        ) {
            Text("Calcular recorrido")
        }

        if (mostrarRuta) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Tu recorrido",
                style = MaterialTheme.typography.titleLarge
            )

            seleccionados.forEachIndexed { index, recinto ->
                Text("${index + 1}. $recinto")
            }

            Text("Total de recintos: ${seleccionados.size}")
            Text("Tiempo estimado: por calcular")
        }
    }
}
