
package com.example.redpatrimonial.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.redpatrimonial.database.DatabaseProvider
import com.example.redpatrimonial.model.Favorito
import com.example.redpatrimonial.repository.FavoritoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    fun guardar(favorito: Favorito) {
        viewModelScope.launch {
            repository.guardar(favorito)
        }
    }

    fun eliminar(favorito: Favorito) {
        viewModelScope.launch {
            repository.eliminar(favorito)
        }
    }
}
