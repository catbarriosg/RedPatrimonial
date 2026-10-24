package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.repository.RecintoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Estado de la pantalla del mapa (CU-01): lista de recintos y recinto seleccionado.
 * idSeleccionado es el recinto que se tocó en el mapa; se muestra en la tarjeta inferior.
 */
class MapaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RecintoRepository(
        DatabaseProvider.obtenerBaseDatos(application).recintoDao()
    )

    val recintos: StateFlow<List<Recinto>> =
        repository.listar().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Recinto tocado en el mapa; se muestra en la tarjeta inferior
    var idSeleccionado by mutableStateOf<Int?>(null)
        private set

    /**
     * Marca un recinto como seleccionado al tocar su marcador.
     */
    fun seleccionar(id: Int) {
        idSeleccionado = id
    }
}
