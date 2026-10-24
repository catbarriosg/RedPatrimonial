package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Bloque semanal en que un guía puede acompañar recorridos (CU-21). Tabla "disponibilidad_guia".
 * Ejemplo: Lunes de 09:00 a 13:00.
 */
@Entity(tableName = "disponibilidad_guia")
data class DisponibilidadGuia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idGuia: Int,
    val diaSemana: String,
    val horaInicio: String,
    val horaFin: String
)
