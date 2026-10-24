package com.example.redpatrimonial.repository

import com.example.redpatrimonial.dao.VisitaDao
import com.example.redpatrimonial.model.DisponibilidadGuia
import com.example.redpatrimonial.model.EstadoSolicitud
import com.example.redpatrimonial.model.SalidaPedagogica
import com.example.redpatrimonial.model.SolicitudGuia
import com.example.redpatrimonial.model.VisitaQr
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de salidas, solicitudes de guía, disponibilidad y visitas por QR.
 * Aquí están las reglas de cómo cambia el estado de una solicitud.
 */
class VisitaRepository(
    private val visitaDao: VisitaDao
) {
    /**
     * CU-12 y CU-13: guarda la salida pedagógica y, si el docente pidió guía, crea la solicitud.
     * La solicitud necesita el id de la salida, por eso primero se guarda la salida.
     */
    suspend fun planificarSalida(salida: SalidaPedagogica, solicitarGuia: Boolean, idRecinto: Int, comentario: String) {
        val idSalida = visitaDao.salidaAlmacenar(salida).toInt()
        if (solicitarGuia) {
            visitaDao.solicitudAlmacenar(
                SolicitudGuia(
                    idSalida = idSalida,
                    idDocente = salida.idDocente,
                    idRecinto = idRecinto,
                    fecha = salida.fecha,
                    hora = salida.hora,
                    tamanoGrupo = salida.tamanoGrupo,
                    comentario = comentario
                )
            )
        }
    }

    /**
     * Salidas de un docente.
     */
    fun listarSalidas(idDocente: Int): Flow<List<SalidaPedagogica>> {
        return visitaDao.salidaListarPorDocente(idDocente)
    }

    /**
     * Solicitudes hechas por un docente (CU-14).
     */
    fun listarSolicitudesDocente(idDocente: Int): Flow<List<SolicitudGuia>> {
        return visitaDao.solicitudListarPorDocente(idDocente)
    }

    /**
     * Solicitudes que recibió un recinto (CU-20).
     */
    fun listarSolicitudesRecinto(idRecinto: Int): Flow<List<SolicitudGuia>> {
        return visitaDao.solicitudListarPorRecinto(idRecinto)
    }

    /**
     * Solicitudes asignadas a un guía que esperan su respuesta (estado ASIGNADA).
     */
    fun listarAsignacionesGuia(idGuia: Int): Flow<List<SolicitudGuia>> {
        return visitaDao.solicitudListarPorGuia(idGuia, EstadoSolicitud.ASIGNADA)
    }

    /**
     * Recorridos confirmados de un guía (estado ACEPTADA, CU-23).
     */
    fun listarAgendaGuia(idGuia: Int): Flow<List<SolicitudGuia>> {
        return visitaDao.solicitudListarPorGuia(idGuia, EstadoSolicitud.ACEPTADA)
    }

    /**
     * CU-20: el encargado asigna un guía; la solicitud pasa a ASIGNADA.
     */
    suspend fun asignarGuia(idSolicitud: Int, idGuia: Int) {
        visitaDao.solicitudActualizarEstado(idSolicitud, EstadoSolicitud.ASIGNADA, idGuia)
    }

    /**
     * El encargado rechaza la solicitud; se quita el guía.
     */
    suspend fun rechazarSolicitud(idSolicitud: Int) {
        visitaDao.solicitudActualizarEstado(idSolicitud, EstadoSolicitud.RECHAZADA, null)
    }

    /**
     * CU-22: respuesta del guía.
     * Si acepta, la solicitud pasa a ACEPTADA. Si rechaza, vuelve a PENDIENTE sin guía,
     * para que el encargado pueda asignar a otro.
     */
    suspend fun responderAsignacion(solicitud: SolicitudGuia, acepta: Boolean) {
        if (acepta) {
            visitaDao.solicitudActualizarEstado(solicitud.id, EstadoSolicitud.ACEPTADA, solicitud.idGuia)
        } else {
            visitaDao.solicitudActualizarEstado(solicitud.id, EstadoSolicitud.PENDIENTE, null)
        }
    }

    /**
     * Bloques de disponibilidad de un guía.
     */
    fun listarDisponibilidad(idGuia: Int): Flow<List<DisponibilidadGuia>> {
        return visitaDao.disponibilidadListar(idGuia)
    }

    /**
     * Agrega un bloque de disponibilidad (CU-21).
     */
    suspend fun agregarDisponibilidad(disponibilidad: DisponibilidadGuia) {
        visitaDao.disponibilidadAlmacenar(disponibilidad)
    }

    /**
     * Elimina un bloque de disponibilidad.
     */
    suspend fun eliminarDisponibilidad(id: Int) {
        visitaDao.disponibilidadEliminar(id)
    }

    /**
     * Registra que se escaneó el QR de un recinto.
     */
    suspend fun registrarVisitaQr(idRecinto: Int) {
        visitaDao.visitaAlmacenar(VisitaQr(idRecinto = idRecinto))
    }

    /**
     * Visitas registradas por QR.
     */
    fun listarVisitasQr(): Flow<List<VisitaQr>> {
        return visitaDao.visitaListar()
    }
}
