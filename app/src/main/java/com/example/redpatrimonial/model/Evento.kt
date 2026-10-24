package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Evento o aviso publicado por un recinto (CU-19). Tabla "eventos".
 * idRecinto indica a qué recinto pertenece.
 * visible = false significa que el administrador lo ocultó (CU-25).
 * fechaPublicacion se llena sola con la hora actual en milisegundos al crear el objeto.
 */
@Entity(tableName = "eventos")
data class Evento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idRecinto: Int,
    val titulo: String,
    val tipo: String,
    val descripcion: String,
    val fecha: String,
    val hora: String,
    val visible: Boolean = true,
    val idAutor: Int? = null,
    val fechaPublicacion: Long = System.currentTimeMillis()
)

/**
 * Resultado de una consulta con JOIN entre eventos y recintos. No es una tabla.
 * Room arma este objeto con las columnas que devuelve el SELECT: los nombres de las propiedades
 * deben coincidir con los nombres de las columnas (por eso la consulta usa "r.nombre AS nombreRecinto").
 * Sirve para mostrar el nombre y la ubicación del recinto junto a cada evento sin hacer dos consultas.
 */
data class EventoConRecinto(
    val id: Int,
    val idRecinto: Int,
    val titulo: String,
    val tipo: String,
    val descripcion: String,
    val fecha: String,
    val hora: String,
    val visible: Boolean,
    val nombreRecinto: String,
    val latitud: Double,
    val longitud: Double
)
