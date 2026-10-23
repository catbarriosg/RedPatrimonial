package com.example.redpatrimonial.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `usuarios` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `mail` TEXT NOT NULL,
                    `pass` TEXT NOT NULL,
                    `rol` TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_mail ON usuarios(mail)"
            )
        }
    }

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun obtenerBaseDatos(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "red_patrimonial_db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
                .also { INSTANCE = it }
        }
    }
}
