
package com.example.redpatrimonial.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.redpatrimonial.dao.EventoDao
import com.example.redpatrimonial.dao.FavoritoDao
import com.example.redpatrimonial.dao.RecintoDao
import com.example.redpatrimonial.dao.RutaDao
import com.example.redpatrimonial.dao.UsuarioDao
import com.example.redpatrimonial.dao.VisitaDao
import com.example.redpatrimonial.model.AperturaEspecial
import com.example.redpatrimonial.model.DisponibilidadGuia
import com.example.redpatrimonial.model.Evento
import com.example.redpatrimonial.model.Favorito
import com.example.redpatrimonial.model.HistorialEstado
import com.example.redpatrimonial.model.ParadaRuta
import com.example.redpatrimonial.model.Recinto
import com.example.redpatrimonial.model.Ruta
import com.example.redpatrimonial.model.SalidaPedagogica
import com.example.redpatrimonial.model.SolicitudGuia
import com.example.redpatrimonial.model.Usuario
import com.example.redpatrimonial.model.VisitaQr

/**
 * Definición de la base de datos Room.
 * - entities: todas las tablas. Si se agrega una entidad nueva, hay que sumarla aquí.
 * - version: se sube cada vez que cambia la estructura de alguna tabla (agregar un campo, por ejemplo).
 * - exportSchema = false: no genera archivos JSON con el esquema.
 * Es abstracta porque Room genera la clase concreta (AppDatabase_Impl) al compilar.
 */
@Database(
    entities = [
        // Exploración y gestión del recinto
        Recinto::class,
        Evento::class,
        Favorito::class,
        HistorialEstado::class,
        AperturaEspecial::class,
        // Acceso, rutas y visitas
        Usuario::class,
        Ruta::class,
        ParadaRuta::class,
        SalidaPedagogica::class,
        SolicitudGuia::class,
        DisponibilidadGuia::class,
        VisitaQr::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Cada función abstracta entrega un DAO. Room genera su implementación.
     */
    abstract fun recintoDao(): RecintoDao
    /**
     * DAO de eventos.
     */
    abstract fun eventoDao(): EventoDao
    /**
     * DAO de favoritos.
     */
    abstract fun favoritoDao(): FavoritoDao
    /**
     * DAO de usuarios.
     */
    abstract fun usuarioDao(): UsuarioDao
    /**
     * DAO de rutas y paradas.
     */
    abstract fun rutaDao(): RutaDao
    /**
     * DAO de salidas, solicitudes, disponibilidad y visitas por QR.
     */
    abstract fun visitaDao(): VisitaDao
}
