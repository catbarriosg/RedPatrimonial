
package com.example.redpatrimonial.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Church
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.theme.*

/**
 * Pantalla de bienvenida: entrar al mapa como visitante o iniciar sesión.
 * Al entrar al mapa usa popUpTo(INICIO) { inclusive = true } para quitar Inicio del historial:
 * así "atrás" desde el mapa cierra la app en vez de volver a esta pantalla.
 */
@Composable
fun InicioScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdePatrimonial)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(2.dp, DoradoAntiguo, CircleShape)
                .background(VerdeContenedorOscuro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Church, contentDescription = null, tint = DoradoAntiguo, modifier = Modifier.size(56.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "RED PATRIMONIAL",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = DoradoAntiguo,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Red de Patrimonio Histórico de la Arquidiócesis de Santiago",
            style = MaterialTheme.typography.bodyLarge,
            color = Marfil,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = {
                navController.navigate(Routes.MAPA) {
                    popUpTo(Routes.INICIO) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DoradoAntiguo, contentColor = TextoPrincipal)
        ) {
            Text("Ingresar al mapa", fontWeight = FontWeight.SemiBold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate(Routes.LOGIN) }) {
            Text("Soy docente, encargado o guía: iniciar sesión", color = Marfil)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "MVP académico · Datos ficticios",
            fontSize = 12.sp,
            color = TextoClaroSecundario
        )
    }
}
