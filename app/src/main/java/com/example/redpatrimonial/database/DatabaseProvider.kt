
package com.example.redpatrimonial.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.redpatrimonial.database.seed.SeedExploracion
import com.example.redpatrimonial.database.seed.SeedRutasVisitas
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Entrega una única instancia de la base de datos para toda la app (patrón Singleton).
 * Es un object: Kotlin crea un solo objeto, sin necesidad de instanciarlo.
 * La anotación @Volatile hace que todos los hilos vean el valor actualizado de INSTANCE.
 */
object DatabaseProvider {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    /**
     * Devuelve la base de datos; la crea solo la primera vez.
     * - "INSTANCE ?: ..." usa la instancia existente o, si es null, entra a crearla.
     * - synchronized evita que dos hilos la creen al mismo tiempo.
     * - fallbackToDestructiveMigration(true): si cambia la versión, borra la base y la vuelve a crear.
     *   Sirve en desarrollo; en producción se escribirían migraciones para no perder datos.
     * - addCallback agrega PoblarBaseDatos, que carga los datos de prueba.
     * - also { INSTANCE = it } guarda la base recién creada para reutilizarla.
     */
    fun obtenerBaseDatos(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "red_patrimonial_db"
            )
                // En desarrollo, si cambia la versión se borra la base y se vuelve a poblar
                .fallbackToDestructiveMigration(true)
                .addCallback(PoblarBaseDatos(context.applicationContext))
                .build().also {
                    INSTANCE = it
                }
        }
    }

    /**
     * Callback de Room que carga los datos sintéticos la primera vez que se crea la base de datos.
     */
    private class PoblarBaseDatos(private val context: Context) : RoomDatabase.Callback() {

        /**
         * Room la llama una sola vez, cuando crea el archivo de la base por primera vez.
         */
        override fun onCreate(db: SupportSQLiteDatabase) {
            poblar()
        }

        /**
         * Room la llama cuando borró la base por un cambio de versión; se vuelve a poblar.
         */
        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            poblar()
        }

        /**
         * Inserta los datos de prueba en segundo plano.
         * CoroutineScope(Dispatchers.IO).launch inicia una corrutina en un hilo para entrada y salida,
         * porque Room no permite escribir en la base desde el hilo principal (el de la pantalla).
         */
        private fun poblar() {
            CoroutineScope(Dispatchers.IO).launch {
                val baseDatos = obtenerBaseDatos(context)
                SeedExploracion.poblar(baseDatos)
                SeedRutasVisitas.poblar(baseDatos)
            }
        }
    }
}
