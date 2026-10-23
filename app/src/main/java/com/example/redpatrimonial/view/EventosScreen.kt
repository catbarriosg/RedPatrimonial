
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
fun EventosScreen(navController: NavController) {
    val eventos = listOf(
        "Misa Coral de San Francisco",
        "Concierto de Órgano Barroco",
        "Visita Nocturna Guiada"
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
                "Eventos cercanos",
                style = MaterialTheme.typography.headlineSmall
            )

            TextButton(
                onClick = {
                    navController.navigate(Routes.MAPA)
                }
            ) {
                Text("← Volver al mapa")
            }

            Text("Actividades litúrgicas, musicales y culturales")

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(eventos) { evento ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(Routes.DETALLE_RECINTO)
                        }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                evento,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text("Santiago Centro")
                            Text("Ver detalles")
                        }
                    }
                }
            }
        }
    }
}
