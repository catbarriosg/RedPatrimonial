
package com.example.redpatrimonial.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.redpatrimonial.model.Usuario

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun guardarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE mail = :mail LIMIT 1")
    suspend fun buscarPorMail(mail: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int
}
