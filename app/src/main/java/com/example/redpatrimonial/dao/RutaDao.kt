package com.example.redpatrimonial.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.redpatrimonial.model.ParadaRuta
import com.example.redpatrimonial.model.Ruta
import com.example.redpatrimonial.model.TipoRuta
import kotlinx.coroutines.flow.Flow

/**
 * DAO de rutas y paradas.
 */
@Dao
interface RutaDao {

    /**
     * Inserta una ruta y devuelve el id generado, que se necesita para guardar sus paradas.
     */
    @Insert
    suspend fun rutaAlmacenar(ruta: Ruta): Long

    /**
     * Inserta todas las paradas de una ruta.
     */
    @Insert
    suspend fun paradaAlmacenarTodas(paradas: List<ParadaRuta>)

    /**
     * Borra una ruta por id.
     */
    @Query("DELETE FROM rutas WHERE id = :id")
    suspend fun rutaEliminar(id: Int)

    /**
     * Borra todas las paradas de una ruta.
     */
    @Query("DELETE FROM paradas_ruta WHERE idRuta = :idRuta")
    suspend fun paradaEliminarPorRuta(idRuta: Int)

    /**
     * Rutas oficiales o personalizadas, según el tipo recibido.
     */
    @Query("SELECT * FROM rutas WHERE tipo = :tipo ORDER BY nombre")
    fun rutaListarPorTipo(tipo: TipoRuta): Flow<List<Ruta>>

    /**
     * Una ruta por su id.
     */
    @Query("SELECT * FROM rutas WHERE id = :id")
    fun rutaObtener(id: Int): Flow<Ruta?>

    /**
     * Paradas de una ruta en el orden del recorrido.
     */
    @Query("SELECT * FROM paradas_ruta WHERE idRuta = :idRuta ORDER BY orden")
    fun paradaListar(idRuta: Int): Flow<List<ParadaRuta>>

    /**
     * Todas las paradas de todas las rutas. Con esta lista se arma, para cada ruta, la lista de sus recintos.
     */
    @Query("SELECT * FROM paradas_ruta ORDER BY idRuta, orden")
    fun paradaListarTodas(): Flow<List<ParadaRuta>>

    /**
     * Guarda la ruta y sus paradas en una sola operación.
     * Al ser una interfaz, Kotlin permite escribir aquí una función con cuerpo que llama a las otras.
     * La anotación @Transaction hace que todo se ejecute junto: si algo falla, no queda una ruta sin paradas.
     * mapIndexed recorre los ids con su posición para asignar orden = posición + 1.
     */
    @Transaction
    suspend fun rutaAlmacenarConParadas(ruta: Ruta, idsRecintos: List<Int>): Int {
        val idRuta = rutaAlmacenar(ruta).toInt()
        val paradas = idsRecintos.mapIndexed { indice, idRecinto ->
            ParadaRuta(idRuta = idRuta, idRecinto = idRecinto, orden = indice + 1)
        }
        paradaAlmacenarTodas(paradas)
        return idRuta
    }

    /**
     * Borra primero las paradas y después la ruta, dentro de una transacción.
     */
    @Transaction
    suspend fun rutaEliminarConParadas(idRuta: Int) {
        paradaEliminarPorRuta(idRuta)
        rutaEliminar(idRuta)
    }
}
