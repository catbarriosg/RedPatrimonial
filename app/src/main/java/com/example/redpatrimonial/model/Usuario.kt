package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["mail"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mail: String,
    val pass: String,
    val rol: String = "VISITANTE"
)

