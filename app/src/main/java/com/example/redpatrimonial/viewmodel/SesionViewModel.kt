package com.example.redpatrimonial.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.redpatrimonial.model.Usuario

/**
 * Guarda el usuario con sesión iniciada. El visitante navega sin sesión (usuario = null).
 * Se crea una sola vez en AppNavigation y se pasa a las pantallas que lo necesitan.
 * - mutableStateOf crea un estado observable: cuando cambia, Compose redibuja lo que lo usa.
 * - "by" (delegado) permite leer y escribir usuario directamente, sin .value.
 * - private set: solo este ViewModel puede cambiarlo; las pantallas solo lo leen.
 */
class SesionViewModel : ViewModel() {

    var usuario by mutableStateOf<Usuario?>(null)
        private set

    /**
     * Guarda el usuario que validó el login.
     */
    fun iniciar(usuarioValidado: Usuario) {
        usuario = usuarioValidado
    }

    /**
     * Borra el usuario: la app vuelve a modo visitante.
     */
    fun cerrar() {
        usuario = null
    }
}
