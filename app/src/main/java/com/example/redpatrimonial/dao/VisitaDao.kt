package com.example.redpatrimonial.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.redpatrimonial.model.DisponibilidadGuia
import com.example.redpatrimonial.model.EstadoSolicitud
import com.example.redpatrimonial.model.SalidaPedagogica
import com.example.redpatrimonial.model.SolicitudGuia
import com.example.redpatrimonial.model.VisitaQr
import kotlinx.coroutines.flow.Flow

/**
 * DAO de salidas pedagógicas, solicitudes de guía, disponibilidad de guías y visitas registradas por QR.
 */
@Dao
interface VisitaDao {

    /**
     * Guarda una salida pedagógica y devuelve su id, que necesita la solicitud de guía.
     */
    @Insert
    suspend fun salidaAlmacenar(salida: SalidaPedagogica): Long

    /**
     * Salidas de un docente ordenadas por fecha.
     */
    @Query("SELECT * FROM salidas_pedagogicas WHERE idDocente = :idDocente ORDER BY fecha")
    fun salidaListarPorDocente(idDocente: Int): Flow<List<SalidaPedagogica>>

    /**
     * Guarda una solicitud de guía (CU-13).
     */
    @Insert
    suspend fun solicitudAlmacenar(solicitud: SolicitudGuia)

    /**
     * Guarda varias solicitudes. Se usa para los datos de prueba.
     */
    @Insert
    suspend fun solicitudAlmacenarTodas(solicitudes: List<SolicitudGuia>)

    /**
     * Solicitudes de un docente (CU-14, ver el estado de mis solicitudes).
     */
    @Query("SELECT * FROM solicitudes_guia WHERE idDocente = :idDocente ORDER BY fecha")
    fun solicitudListarPorDocente(idDocente: Int): Flow<List<SolicitudGuia>>

    /**
     * Solicitudes que recibió un recinto (CU-20).
     * Las pendientes van primero porque son las que el encargado debe resolver:
     * el CASE devuelve 0 para PENDIENTE y 1 para el resto, y se ordena por ese número.
     */
    @Query("SELECT * FROM solicitudes_guia WHERE idRecinto = :idRecinto ORDER BY CASE estado WHEN 'PENDIENTE' THEN 0 ELSE 1 END, fecha")
    fun solicitudListarPorRecinto(idRecinto: Int): Flow<List<SolicitudGuia>>

    /**
     * Solicitudes de un guía en un estado: ASIGNADA para responder (CU-22) o ACEPTADA para su agenda (CU-23).
     */
    @Query("SELECT * FROM solicitudes_guia WHERE idGuia = :idGuia AND estado = :estado ORDER BY fecha, hora")
    fun solicitudListarPorGuia(idGuia: Int, estado: EstadoSolicitud): Flow<List<SolicitudGuia>>

    /**
     * Cambia el estado de una solicitud y el guía asignado (null si se libera).
     */
    @Query("UPDATE solicitudes_guia SET estado = :estado, idGuia = :idGuia WHERE id = :id")
    suspend fun solicitudActualizarEstado(id: Int, estado: EstadoSolicitud, idGuia: Int?)

    /**
     * Guarda un bloque de disponibilidad de un guía (CU-21).
     */
    @Insert
    suspend fun disponibilidadAlmacenar(disponibilidad: DisponibilidadGuia)

    /**
     * Guarda varios bloques. Se usa para los datos de prueba.
     */
    @Insert
    suspend fun disponibilidadAlmacenarTodas(disponibilidades: List<DisponibilidadGuia>)

    /**
     * Borra un bloque de disponibilidad.
     */
    @Query("DELETE FROM disponibilidad_guia WHERE id = :id")
    suspend fun disponibilidadEliminar(id: Int)

    /**
     * Bloques de disponibilidad de un guía.
     */
    @Query("SELECT * FROM disponibilidad_guia WHERE idGuia = :idGuia ORDER BY id")
    fun disponibilidadListar(idGuia: Int): Flow<List<DisponibilidadGuia>>

    /**
     * Registra que se escaneó el QR de un recinto.
     */
    @Insert
    suspend fun visitaAlmacenar(visita: VisitaQr)

    /**
     * Visitas registradas, de la más reciente a la más antigua.
     */
    @Query("SELECT * FROM visitas_qr ORDER BY fechaHora DESC")
    fun visitaListar(): Flow<List<VisitaQr>>
}
