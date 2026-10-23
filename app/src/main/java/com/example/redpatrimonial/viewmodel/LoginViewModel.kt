package com.example.redpatrimonial.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.Usuario
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    var mail by mutableStateOf("")
    var pass by mutableStateOf("")
    var rolSeleccionado by mutableStateOf("DOCENTE")
    var textoVisible by mutableStateOf(false)
    var cargando by mutableStateOf(false)
    var usuarioAutenticado by mutableStateOf<Usuario?>(null)

    var mensajeError by mutableStateOf("")

    fun cambiarMail(nuevoMail: String) {
        mail = nuevoMail
    }

    fun cambiarPass(nuevaPass: String) {
        pass = nuevaPass
    }

    fun cambiarRol(nuevoRol: String) {
        rolSeleccionado = nuevoRol
    }

    fun login(context: android.content.Context) {
        if (cargando) return

        cargando = true
        textoVisible = false
        mensajeError = ""

        viewModelScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {
                    DatabaseProvider
                        .obtenerBaseDatos(context)
                        .usuarioDao()
                        .buscarPorMail(mail.trim())
                }

                if (
                    usuario != null &&
                    usuario.pass == pass &&
                    usuario.rol == rolSeleccionado
                ) {
                    usuarioAutenticado = usuario
                    textoVisible = false
                } else {
                    usuarioAutenticado = null
                    mensajeError = "Credenciales o rol incorrectos"
                    textoVisible = true
                }
            } catch (e: Exception) {
                usuarioAutenticado = null
                mensajeError = "No se pudo consultar la base de datos"
                textoVisible = true
            } finally {
                cargando = false
            }
        }
    }
}
