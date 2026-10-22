package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favoritos")
data class Favorito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val idRecinto: Int
)