package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Recinto o evento guardado por el visitante en su teléfono (CU-05). Tabla "favoritos".
 * Un favorito apunta a un recinto o a un evento, nunca a ambos: por eso los dos campos son
 * nullable (Int?) y se llena solo uno.
 */
@Entity(tableName = "favoritos")
data class Favorito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idRecinto: Int? = null,
    val idEvento: Int? = null
)
