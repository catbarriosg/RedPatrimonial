package com.example.redpatrimonial.repository

import com.example.redpatrimonial.dao.RutaDao
import com.example.redpatrimonial.model.ParadaRuta
import com.example.redpatrimonial.model.Ruta
import com.example.redpatrimonial.model.TipoRuta
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de rutas y paradas.
 */
class RutaRepository(
    private val rutaDao: RutaDao
) {
    /**
     * Rutas oficiales (CU-08).
     */
    fun listarOficiales(): Flow<List<Ruta>> {
        return rutaDao.rutaListarPorTipo(TipoRuta.OFICIAL)
    }

    /**
     * Rutas creadas por los usuarios (CU-09).
     */
    fun listarPersonalizadas(): Flow<List<Ruta>> {
        return rutaDao.rutaListarPorTipo(TipoRuta.PERSONALIZADA)
    }

    /**
     * Una ruta por id.
     */
    fun obtener(id: Int): Flow<Ruta?> {
        return rutaDao.rutaObtener(id)
    }

    /**
     * Paradas de una ruta en orden.
     */
    fun listarParadas(idRuta: Int): Flow<List<ParadaRuta>> {
        return rutaDao.paradaListar(idRuta)
    }

    /**
     * Todas las paradas, para armar la lista de recintos de cada ruta.
     */
    fun listarTodasLasParadas(): Flow<List<ParadaRuta>> {
        return rutaDao.paradaListarTodas()
    }

    /**
     * Guarda la ruta con sus paradas y devuelve el id de la ruta nueva.
     */
    suspend fun guardar(ruta: Ruta, idsRecintos: List<Int>): Int {
        return rutaDao.rutaAlmacenarConParadas(ruta, idsRecintos)
    }

    /**
     * Elimina una ruta y sus paradas.
     */
    suspend fun eliminar(idRuta: Int) {
        rutaDao.rutaEliminarConParadas(idRuta)
    }
}
