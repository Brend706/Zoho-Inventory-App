package com.zoho.inventarioapp.data.local.dao

import androidx.room.*
import com.zoho.inventarioapp.data.local.entities.FechasProducto
import kotlinx.coroutines.flow.Flow

@Dao
interface FechasProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(fechasProducto: FechasProducto): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(fechasProductos: List<FechasProducto>)

    @Update
    suspend fun actualizar(fechasProducto: FechasProducto)

    @Delete
    suspend fun eliminar(fechasProducto: FechasProducto)

    @Query("SELECT * FROM fechas_productos ORDER BY fecha_caducidad ASC")
    fun obtenerTodas(): Flow<List<FechasProducto>>

    @Query("SELECT * FROM fechas_productos WHERE idFechasP = :id")
    suspend fun obtenerPorId(id: Int): FechasProducto?

    @Query("SELECT * FROM fechas_productos WHERE idProducto = :productoId ORDER BY fecha_caducidad ASC")
    fun obtenerPorProducto(productoId: Int): Flow<List<FechasProducto>>

    // Obtener productos próximos a vencer (en los próximos 30 días)
    @Query("""
        SELECT * FROM fechas_productos 
        WHERE fecha_caducidad BETWEEN :fechaActual AND :fechaLimite
        ORDER BY fecha_caducidad ASC
    """)
    fun obtenerProximosAVencer(fechaActual: Long, fechaLimite: Long): Flow<List<FechasProducto>>

    // Obtener productos ya vencidos
    @Query("""
        SELECT * FROM fechas_productos 
        WHERE fecha_caducidad < :fechaActual
        ORDER BY fecha_caducidad DESC
    """)
    fun obtenerVencidos(fechaActual: Long): Flow<List<FechasProducto>>
}