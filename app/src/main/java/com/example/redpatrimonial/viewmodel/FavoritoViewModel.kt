
package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.EventoConRecinto
import com.example.redpatrimonial.model.Favorito
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.repository.FavoritoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Favoritos del visitante (CU-05). Lo usan el mapa, la ficha, el detalle de evento y la pantalla de favoritos.
 *
 * Las listas se exponen como StateFlow con stateIn:
 * - convierte el Flow de Room en un StateFlow, que siempre tiene un valor actual (initialValue = lista vacía);
 * - WhileSubscribed(5000) mantiene la consulta activa mientras la pantalla la observa
 *   y la detiene 5 segundos después de que nadie la mira (por ejemplo, al rotar no se reinicia).
 * La pantalla lo lee con collectAsState(), que la redibuja cada vez que cambia.
 */
class FavoritoViewModel(application: Application) :
    AndroidViewModel(application) {

    private val favoritoDao = DatabaseProvider
        .obtenerBaseDatos(application)
        .favoritoDao()

    private val repository = FavoritoRepository(favoritoDao)

    val favoritos: StateFlow<List<Favorito>> =
        repository.listar().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recintosFavoritos: StateFlow<List<Recinto>> =
        repository.listarRecintos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val eventosFavoritos: StateFlow<List<EventoConRecinto>> =
        repository.listarEventos().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * true si el recinto está en la lista de favoritos. any { } revisa si algún elemento cumple la condición.
     */
    fun esRecintoFavorito(favoritos: List<Favorito>, idRecinto: Int): Boolean {
        return favoritos.any { it.idRecinto == idRecinto }
    }

    /**
     * true si el evento está en la lista de favoritos.
     */
    fun esEventoFavorito(favoritos: List<Favorito>, idEvento: Int): Boolean {
        return favoritos.any { it.idEvento == idEvento }
    }

    /**
     * Si el recinto es favorito lo quita; si no, lo agrega. Es lo que hace el botón del corazón.
     */
    fun alternarRecinto(idRecinto: Int, esFavorito: Boolean) {
        viewModelScope.launch {
            if (esFavorito) repository.eliminarRecinto(idRecinto) else repository.guardarRecinto(idRecinto)
        }
    }

    /**
     * Lo mismo que alternarRecinto, para un evento.
     */
    fun alternarEvento(idEvento: Int, esFavorito: Boolean) {
        viewModelScope.launch {
            if (esFavorito) repository.eliminarEvento(idEvento) else repository.guardarEvento(idEvento)
        }
    }
}
