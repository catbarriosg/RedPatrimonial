
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes

@Composable
fun RutasScreen(navController: NavController) {
    val rutas = listOf(
        "Ruta de las Tres Basílicas",
        "Tesoros Coloniales del Centro",
        "Circuito Sacro Recoleta"
    )

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
                "Rutas sugeridas",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick = {
                    navController.navigate(Routes.MAPA)
                }
            ) {
                Text("← Volver al mapa")
            }

            Text("Circuitos temáticos diseñados para recorrer a pie")

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(rutas) { ruta ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(Routes.RUTA_PERSONALIZADA)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                ruta,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Santiago Centro")
                            Text("Ver recorrido")
                        }
                    }
                }
            }

            Button(
                onClick = {
                    navController.navigate(Routes.RUTA_PERSONALIZADA)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear ruta personalizada")
            }
        }
    }
}
