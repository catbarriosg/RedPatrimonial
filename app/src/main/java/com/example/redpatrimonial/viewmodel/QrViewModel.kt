package com.example.redpatrimonial.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class QrViewModel : ViewModel() {

    private val _contenidoQR = MutableStateFlow("")
    val contenidoQR: StateFlow<String> = _contenidoQR

    fun guardarQR(contenido: String) {
        _contenidoQR.value = contenido
    }
}