package com.example.redpatrimonial.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.redpatrimonial.model.Evento
import com.example.redpatrimonial.model.EventoConRecinto
import kotlinx.coroutines.flow.Flow

/**
 * DAO de eventos. Tres consultas usan JOIN con recintos y devuelven EventoConRecinto,
 * para tener el nombre y las coordenadas del recinto junto a cada evento.
 * Las consultas largas usan triple comilla (""" ... """) para escribir el SQL en varias líneas.
 */
@Dao
interface EventoDao {

    /**
     * Inserta un evento nuevo (CU-19, publicar evento).
     */
    @Insert
    suspend fun eventoAlmacenar(evento: Evento)

    /**
     * Inserta varios eventos de una vez. Se usa para los datos de prueba.
     */
    @Insert
    suspend fun eventoAlmacenarTodos(eventos: List<Evento>)

    /**
     * Eventos visibles para el público con los datos de su recinto, ordenados por fecha y hora.
     * Los ocultos por el administrador (visible = 0) no aparecen.
     */
    @Query(
        """
        SELECT e.id, e.idRecinto, e.titulo, e.tipo, e.descripcion, e.fecha, e.hora, e.visible,
               r.nombre AS nombreRecinto, r.latitud, r.longitud
        FROM eventos e INNER JOIN recintos r ON r.id = e.idRecinto
        WHERE e.visible = 1
        ORDER BY e.fecha, e.hora
        """
    )
    fun eventoListarVisibles(): Flow<List<EventoConRecinto>>

    /**
     * Todos los eventos, incluidos los ocultos, del más reciente al más antiguo.
     * La usa la pantalla de moderación del administrador.
     */
    @Query(
        """
        SELECT e.id, e.idRecinto, e.titulo, e.tipo, e.descripcion, e.fecha, e.hora, e.visible,
               r.nombre AS nombreRecinto, r.latitud, r.longitud
        FROM eventos e INNER JOIN recintos r ON r.id = e.idRecinto
        ORDER BY e.fechaPublicacion DESC
        """
    )
    fun eventoListarTodos(): Flow<List<EventoConRecinto>>

    /**
     * Un evento con los datos de su recinto. Lo usa la pantalla de detalle del evento.
     */
    @Query(
        """
        SELECT e.id, e.idRecinto, e.titulo, e.tipo, e.descripcion, e.fecha, e.hora, e.visible,
               r.nombre AS nombreRecinto, r.latitud, r.longitud
        FROM eventos e INNER JOIN recintos r ON r.id = e.idRecinto
        WHERE e.id = :id
        """
    )
    fun eventoObtener(id: Int): Flow<EventoConRecinto?>

    /**
     * Eventos visibles de un recinto. Se muestran en la ficha del recinto.
     */
    @Query("SELECT * FROM eventos WHERE idRecinto = :idRecinto AND visible = 1 ORDER BY fecha, hora")
    fun eventoListarPorRecinto(idRecinto: Int): Flow<List<Evento>>

    /**
     * Muestra u oculta un evento (CU-25, moderar publicaciones).
     */
    @Query("UPDATE eventos SET visible = :visible WHERE id = :id")
    suspend fun eventoCambiarVisibilidad(id: Int, visible: Boolean)
}
