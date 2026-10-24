package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.EventoConRecinto
import com.example.redpatrimonial.repository.EventoRepository
import com.example.redpatrimonial.util.CalculadoraTiempos
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Eventos cercanos (CU-06) y detalle de un evento.
 * La lista de eventos se ordena con map + sortedBy por distancia a la ubicación simulada del usuario:
 * map transforma cada lista que emite Room antes de entregarla a la pantalla.
 */
class EventosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EventoRepository(
        DatabaseProvider.obtenerBaseDatos(application).eventoDao()
    )

    val tipos = listOf("Liturgia", "Concierto", "Exposición", "Tour cultural", "Aviso")

    // CU-06: los eventos se ordenan del más cercano al más lejano
    val eventos: StateFlow<List<EventoConRecinto>> =
        repository.listarVisibles()
            .map { lista -> lista.sortedBy { distanciaKm(it) } }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    var tipoFiltro by mutableStateOf<String?>(null)
        private set

    var eventoDetalle by mutableStateOf<EventoConRecinto?>(null)
        private set

    /**
     * Filtra por tipo de evento. Tocar el mismo tipo de nuevo quita el filtro.
     */
    fun elegirTipo(tipo: String) {
        tipoFiltro = if (tipoFiltro == tipo) null else tipo
    }

    /**
     * Devuelve los eventos del tipo elegido, o todos si no hay filtro.
     */
    fun filtrar(eventos: List<EventoConRecinto>): List<EventoConRecinto> {
        return eventos.filter { tipoFiltro == null || it.tipo == tipoFiltro }
    }

    /**
     * Distancia desde el usuario hasta el recinto del evento.
     */
    fun distanciaKm(evento: EventoConRecinto): Double {
        return CalculadoraTiempos.distanciaDesdeUsuarioKm(evento.latitud, evento.longitud)
    }

    /**
     * Carga un evento para la pantalla de detalle.
     */
    fun cargarDetalle(idEvento: Int) {
        viewModelScope.launch {
            repository.obtener(idEvento).collect { eventoDetalle = it }
        }
    }
}
