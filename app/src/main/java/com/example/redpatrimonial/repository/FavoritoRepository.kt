package com.example.redpatrimonial.repository

import com.example.redpatrimonial.dao.FavoritoDao
import com.example.redpatrimonial.model.EventoConRecinto
import com.example.redpatrimonial.model.Favorito
import com.example.redpatrimonial.model.Recinto
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de favoritos. Construye el objeto Favorito para que el ViewModel solo entregue ids.
 */
class FavoritoRepository(
    private val favoritoDao: FavoritoDao
) {
    /**
     * Marca un recinto como favorito.
     */
    suspend fun guardarRecinto(idRecinto: Int) {
        favoritoDao.favoritoAlmacenar(Favorito(idRecinto = idRecinto))
    }

    /**
     * Marca un evento como favorito.
     */
    suspend fun guardarEvento(idEvento: Int) {
        favoritoDao.favoritoAlmacenar(Favorito(idEvento = idEvento))
    }

    /**
     * Quita un recinto de favoritos.
     */
    suspend fun eliminarRecinto(idRecinto: Int) {
        favoritoDao.favoritoEliminarRecinto(idRecinto)
    }

    /**
     * Quita un evento de favoritos.
     */
    suspend fun eliminarEvento(idEvento: Int) {
        favoritoDao.favoritoEliminarEvento(idEvento)
    }

    /**
     * Todos los favoritos.
     */
    fun listar(): Flow<List<Favorito>> {
        return favoritoDao.favoritoListar()
    }

    /**
     * Recintos favoritos.
     */
    fun listarRecintos(): Flow<List<Recinto>> {
        return favoritoDao.favoritoListarRecintos()
    }

    /**
     * Eventos favoritos.
     */
    fun listarEventos(): Flow<List<EventoConRecinto>> {
        return favoritoDao.favoritoListarEventos()
    }
}
