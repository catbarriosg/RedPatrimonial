package com.example.redpatrimonial.repository

import com.example.redpatrimonial.dao.UsuarioDao
import com.example.redpatrimonial.model.Usuario

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {
    suspend fun guardar(usuario: Usuario) {
        usuarioDao.guardarUsuario(usuario)
    }

    suspend fun buscarPorMail(mail: String): Usuario? {
        return usuarioDao.buscarPorMail(mail)
    }
}
