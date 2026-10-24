package com.example.redpatrimonial.repository

import com.example.redpatrimonial.dao.UsuarioDao
import com.example.redpatrimonial.model.Rol
import com.example.redpatrimonial.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de usuarios.
 */
class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {
    /**
     * Valida el login. Limpia el correo antes de buscar: trim() quita espacios y lowercase()
     * lo pasa a minúsculas, así "Docente@Demo.cl " también funciona.
     */
    suspend fun validar(mail: String, pass: String): Usuario? {
        return usuarioDao.usuarioValidar(mail.trim().lowercase(), pass)
    }

    /**
     * Usuarios de un rol.
     */
    fun listarPorRol(rol: Rol): Flow<List<Usuario>> {
        return usuarioDao.usuarioListarPorRol(rol)
    }

    /**
     * Todos los usuarios.
     */
    fun listar(): Flow<List<Usuario>> {
        return usuarioDao.usuarioListar()
    }
}
