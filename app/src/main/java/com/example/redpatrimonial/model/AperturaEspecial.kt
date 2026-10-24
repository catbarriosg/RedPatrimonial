package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Bloque horario en que un recinto cerrado abre de forma excepcional (CU-18).
 * Tabla "aperturas_especiales". Fecha y horas se guardan como texto (AAAA-MM-DD y HH:MM)
 * y se validan antes de guardar con util/Validaciones.
 */
@Entity(tableName = "aperturas_especiales")
data class AperturaEspecial(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idRecinto: Int,
    val fecha: String,
    val horaInicio: String,
    val horaFin: String,
    val motivo: String
)
