package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.model.Ruta
import com.example.redpatrimonial.model.TipoRuta
import com.example.redpatrimonial.repository.RecintoRepository
import com.example.redpatrimonial.repository.RutaRepository
import com.example.redpatrimonial.util.CalculadoraTiempos
import com.example.redpatrimonial.util.ResumenRecorrido
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Armado de rutas: lo usan la ruta personalizada (CU-09) y las rutas oficiales del administrador (CU-26).
 * seleccionados es una lista observable (mutableStateListOf) con las paradas elegidas en orden:
 * cuando se agrega, quita o mueve una parada, Compose redibuja la lista y el cálculo.
 */
class RutaPersonalizadaViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = DatabaseProvider.obtenerBaseDatos(application)
    private val rutaRepository = RutaRepository(baseDatos.rutaDao())
    private val recintoRepository = RecintoRepository(baseDatos.recintoDao())

    val recintos: StateFlow<List<Recinto>> =
        recintoRepository.listar().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Paradas elegidas, en el orden del recorrido
    val seleccionados = mutableStateListOf<Recinto>()

    var nombre by mutableStateOf("")
    var descripcion by mutableStateOf("")
    var mensajeError by mutableStateOf<String?>(null)
        private set

    /**
     * Agrega el recinto al recorrido o lo quita si ya estaba.
     */
    fun alternar(recinto: Recinto) {
        if (seleccionados.any { it.id == recinto.id }) {
            seleccionados.removeAll { it.id == recinto.id }
        } else {
            seleccionados.add(recinto)
        }
    }

    /**
     * true si el recinto ya está en el recorrido; marca el checkbox.
     */
    fun estaSeleccionado(recinto: Recinto): Boolean {
        return seleccionados.any { it.id == recinto.id }
    }

    /**
     * Mueve una parada una posición hacia arriba: la saca de la lista y la inserta en la posición anterior.
     */
    fun subir(indice: Int) {
        if (indice > 0) {
            val recinto = seleccionados.removeAt(indice)
            seleccionados.add(indice - 1, recinto)
        }
    }

    /**
     * Mueve una parada una posición hacia abajo.
     */
    fun bajar(indice: Int) {
        if (indice < seleccionados.size - 1) {
            val recinto = seleccionados.removeAt(indice)
            seleccionados.add(indice + 1, recinto)
        }
    }

    /**
     * CU-10: distancia y tiempo del recorrido. Se recalcula cada vez que cambian las paradas.
     */
    fun resumen(): ResumenRecorrido {
        return CalculadoraTiempos.calcular(seleccionados)
    }

    /**
     * Valida y guarda la ruta con sus paradas.
     * tipo indica si es OFICIAL o PERSONALIZADA. Si no se escribió descripción, se usan los nombres de las paradas.
     * Al terminar llama a onGuardada con el id nuevo, para que la pantalla navegue al detalle de la ruta.
     */
    fun guardar(tipo: TipoRuta, idUsuario: Int?, onGuardada: (Int) -> Unit) {
        when {
            seleccionados.size < 2 -> mensajeError = "Elige al menos dos recintos para armar la ruta"
            nombre.isBlank() -> mensajeError = "Ponle un nombre a la ruta"
            else -> {
                mensajeError = null
                val resumen = resumen()
                val ruta = Ruta(
                    nombre = nombre.trim(),
                    descripcion = descripcion.trim().ifBlank { seleccionados.joinToString(" · ") { it.nombre } },
                    tipo = tipo,
                    distanciaKm = resumen.distanciaKm,
                    duracionMin = resumen.duracionTotalMin,
                    idUsuario = idUsuario
                )
                val ids = seleccionados.map { it.id }
                viewModelScope.launch {
                    val idRuta = rutaRepository.guardar(ruta, ids)
                    seleccionados.clear()
                    nombre = ""
                    descripcion = ""
                    onGuardada(idRuta)
                }
            }
        }
    }
}
