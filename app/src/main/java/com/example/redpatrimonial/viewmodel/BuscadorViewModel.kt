package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.EstadoApertura
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.repository.RecintoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Búsqueda y filtros de recintos (CU-02).
 * Guarda el texto buscado y los filtros elegidos; null en un filtro significa "sin filtrar".
 */
class BuscadorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecintoRepository(
        DatabaseProvider.obtenerBaseDatos(application).recintoDao()
    )

    val recintos: StateFlow<List<Recinto>> =
        repository.listar().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    var busqueda by mutableStateOf("")
        private set
    var comuna by mutableStateOf<String?>(null)
        private set
    var tipo by mutableStateOf<String?>(null)
        private set
    var estado by mutableStateOf<EstadoApertura?>(null)
        private set

    /**
     * Actualiza el texto de búsqueda.
     */
    fun cambiarBusqueda(texto: String) {
        busqueda = texto
    }

    /**
     * Elige una comuna para filtrar. Tocar la comuna que ya estaba elegida quita el filtro.
     */
    fun elegirComuna(opcion: String) {
        comuna = if (comuna == opcion) null else opcion
    }

    /**
     * Elige un tipo de recinto. Tocarlo de nuevo quita el filtro.
     */
    fun elegirTipo(opcion: String) {
        tipo = if (tipo == opcion) null else opcion
    }

    /**
     * Elige un estado de apertura. Tocarlo de nuevo quita el filtro.
     */
    fun elegirEstado(opcion: EstadoApertura) {
        estado = if (estado == opcion) null else opcion
    }

    /**
     * Quita el texto y todos los filtros.
     */
    fun limpiarFiltros() {
        busqueda = ""
        comuna = null
        tipo = null
        estado = null
    }

    /**
     * Devuelve los recintos que cumplen todas las condiciones a la vez (por eso se unen con &&).
     * Cada filtro en null deja pasar todo; contains(..., ignoreCase = true) no distingue mayúsculas.
     */
    fun filtrar(recintos: List<Recinto>): List<Recinto> {
        return recintos.filter { recinto ->
            recinto.nombre.contains(busqueda.trim(), ignoreCase = true) &&
                    (comuna == null || recinto.comuna == comuna) &&
                    (tipo == null || recinto.tipo == tipo) &&
                    (estado == null || recinto.estadoActual == estado)
        }
    }
}
