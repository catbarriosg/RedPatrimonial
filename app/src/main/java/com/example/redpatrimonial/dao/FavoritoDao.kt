package com.example.redpatrimonial.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.redpatrimonial.model.EventoConRecinto
import com.example.redpatrimonial.model.Favorito
import com.example.redpatrimonial.model.Recinto
import kotlinx.coroutines.flow.Flow

/**
 * DAO de favoritos (CU-05). Los favoritos quedan guardados en el teléfono aunque se cierre la app.
 */
@Dao
interface FavoritoDao {

    /**
     * Guarda un favorito nuevo.
     */
    @Insert
    suspend fun favoritoAlmacenar(favorito: Favorito)

    /**
     * Quita de favoritos un recinto. Se borra por idRecinto porque la pantalla conoce el recinto, no el id del favorito.
     */
    @Query("DELETE FROM favoritos WHERE idRecinto = :idRecinto")
    suspend fun favoritoEliminarRecinto(idRecinto: Int)

    /**
     * Quita de favoritos un evento.
     */
    @Query("DELETE FROM favoritos WHERE idEvento = :idEvento")
    suspend fun favoritoEliminarEvento(idEvento: Int)

    /**
     * Todos los favoritos. Se usa para saber si el corazón de una ficha debe aparecer marcado.
     */
    @Query("SELECT * FROM favoritos ORDER BY id DESC")
    fun favoritoListar(): Flow<List<Favorito>>

    /**
     * Los recintos guardados como favoritos. El INNER JOIN deja solo los recintos que tienen un favorito.
     */
    @Query("SELECT r.* FROM recintos r INNER JOIN favoritos f ON f.idRecinto = r.id ORDER BY f.id DESC")
    fun favoritoListarRecintos(): Flow<List<Recinto>>

    /**
     * Los eventos guardados como favoritos, con los datos de su recinto (dos JOIN).
     */
    @Query(
        """
        SELECT e.id, e.idRecinto, e.titulo, e.tipo, e.descripcion, e.fecha, e.hora, e.visible,
               r.nombre AS nombreRecinto, r.latitud, r.longitud
        FROM eventos e
        INNER JOIN favoritos f ON f.idEvento = e.id
        INNER JOIN recintos r ON r.id = e.idRecinto
        ORDER BY f.id DESC
        """
    )
    fun favoritoListarEventos(): Flow<List<EventoConRecinto>>
}
