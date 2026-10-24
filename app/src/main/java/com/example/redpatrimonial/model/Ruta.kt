package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * OFICIAL: ruta publicada por el administrador (CU-26). PERSONALIZADA: ruta armada por el usuario (CU-09).
 */
enum class TipoRuta { OFICIAL, PERSONALIZADA }

/**
 * Ruta patrimonial. Tabla "rutas".
 * Las paradas no están aquí: viven en la tabla paradas_ruta, unidas por idRuta (relación uno a muchos).
 * distanciaKm y duracionMin se calculan con CalculadoraTiempos al guardar la ruta.
 */
@Entity(tableName = "rutas")
data class Ruta(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val tipo: TipoRuta,
    val distanciaKm: Double,
    val duracionMin: Int,
    val idUsuario: Int? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)
