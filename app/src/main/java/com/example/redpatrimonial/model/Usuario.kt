package com.example.redpatrimonial.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Cuenta de un docente, encargado, guía o administrador. Tabla "usuarios".
 * El login (CU-15) busca en esta tabla un usuario con el mismo mail y pass.
 * Las contraseñas están en texto plano solo porque son datos de prueba locales;
 * el backend (ms-usuarios) sí las guarda cifradas con BCrypt.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val mail: String,
    val pass: String,
    val rol: Rol,
    val institucion: String = ""
)
