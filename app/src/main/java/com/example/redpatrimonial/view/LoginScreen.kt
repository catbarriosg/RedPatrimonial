package com.example.redpatrimonial.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redpatrimonial.viewmodel.LoginViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.navigation.NavController
import com.example.redpatrimonial.navigation.Routes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import com.example.redpatrimonial.ui.theme.RojoError

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel = viewModel()
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Iniciar Sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        OutlinedTextField(
            value = loginViewModel.mail,
            onValueChange = {
                loginViewModel.cambiarMail(it)
            },
            label = {
                Text("Correo Electrónico")
            },
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
            visualTransformation = PasswordVisualTransformation(mask = '*'),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                loginViewModel.textoVisible = false
                val validador = loginViewModel.login()

                if(validador){

                    navController.navigate(Routes.INICIO) {
                        popUpTo(Routes.INICIO) { inclusive = true }
                    }
                }else{
                    loginViewModel.textoVisible = true
                }

            },
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth(),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(text = "Ingresar")
        }
        Spacer(
            modifier = Modifier.height(20.dp)
        )

        if(loginViewModel.textoVisible) {
            Text(
                text = "Credenciales invalidas",
                fontSize = 20.sp,
                color = RojoError
            )
        }
    }

}