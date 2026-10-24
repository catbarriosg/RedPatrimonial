package com.example.redpatrimonial.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.redpatrimonial.model.Rol
import com.example.redpatrimonial.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * DAO de usuarios: login y listas de usuarios por rol.
 */
@Dao
interface UsuarioDao {

    /**
     * Inserta los usuarios de prueba.
     */
    @Insert
    suspend fun usuarioAlmacenarTodos(usuarios: List<Usuario>)

    /**
     * Login (CU-15): busca un usuario con ese correo y contraseña.
     * Es suspend y no Flow porque se consulta una sola vez al pulsar "Ingresar".
     * Devuelve null si las credenciales no coinciden.
     */
    @Query("SELECT * FROM usuarios WHERE mail = :mail AND pass = :pass LIMIT 1")
    suspend fun usuarioValidar(mail: String, pass: String): Usuario?

    /**
     * Usuarios de un rol, por ejemplo todos los guías para asignar uno.
     */
    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY nombre")
    fun usuarioListarPorRol(rol: Rol): Flow<List<Usuario>>

    /**
     * Todos los usuarios. Se usa para mostrar nombres a partir de un id.
     */
    @Query("SELECT * FROM usuarios ORDER BY nombre")
    fun usuarioListar(): Flow<List<Usuario>>
}
