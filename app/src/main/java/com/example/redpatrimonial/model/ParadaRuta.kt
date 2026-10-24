package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Una parada de una ruta. Tabla "paradas_ruta".
 * idRuta: ruta a la que pertenece. idRecinto: recinto que se visita.
 * orden: posición en el recorrido (1, 2, 3...). Las consultas ordenan por este campo.
 */
@Entity(tableName = "paradas_ruta")
data class ParadaRuta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idRuta: Int,
    val idRecinto: Int,
    val orden: Int,
    val tiempoVisitaMin: Int = 20
)
