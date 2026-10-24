package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registro de cada cambio de estado de un recinto (CU-17). Tabla "historial_estado".
 * Guarda el estado anterior, el nuevo y la fecha, para que el encargado vea qué cambió y cuándo.
 */
@Entity(tableName = "historial_estado")
data class HistorialEstado(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idRecinto: Int,
    val estadoAnterior: EstadoApertura,
    val estadoNuevo: EstadoApertura,
    val fechaHora: Long = System.currentTimeMillis()
)
