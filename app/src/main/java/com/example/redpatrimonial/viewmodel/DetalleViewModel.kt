package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.AperturaEspecial
import com.example.redpatrimonial.model.Evento
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.repository.EventoRepository
import com.example.redpatrimonial.repository.RecintoRepository
import kotlinx.coroutines.launch

/**
 * Datos de la ficha de un recinto (CU-03 y CU-04): el recinto, sus eventos y sus aperturas especiales.
 */
class DetalleViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = DatabaseProvider.obtenerBaseDatos(application)
    private val recintoRepository = RecintoRepository(baseDatos.recintoDao())
    private val eventoRepository = EventoRepository(baseDatos.eventoDao())

    var recinto by mutableStateOf<Recinto?>(null)
        private set
    var eventos by mutableStateOf<List<Evento>>(emptyList())
        private set
    var aperturas by mutableStateOf<List<AperturaEspecial>>(emptyList())
        private set

    /**
     * Carga los datos del recinto cuyo id llegó por la navegación. La pantalla la llama una vez con LaunchedEffect.
     * Lanza tres corrutinas, una por consulta. collect { } recibe cada valor que emite el Flow,
     * así la ficha se actualiza sola si el encargado cambia algo.
     */
    fun cargar(idRecinto: Int) {
        viewModelScope.launch {
            recintoRepository.obtener(idRecinto).collect { recinto = it }
        }
        viewModelScope.launch {
            eventoRepository.listarPorRecinto(idRecinto).collect { eventos = it }
        }
        viewModelScope.launch {
            recintoRepository.listarAperturas(idRecinto).collect { aperturas = it }
        }
    }
}
