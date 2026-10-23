package com.example.redpatrimonial.view

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import com.example.redpatrimonial.ui.theme.RojoError
import com.example.redpatrimonial.viewmodel.LoginViewModel
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val roles = listOf(
        "DOCENTE" to "Docente",
        "ENCARGADO" to "Encargado de templo",
        "GUIA" to "Guía voluntario",
        "ADMINISTRADOR" to "Administrador de red"
    )
    var menuAbierto by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Iniciar sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        ExposedDropdownMenuBox(
            expanded = menuAbierto,
            onExpandedChange = { menuAbierto = !menuAbierto },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = roles.first { it.first == loginViewModel.rolSeleccionado }.second,
                onValueChange = {},
                readOnly = true,
                label = { Text("Selecciona tu rol") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuAbierto)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false }
            ) {
                roles.forEach { (valor, etiqueta) ->
                    DropdownMenuItem(
                        text = { Text(etiqueta) },
                        onClick = {
                            loginViewModel.cambiarRol(valor)
                            menuAbierto = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = loginViewModel.mail,
            onValueChange = { loginViewModel.cambiarMail(it) },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = loginViewModel.pass,
            onValueChange = { loginViewModel.cambiarPass(it) },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(mask = '*'),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { loginViewModel.login(context) },
            enabled = !loginViewModel.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (loginViewModel.cargando) {
                CircularProgressIndicator()
            } else {
                Text("Ingresar")
            }
        }

        if (loginViewModel.textoVisible) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = loginViewModel.mensajeError,
                color = RojoError,
                fontSize = 16.sp
            )
        }

        LaunchedEffect(loginViewModel.usuarioAutenticado) {
            val usuario = loginViewModel.usuarioAutenticado
            if (usuario != null) {
                Toast.makeText(
                    context,
                    "Sesión iniciada: ${usuario.rol}",
                    Toast.LENGTH_SHORT
                ).show()

                navController.navigate(Routes.MAPA) {
                    popUpTo(Routes.INICIO) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }
}
