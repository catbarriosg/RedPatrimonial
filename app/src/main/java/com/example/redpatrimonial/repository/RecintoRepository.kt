package com.example.redpatrimonial.repository

import com.example.redpatrimonial.dao.RecintoDao
import com.example.redpatrimonial.model.AperturaEspecial
import com.example.redpatrimonial.model.EstadoApertura
import com.example.redpatrimonial.model.HistorialEstado
import com.example.redpatrimonial.model.Recinto
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio de recintos: la capa entre los ViewModels y el DAO.
 * Los ViewModels no llaman directo al DAO; así, en la Prueba 3 se puede cambiar el origen
 * de los datos (por ejemplo, la API con Retrofit) sin tocar los ViewModels ni las pantallas.
 * Recibe el DAO por el constructor.
 */
class RecintoRepository(
    private val recintoDao: RecintoDao
) {
    /**
     * Recintos activos (Flow que se actualiza solo).
     */
    fun listar(): Flow<List<Recinto>> {
        return recintoDao.recintoListar()
    }

    /**
     * Un recinto por id.
     */
    fun obtener(id: Int): Flow<Recinto?> {
        return recintoDao.recintoObtener(id)
    }

    /**
     * Recinto a cargo de un encargado.
     */
    fun obtenerPorEncargado(idEncargado: Int): Flow<Recinto?> {
        return recintoDao.recintoObtenerPorEncargado(idEncargado)
    }

    /**
     * Registra un recinto nuevo (CU-24).
     */
    suspend fun registrar(recinto: Recinto) {
        recintoDao.recintoAlmacenar(recinto)
    }

    /**
     * Guarda los cambios de la ficha (CU-16).
     */
    suspend fun actualizar(recinto: Recinto) {
        recintoDao.recintoActualizar(recinto)
    }

    /**
     * CU-17: cambia el estado del recinto y deja registro en el historial.
     * Agrupa dos operaciones del DAO en una sola acción del negocio; por eso esta lógica vive en el repositorio.
     */
    suspend fun cambiarEstado(recinto: Recinto, nuevoEstado: EstadoApertura) {
        recintoDao.recintoCambiarEstado(recinto.id, nuevoEstado)
        recintoDao.historialAlmacenar(
            HistorialEstado(
                idRecinto = recinto.id,
                estadoAnterior = recinto.estadoActual,
                estadoNuevo = nuevoEstado
            )
        )
    }

    /**
     * Historial de cambios de estado de un recinto.
     */
    fun listarHistorial(idRecinto: Int): Flow<List<HistorialEstado>> {
        return recintoDao.historialListar(idRecinto)
    }

    /**
     * Guarda una apertura especial (CU-18).
     */
    suspend fun programarApertura(apertura: AperturaEspecial) {
        recintoDao.aperturaAlmacenar(apertura)
    }

    /**
     * Aperturas especiales de un recinto.
     */
    fun listarAperturas(idRecinto: Int): Flow<List<AperturaEspecial>> {
        return recintoDao.aperturaListar(idRecinto)
    }
}
