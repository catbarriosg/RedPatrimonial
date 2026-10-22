package com.example.redpatrimonial.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.redpatrimonial.model.Favorito

@Dao
interface FavoritoDao {

    @Insert
    suspend fun favoritoAlmacenar(favorito: Favorito)

    @Delete
    suspend fun favoritoEliminar(favorito: Favorito)

    @Query("SELECT * FROM favoritos ORDER BY id DESC")
    fun favoritoListar(): Flow<List<Favorito>>
}