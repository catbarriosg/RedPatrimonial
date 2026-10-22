
package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes

@Composable
fun DetalleRecintoScreen(navController: NavController) {
    val fotos = listOf("Fachada", "Interior", "Altar")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Iglesia de San Francisco",
            style = MaterialTheme.typography.headlineSmall
        )

        Text("Monumento Nacional · Neoclásico")
        Text("Av. Libertador Bernardo O'Higgins 834, Santiago Centro")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Reseña histórica", style = MaterialTheme.typography.titleMedium)

        Text(
            "La Iglesia de San Francisco es uno de los templos históricos " +
                    "más importantes de Santiago."
        )
        TextButton(
            onClick = { navController.navigate(Routes.MAPA) }
        ) {
            Text("← Volver al mapa")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Galería de detalles", style = MaterialTheme.typography.titleMedium)

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(fotos) { foto ->
                Card {
                    Text(foto, modifier = Modifier.padding(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Horarios de visita", style = MaterialTheme.typography.titleMedium)
                Text("Lunes a viernes: 09:00 - 18:00")
                Text("Sábados: 10:00 - 14:00")
                Text("Domingos (misa): 11:00 - 13:00")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate(Routes.RUTAS) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver rutas que incluyen este templo")
        }
    }
}
