
package com.example.redpatrimonial.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.redpatrimonial.dao.FavoritoDao
import com.example.redpatrimonial.model.Favorito

@Database(
    entities = [Favorito::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoritoDao(): FavoritoDao
}
