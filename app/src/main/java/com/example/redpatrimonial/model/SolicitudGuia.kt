package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ciclo de vida de una solicitud de guía:
 * PENDIENTE -> ASIGNADA (el encargado eligió un guía) -> ACEPTADA (el guía confirmó).
 * Si el guía rechaza, vuelve a PENDIENTE. Si el encargado la rechaza, queda RECHAZADA.
 */
enum class EstadoSolicitud(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    ASIGNADA("Guía asignado"),
    ACEPTADA("Confirmada"),
    RECHAZADA("Rechazada")
}

/**
 * Pedido de un guía voluntario para una salida pedagógica (CU-13). Tabla "solicitudes_guia".
 * idRecinto indica en qué recinto se necesita el guía; idGuia queda null hasta que se asigna uno.
 * Copia fecha, hora y tamaño del grupo de la salida para mostrarlos sin otra consulta.
 */
@Entity(tableName = "solicitudes_guia")
data class SolicitudGuia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idSalida: Int,
    val idDocente: Int,
    val idRecinto: Int,
    val fecha: String,
    val hora: String,
    val tamanoGrupo: Int,
    val comentario: String,
    val estado: EstadoSolicitud = EstadoSolicitud.PENDIENTE,
    val idGuia: Int? = null,
    val fechaSolicitud: Long = System.currentTimeMillis()
)
