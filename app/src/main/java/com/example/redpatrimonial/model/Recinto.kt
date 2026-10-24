package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Ficha patrimonial de un templo, museo, colegio o archivo (CU-03).
 *
 * La anotación @Entity convierte la clase en la tabla "recintos" de Room: cada propiedad es una columna.
 * La anotación @PrimaryKey(autoGenerate = true) hace que SQLite asigne el id solo; por eso el valor por defecto es 0.
 * Es una data class: Kotlin genera equals, toString y copy(). copy() se usa al editar la ficha
 * para crear una versión nueva con algunos campos cambiados, sin modificar el objeto original.
 *
 * latitud y longitud ubican el recinto en el mapa y permiten calcular distancias.
 * idEncargado guarda el id del usuario encargado (no es una relación de Room, solo el número).
 */
@Entity(tableName = "recintos")
data class Recinto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val comuna: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val categoria: String,
    val estilo: String,
    val siglo: String,
    val resenaHistorica: String,
    val horarioSemana: String,
    val horarioSabado: String,
    val horarioDomingo: String,
    val estadoActual: EstadoApertura = EstadoApertura.ABIERTO,
    val idEncargado: Int? = null,
    val activo: Boolean = true
)
