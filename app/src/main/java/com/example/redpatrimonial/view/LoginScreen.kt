package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.components.BotonPrincipal
import com.example.redpatrimonial.ui.components.Encabezado
import com.example.redpatrimonial.ui.theme.DoradoClaro
import com.example.redpatrimonial.ui.theme.RojoError
import com.example.redpatrimonial.ui.theme.TextoSecundario
import com.example.redpatrimonial.viewmodel.LoginViewModel
import com.example.redpatrimonial.viewmodel.SesionViewModel

/**
 * CU-15: inicio de sesión.
 * - PasswordVisualTransformation oculta la contraseña con asteriscos.
 * - Al validar, guarda el usuario en SesionViewModel y navega al panel de su rol.
 *   popUpTo(LOGIN) { inclusive = true } quita el login del historial para que "atrás" no regrese aquí.
 * - La tarjeta de usuarios de prueba ayuda a la demostración; en una app real no existiría.
 */
@Composable
fun LoginScreen(
    navController: NavController,
    sesionViewModel: SesionViewModel,
    loginViewModel: LoginViewModel = viewModel()
){
    Column(modifier = Modifier.fillMaxSize()) {
        Encabezado(
            titulo = "Iniciar sesión",
            subtitulo = "Para docentes, encargados de templo, guías y administradores",
            onVolver = { navController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = loginViewModel.mail,
                onValueChange = {
                    loginViewModel.cambiarMail(it)
                },
                label = {
                    Text("Correo electrónico")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = loginViewModel.pass,
                onValueChange = {
                    loginViewModel.cambiarPass(it)
                },
                label = {
                    Text("Contraseña")
                },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(mask = '*'),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            if (loginViewModel.textoVisible) {
                Text(
                    text = "Credenciales inválidas. Revisa el correo y la contraseña.",
                    color = RojoError
                )
            }

            BotonPrincipal(
                texto = if (loginViewModel.cargando) "Validando…" else "Ingresar",
                enabled = !loginViewModel.cargando,
                onClick = {
                    loginViewModel.login { usuario ->
                        sesionViewModel.iniciar(usuario)
                        // Se reemplaza el login por el panel del rol para que "volver" no regrese aquí
                        navController.navigate(Routes.panelSegunRol(usuario.rol)) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                }
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = DoradoClaro),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Usuarios de prueba (contraseña 1234)", fontWeight = FontWeight.SemiBold)
                    listOf(
                        "docente@demo.cl · Docente",
                        "encargado@demo.cl · Encargado San Francisco",
                        "encargado2@demo.cl · Encargado La Merced",
                        "guia@demo.cl · Guía voluntario",
                        "admin@demo.cl · Administrador de red"
                    ).forEach { cuenta ->
                        Text(cuenta, style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                    }
                }
            }
        }
    }
}
