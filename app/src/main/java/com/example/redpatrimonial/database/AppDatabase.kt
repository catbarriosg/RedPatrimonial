package com.example.redpatrimonial.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.redpatrimonial.dao.FavoritoDao
import com.example.redpatrimonial.dao.UsuarioDao
import com.example.redpatrimonial.model.Favorito
import com.example.redpatrimonial.model.Usuario

@Database(
    entities = [
        Favorito::class,
        Usuario::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritoDao(): FavoritoDao

    abstract fun usuarioDao(): UsuarioDao
}
