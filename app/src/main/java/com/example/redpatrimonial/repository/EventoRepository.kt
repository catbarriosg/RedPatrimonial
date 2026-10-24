package com.example.redpatrimonial.repository

import com.example.redpatrimonial.dao.EventoDao
import com.example.redpatrimonial.model.Evento
import com.example.redpatrimonial.model.EventoConRecinto
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de eventos. Traspasa las llamadas al EventoDao.
 */
class EventoRepository(
    private val eventoDao: EventoDao
) {
    /**
     * Eventos visibles para el público.
     */
    fun listarVisibles(): Flow<List<EventoConRecinto>> {
        return eventoDao.eventoListarVisibles()
    }

    /**
     * Todos los eventos, incluidos los ocultos (moderación).
     */
    fun listarTodos(): Flow<List<EventoConRecinto>> {
        return eventoDao.eventoListarTodos()
    }

    /**
     * Un evento con los datos de su recinto.
     */
    fun obtener(id: Int): Flow<EventoConRecinto?> {
        return eventoDao.eventoObtener(id)
    }

    /**
     * Eventos visibles de un recinto.
     */
    fun listarPorRecinto(idRecinto: Int): Flow<List<Evento>> {
        return eventoDao.eventoListarPorRecinto(idRecinto)
    }

    /**
     * Guarda un evento nuevo (CU-19).
     */
    suspend fun publicar(evento: Evento) {
        eventoDao.eventoAlmacenar(evento)
    }

    /**
     * Muestra u oculta un evento (CU-25).
     */
    suspend fun cambiarVisibilidad(id: Int, visible: Boolean) {
        eventoDao.eventoCambiarVisibilidad(id, visible)
    }
}
