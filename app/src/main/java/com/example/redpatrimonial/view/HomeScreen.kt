
package com.example.redpatrimonial.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes

private val AzulMarino = Color(0xFF14263D)
private val Dorado = Color(0xFFD4AF62)
private val FondoCrema = Color(0xFFF7F4ED)

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        containerColor = FondoCrema,
        bottomBar = {
            NavigationBar(
                containerColor = AzulMarino,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Text("⌖", color = Dorado) },
                    label = { Text("Mapa", color = Dorado) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(Routes.BUSCADOR) {
                            launchSingleTop = true
                        }
                    },
                    icon = { Text("⌕", color = Color.White) },
                    label = { Text("Buscador", color = Color.White) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(Routes.QR) {
                            launchSingleTop = true
                        }
                    },
                    icon = { Text("▦", color = Color.White) },
                    label = { Text("QR", color = Color.White) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(Routes.EVENTOS) {
                            launchSingleTop = true
                        }
                    },
                    icon = { Text("▣", color = Color.White) },
                    label = { Text("Eventos", color = Color.White) }
                )

                NavigationBarItem(
                    selected = false,
                    onClick = {
                        navController.navigate(Routes.RUTAS) {
                            launchSingleTop = true
                        }
                    },
                    icon = { Text("↗", color = Color.White) },
                    label = { Text("Rutas", color = Color.White) }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(FondoCrema)
                .padding(16.dp)
        ) {
            Text(
                text = "Red Patrimonial",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = AzulMarino
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Descubre los templos históricos de Santiago",
                color = AzulMarino
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate(Routes.BUSCADOR)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulMarino
                )
            ) {
                Text("Buscar un templo")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Recintos destacados",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AzulMarino
            )

            Spacer(modifier = Modifier.height(8.dp))

            val recintos = listOf(
                "Iglesia de San Francisco",
                "Catedral Metropolitana",
                "Iglesia de Santo Domingo",
                "Basílica de la Merced"
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(recintos) { recinto ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        onClick = {
                            navController.navigate(Routes.DETALLE_RECINTO)
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = recinto,
                                color = AzulMarino,
                                fontWeight = FontWeight.Medium
                            )

                            Text("›", color = Dorado)
                        }
                    }
                }
            }
        }
    }
}
