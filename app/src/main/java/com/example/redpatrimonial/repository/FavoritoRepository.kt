package com.example.redpatrimonial.repository

import com.example.redpatrimonial.dao.FavoritoDao
import com.example.redpatrimonial.model.Favorito
import kotlinx.coroutines.flow.Flow

class FavoritoRepository(
    private val favoritoDao: FavoritoDao
) {
    suspend fun guardar(favorito: Favorito) {
        favoritoDao.favoritoAlmacenar(favorito)
    }

    suspend fun eliminar(favorito: Favorito) {
        favoritoDao.favoritoEliminar(favorito)
    }

    fun listar(): Flow<List<Favorito>> {
        return favoritoDao.favoritoListar()
    }
}