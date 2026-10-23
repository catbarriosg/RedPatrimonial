package com.example.redpatrimonial.database

import android.content.Context
import com.example.redpatrimonial.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UsuarioInicializador {

    suspend fun crearUsuariosPrueba(context: Context) {
        withContext(Dispatchers.IO) {
            val dao = DatabaseProvider
                .obtenerBaseDatos(context)
                .usuarioDao()

            val usuarios = listOf(
                Usuario(
                    mail = "docente@redpatrimonial.cl",
                    pass = "Docente123",
                    rol = "DOCENTE"
                ),
                Usuario(
                    mail = "encargado@redpatrimonial.cl",
                    pass = "Encargado123",
                    rol = "ENCARGADO"
                ),
                Usuario(
                    mail = "guia@redpatrimonial.cl",
                    pass = "Guia123",
                    rol = "GUIA"
                ),
                Usuario(
                    mail = "admin@redpatrimonial.cl",
                    pass = "Admin123",
                    rol = "ADMINISTRADOR"
                )
            )

            usuarios.forEach { usuario ->
                if (dao.buscarPorMail(usuario.mail) == null) {
                    dao.guardarUsuario(usuario)
                }
            }
        }
    }
}
