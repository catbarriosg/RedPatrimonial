package com.example.redpatrimonial.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.redpatrimonial.model.AperturaEspecial
import com.example.redpatrimonial.model.EstadoApertura
import com.example.redpatrimonial.model.HistorialEstado
import com.example.redpatrimonial.model.Recinto
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) de recintos, historial de estados y aperturas especiales.
 *
 * Es una interfaz: no escribimos el código, Room lo genera al compilar a partir de las anotaciones.
 * - @Insert / @Update: Room arma el INSERT o UPDATE a partir de la entidad.
 * - @Query: escribimos el SQL; los :parametros se reemplazan por los argumentos de la función.
 * - Las funciones "suspend" escriben en la base y deben llamarse desde una corrutina,
 *   así no bloquean la pantalla mientras trabajan.
 * - Las funciones que devuelven Flow no son suspend: entregan los datos y vuelven a emitir
 *   automáticamente cada vez que la tabla cambia. Así la pantalla se actualiza sola.
 */
@Dao
interface RecintoDao {

    /**
     * Inserta un recinto y devuelve el id que le asignó SQLite (tipo Long).
     */
    @Insert
    suspend fun recintoAlmacenar(recinto: Recinto): Long

    /**
     * Inserta una lista completa de recintos. Se usa para cargar los datos de prueba.
     */
    @Insert
    suspend fun recintoAlmacenarTodos(recintos: List<Recinto>)

    /**
     * Actualiza todas las columnas del recinto que tenga el mismo id (CU-16, editar ficha).
     */
    @Update
    suspend fun recintoActualizar(recinto: Recinto)

    /**
     * Recintos activos ordenados por nombre. Devuelve un Flow: se vuelve a emitir con cada cambio.
     */
    @Query("SELECT * FROM recintos WHERE activo = 1 ORDER BY nombre")
    fun recintoListar(): Flow<List<Recinto>>

    /**
     * Un recinto por su id. Es Recinto? (nullable) porque puede no existir.
     */
    @Query("SELECT * FROM recintos WHERE id = :id")
    fun recintoObtener(id: Int): Flow<Recinto?>

    /**
     * Recinto a cargo de un usuario encargado. Lo usan los paneles del encargado.
     */
    @Query("SELECT * FROM recintos WHERE idEncargado = :idEncargado LIMIT 1")
    fun recintoObtenerPorEncargado(idEncargado: Int): Flow<Recinto?>

    /**
     * Cambia solo la columna estadoActual, sin tocar el resto de la ficha (CU-17).
     */
    @Query("UPDATE recintos SET estadoActual = :estado WHERE id = :id")
    suspend fun recintoCambiarEstado(id: Int, estado: EstadoApertura)

    /**
     * Guarda un registro en el historial de cambios de estado.
     */
    @Insert
    suspend fun historialAlmacenar(historial: HistorialEstado)

    /**
     * Historial de un recinto, del cambio más reciente al más antiguo (DESC).
     */
    @Query("SELECT * FROM historial_estado WHERE idRecinto = :idRecinto ORDER BY fechaHora DESC")
    fun historialListar(idRecinto: Int): Flow<List<HistorialEstado>>

    /**
     * Guarda una apertura especial (CU-18).
     */
    @Insert
    suspend fun aperturaAlmacenar(apertura: AperturaEspecial)

    /**
     * Aperturas especiales de un recinto ordenadas por fecha.
     */
    @Query("SELECT * FROM aperturas_especiales WHERE idRecinto = :idRecinto ORDER BY fecha")
    fun aperturaListar(idRecinto: Int): Flow<List<AperturaEspecial>>
}
