
package com.example.redpatrimonial.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes

private val AzulMarino = Color(0xFF14263D)
private val Dorado = Color(0xFFD4AF62)
private val Fondo = Color(0xFFF7F4ED)

@Composable
fun InicioScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulMarino)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "RP",
            color = Dorado,
            fontSize = 54.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "RED PATRIMONIAL",
            color = Color.White,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        HorizontalDivider(
            modifier = Modifier.width(100.dp),
            color = Dorado,
            thickness = 2.dp
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Descubre el patrimonio religioso y cultural de Santiago",
            color = Fondo,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = {
                navController.navigate(Routes.LOGIN)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Dorado,
                contentColor = AzulMarino
            )
        ) {
            Text(
                text = "INGRESAR AL MAPA",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
