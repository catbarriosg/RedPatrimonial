
package com.example.redpatrimonial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.redpatrimonial.database.UsuarioInicializador
import com.example.redpatrimonial.navigation.AppNavigation
import com.example.redpatrimonial.ui.theme.RedPatrimonialTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        lifecycleScope.launch {
            UsuarioInicializador.crearUsuariosPrueba(applicationContext)
        }

        setContent {
            RedPatrimonialTheme {
                AppNavigation()
            }
        }
    }
}
