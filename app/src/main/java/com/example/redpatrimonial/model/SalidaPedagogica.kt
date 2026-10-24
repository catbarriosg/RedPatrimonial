package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Salida pedagógica planificada por un docente (CU-12). Tabla "salidas_pedagogicas".
 * Siempre se basa en una ruta (idRuta), tal como indica la relación R-05 de los casos de uso.
 */
@Entity(tableName = "salidas_pedagogicas")
data class SalidaPedagogica(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idDocente: Int,
    val idRuta: Int,
    val fecha: String,
    val hora: String,
    val tamanoGrupo: Int,
    val nivelEducativo: String,
    val observaciones: String
)
