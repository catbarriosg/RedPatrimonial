package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.EventoConRecinto
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.model.Rol
import com.example.redpatrimonial.model.Usuario
import com.example.redpatrimonial.repository.EventoRepository
import com.example.redpatrimonial.repository.RecintoRepository
import com.example.redpatrimonial.repository.UsuarioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Administración de la red: registrar nodos (CU-24) y moderar publicaciones (CU-25).
 * Usa repositorios de las dos personas: recintos y eventos (A) y usuarios (B, para listar encargados).
 */
class AdminViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = DatabaseProvider.obtenerBaseDatos(application)
    private val recintoRepository = RecintoRepository(baseDatos.recintoDao())
    private val eventoRepository = EventoRepository(baseDatos.eventoDao())
    private val usuarioRepository = UsuarioRepository(baseDatos.usuarioDao())

    val recintos: StateFlow<List<Recinto>> =
        recintoRepository.listar().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val eventos: StateFlow<List<EventoConRecinto>> =
        eventoRepository.listarTodos().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val encargados: StateFlow<List<Usuario>> =
        usuarioRepository.listarPorRol(Rol.ENCARGADO).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tiposRecinto = listOf("Iglesia", "Catedral", "Basílica", "Capilla", "Santuario", "Museo", "Colegio", "Archivo")

    // Formulario: registrar nodo
    var nombre by mutableStateOf("")
    var tipo by mutableStateOf("Iglesia")
    var comuna by mutableStateOf("")
    var direccion by mutableStateOf("")
    var latitud by mutableStateOf("")
    var longitud by mutableStateOf("")
    var siglo by mutableStateOf("")
    var resena by mutableStateOf("")
    var idEncargado by mutableStateOf<Int?>(null)

    var mensajeError by mutableStateOf<String?>(null)
        private set

    /**
     * CU-24: valida y registra un recinto nuevo. Devuelve true si se registró.
     * replace(",", ".") acepta coordenadas escritas con coma; toDoubleOrNull devuelve null si no es un número.
     * "lat !in -90.0..90.0" revisa que el número esté fuera del rango permitido.
     */
    fun registrarNodo(): Boolean {
        val lat = latitud.replace(",", ".").toDoubleOrNull()
        val lng = longitud.replace(",", ".").toDoubleOrNull()
        when {
            nombre.isBlank() || comuna.isBlank() || direccion.isBlank() ->
                mensajeError = "Nombre, comuna y dirección son obligatorios"
            lat == null || lng == null || lat !in -90.0..90.0 || lng !in -180.0..180.0 ->
                mensajeError = "Las coordenadas deben ser números válidos, por ejemplo -33.4378 y -70.6504"
            resena.isBlank() ->
                mensajeError = "Agrega una reseña histórica"
            else -> {
                viewModelScope.launch {
                    recintoRepository.registrar(
                        Recinto(
                            nombre = nombre.trim(),
                            tipo = tipo,
                            comuna = comuna.trim(),
                            direccion = direccion.trim(),
                            latitud = lat,
                            longitud = lng,
                            categoria = "Nodo de la red",
                            estilo = "Sin información",
                            siglo = siglo.trim().ifBlank { "—" },
                            resenaHistorica = resena.trim(),
                            horarioSemana = "Por definir",
                            horarioSabado = "Por definir",
                            horarioDomingo = "Por definir",
                            idEncargado = idEncargado
                        )
                    )
                }
                mensajeError = null
                return true
            }
        }
        return false
    }

    /**
     * CU-25: muestra u oculta un evento. Se llama al mover el interruptor.
     */
    fun cambiarVisibilidad(idEvento: Int, visible: Boolean) {
        viewModelScope.launch {
            eventoRepository.cambiarVisibilidad(idEvento, visible)
        }
    }
}
