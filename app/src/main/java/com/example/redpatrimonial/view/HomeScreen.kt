
package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate(Routes.MAPA) },
                    icon = { Text("🗺️") },
                    label = { Text("Mapa") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Routes.BUSCADOR) },
                    icon = { Text("🔎") },
                    label = { Text("Buscador") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Routes.EVENTOS) },
                    icon = { Text("📅") },
                    label = { Text("Eventos") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Routes.RUTAS) },
                    icon = { Text("🧭") },
                    label = { Text("Rutas") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                "Mapa patrimonial",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("Explora los templos históricos de Santiago")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate(Routes.BUSCADOR) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Buscar un templo")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Recintos destacados",
                style = MaterialTheme.typography.titleMedium
            )

            listOf(
                "Iglesia de San Francisco",
                "Catedral Metropolitana",
                "Iglesia de Santo Domingo",
                "Basílica de la Merced"
            ).forEach { recinto ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    onClick = {
                        navController.navigate(Routes.DETALLE_RECINTO)
                    }
                ) {
                    Text(
                        recinto,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Mapa interactivo pendiente de integración",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
