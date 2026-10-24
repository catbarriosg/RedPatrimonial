package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.Usuario
import com.example.redpatrimonial.repository.UsuarioRepository
import kotlinx.coroutines.launch

/**
 * CU-15: estado y lógica de la pantalla de login. Valida las credenciales contra la tabla usuarios de Room.
 *
 * Es un AndroidViewModel: recibe la Application, que se necesita para abrir la base de datos.
 * Un ViewModel sobrevive a la rotación de pantalla, así no se pierde lo escrito.
 * mail, pass, textoVisible y cargando son estados que la pantalla muestra.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UsuarioRepository(
        DatabaseProvider.obtenerBaseDatos(application).usuarioDao()
    )

    var mail by mutableStateOf("")
    var pass by mutableStateOf("")

    var textoVisible by mutableStateOf(false)
    var cargando by mutableStateOf(false)
        private set

    /**
     * Actualiza el correo cada vez que el usuario escribe.
     */
    fun cambiarMail(nuevoMail: String) {
        mail = nuevoMail
    }

    /**
     * Actualiza la contraseña cada vez que el usuario escribe.
     */
    fun cambiarPass(nuevaPass: String) {
        pass = nuevaPass
    }

    /**
     * Valida el login.
     * 1. Si falta el correo o la contraseña, muestra el error sin consultar la base.
     * 2. viewModelScope.launch inicia una corrutina ligada al ViewModel (se cancela si el ViewModel se destruye)
     *    y llama a la función suspend del repositorio sin bloquear la pantalla.
     * 3. Si el usuario existe, llama a onExito con el usuario; la pantalla decide a dónde navegar.
     *    Así el ViewModel no necesita conocer el NavController.
     */
    fun login(onExito: (Usuario) -> Unit) {
        textoVisible = false
        if (mail.isBlank() || pass.isBlank()) {
            textoVisible = true
            return
        }
        cargando = true
        viewModelScope.launch {
            val usuario = repository.validar(mail, pass)
            cargando = false
            if (usuario != null) {
                onExito(usuario)
            } else {
                textoVisible = true
            }
        }
    }
}
