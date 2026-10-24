package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cada lectura de un código QR en un recinto queda registrada como visita. Tabla "visitas_qr".
 * La pantalla de detalle de ruta usa estas visitas para marcar las paradas ya recorridas.
 */
@Entity(tableName = "visitas_qr")
data class VisitaQr(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idRecinto: Int,
    val fechaHora: Long = System.currentTimeMillis()
)
