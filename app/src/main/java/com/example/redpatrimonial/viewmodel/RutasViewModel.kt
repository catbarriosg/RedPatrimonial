package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.ParadaRuta
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.model.Ruta
import com.example.redpatrimonial.repository.RecintoRepository
import com.example.redpatrimonial.repository.RutaRepository
import com.example.redpatrimonial.repository.VisitaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Una ruta junto con los recintos de sus paradas, en orden. Es lo que muestran las pantallas de rutas.
 */
data class RutaConParadas(
    val ruta: Ruta,
    val paradas: List<Recinto>
)

/**
 * CU-08: rutas sugeridas, rutas personalizadas y detalle de cada ruta.
 * combine junta tres Flows (rutas, paradas y recintos) en uno: cada vez que cualquiera cambia,
 * vuelve a armar la lista de RutaConParadas.
 * recintosVisitados guarda los ids de los recintos donde ya se escaneó el código QR.
 */
class RutasViewModel(application: Application) : AndroidViewModel(application) {

    private val baseDatos = DatabaseProvider.obtenerBaseDatos(application)
    private val rutaRepository = RutaRepository(baseDatos.rutaDao())
    private val recintoRepository = RecintoRepository(baseDatos.recintoDao())
    private val visitaRepository = VisitaRepository(baseDatos.visitaDao())

    val rutasOficiales: StateFlow<List<RutaConParadas>> =
        combine(
            rutaRepository.listarOficiales(),
            rutaRepository.listarTodasLasParadas(),
            recintoRepository.listar()
        ) { rutas, paradas, recintos -> armar(rutas, paradas, recintos) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rutasPersonalizadas: StateFlow<List<RutaConParadas>> =
        combine(
            rutaRepository.listarPersonalizadas(),
            rutaRepository.listarTodasLasParadas(),
            recintoRepository.listar()
        ) { rutas, paradas, recintos -> armar(rutas, paradas, recintos) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var rutaDetalle by mutableStateOf<RutaConParadas?>(null)
        private set

    // IDs de los recintos donde ya se escaneó el código QR
    var recintosVisitados by mutableStateOf<Set<Int>>(emptySet())
        private set

    /**
     * Para cada ruta: filtra sus paradas, las ordena y busca el recinto de cada una.
     * mapNotNull descarta las paradas cuyo recinto no se encontró.
     */
    private fun armar(rutas: List<Ruta>, paradas: List<ParadaRuta>, recintos: List<Recinto>): List<RutaConParadas> {
        return rutas.map { ruta ->
            val recintosDeRuta = paradas
                .filter { it.idRuta == ruta.id }
                .sortedBy { it.orden }
                .mapNotNull { parada -> recintos.find { it.id == parada.idRecinto } }
            RutaConParadas(ruta, recintosDeRuta)
        }
    }

    /**
     * Deja solo las rutas que pasan por un recinto. Si idRecinto es null devuelve todas.
     */
    fun filtrarPorRecinto(rutas: List<RutaConParadas>, idRecinto: Int?): List<RutaConParadas> {
        if (idRecinto == null) return rutas
        return rutas.filter { ruta -> ruta.paradas.any { it.id == idRecinto } }
    }

    /**
     * Carga una ruta con sus paradas y las visitas registradas por QR, para marcar las paradas visitadas.
     * toSet() convierte la lista de ids en un conjunto, que permite preguntar "id in recintosVisitados".
     */
    fun cargarDetalle(idRuta: Int) {
        viewModelScope.launch {
            combine(
                rutaRepository.obtener(idRuta),
                rutaRepository.listarParadas(idRuta),
                recintoRepository.listar()
            ) { ruta, paradas, recintos ->
                if (ruta == null) null else armar(listOf(ruta), paradas, recintos).first()
            }.collect { rutaDetalle = it }
        }
        viewModelScope.launch {
            visitaRepository.listarVisitasQr().collect { visitas ->
                recintosVisitados = visitas.map { it.idRecinto }.toSet()
            }
        }
    }

    /**
     * Elimina una ruta personalizada o una ruta oficial.
     */
    fun eliminar(idRuta: Int) {
        viewModelScope.launch {
            rutaRepository.eliminar(idRuta)
        }
    }
}
