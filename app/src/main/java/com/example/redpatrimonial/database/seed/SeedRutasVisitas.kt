package com.example.redpatrimonial.database.seed

import com.example.redpatrimonial.database.AppDatabase
import com.example.redpatrimonial.model.DisponibilidadGuia
import com.example.redpatrimonial.model.EstadoSolicitud
import com.example.redpatrimonial.model.Rol
import com.example.redpatrimonial.model.Ruta
import com.example.redpatrimonial.model.SalidaPedagogica
import com.example.redpatrimonial.model.SolicitudGuia
import com.example.redpatrimonial.model.TipoRuta
import com.example.redpatrimonial.model.Usuario
import com.example.redpatrimonial.util.CalculadoraTiempos

/**
 * Datos de prueba de usuarios, rutas oficiales, salidas, solicitudes y disponibilidad (Persona B).
 * Todos los datos son ficticios. Los IDs de usuario coinciden con los del backend.
 */
object SeedRutasVisitas {

    private val usuarios = listOf(
        Usuario(id = 1, nombre = "Docente Demo", mail = "docente@demo.cl", pass = "1234", rol = Rol.DOCENTE, institucion = "Colegio Ficticio Los Andes"),
        Usuario(id = 2, nombre = "Encargado San Francisco", mail = "encargado@demo.cl", pass = "1234", rol = Rol.ENCARGADO),
        Usuario(id = 3, nombre = "Encargado La Merced", mail = "encargado2@demo.cl", pass = "1234", rol = Rol.ENCARGADO),
        Usuario(id = 4, nombre = "Guía Demo Uno", mail = "guia@demo.cl", pass = "1234", rol = Rol.GUIA),
        Usuario(id = 5, nombre = "Guía Demo Dos", mail = "guia2@demo.cl", pass = "1234", rol = Rol.GUIA),
        Usuario(id = 6, nombre = "Administrador de Red", mail = "admin@demo.cl", pass = "1234", rol = Rol.ADMIN)
    )

    // Nombre, descripción e IDs de los recintos en el orden del recorrido
    private val rutasOficiales = listOf(
        Triple("Ruta de las Tres Basílicas", "La Merced, Sacramentinos y El Salvador en un solo recorrido.", listOf(4, 5, 6)),
        Triple("Tesoros Coloniales del Centro", "Los templos más antiguos en torno a la Plaza de Armas.", listOf(1, 10, 2, 4)),
        Triple("Circuito Sacro Recoleta", "Desde el Barrio Lastarria hasta la Recoleta Dominica.", listOf(7, 3, 8))
    )

    /**
     * Inserta los datos en orden: usuarios, rutas con paradas, salidas, solicitudes y disponibilidad.
     * Para cada ruta oficial calcula distancia y duración con CalculadoraTiempos.
     * Las desestructuraciones (nombre, descripcion, idsRecintos) separan los tres valores de cada Triple.
     */
    suspend fun poblar(baseDatos: AppDatabase) {
        baseDatos.usuarioDao().usuarioAlmacenarTodos(usuarios)

        val rutaDao = baseDatos.rutaDao()
        rutasOficiales.forEach { (nombre, descripcion, idsRecintos) ->
            val paradas = idsRecintos.map { id -> SeedExploracion.recintos.first { it.id == id } }
            val resumen = CalculadoraTiempos.calcular(paradas)
            val ruta = Ruta(
                nombre = nombre,
                descripcion = descripcion,
                tipo = TipoRuta.OFICIAL,
                distanciaKm = resumen.distanciaKm,
                duracionMin = resumen.duracionTotalMin
            )
            rutaDao.rutaAlmacenarConParadas(ruta, idsRecintos)
        }

        val visitaDao = baseDatos.visitaDao()
        val idSalida1 = visitaDao.salidaAlmacenar(
            SalidaPedagogica(idDocente = 1, idRuta = 2, fecha = "2026-11-09", hora = "10:00", tamanoGrupo = 32, nivelEducativo = "2° medio", observaciones = "Unidad de historia colonial")
        ).toInt()
        val idSalida2 = visitaDao.salidaAlmacenar(
            SalidaPedagogica(idDocente = 1, idRuta = 1, fecha = "2026-11-16", hora = "09:30", tamanoGrupo = 28, nivelEducativo = "8° básico", observaciones = "")
        ).toInt()

        visitaDao.solicitudAlmacenarTodas(
            listOf(
                SolicitudGuia(idSalida = idSalida1, idDocente = 1, idRecinto = 1, fecha = "2026-11-09", hora = "10:00", tamanoGrupo = 32, comentario = "Grupo con dos estudiantes en silla de ruedas"),
                SolicitudGuia(idSalida = idSalida2, idDocente = 1, idRecinto = 4, fecha = "2026-11-16", hora = "09:30", tamanoGrupo = 28, comentario = "", estado = EstadoSolicitud.ASIGNADA, idGuia = 4),
                SolicitudGuia(idSalida = idSalida2, idDocente = 1, idRecinto = 1, fecha = "2026-11-16", hora = "11:00", tamanoGrupo = 28, comentario = "", estado = EstadoSolicitud.ACEPTADA, idGuia = 4)
            )
        )

        visitaDao.disponibilidadAlmacenarTodas(
            listOf(
                DisponibilidadGuia(idGuia = 4, diaSemana = "Lunes", horaInicio = "09:00", horaFin = "13:00"),
                DisponibilidadGuia(idGuia = 4, diaSemana = "Miércoles", horaInicio = "15:00", horaFin = "18:00"),
                DisponibilidadGuia(idGuia = 5, diaSemana = "Sábado", horaInicio = "10:00", horaFin = "14:00")
            )
        )
    }
}
