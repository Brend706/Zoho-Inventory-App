package com.zoho.inventarioapp.data.local.dao

import androidx.room.*
import com.zoho.inventarioapp.data.local.entities.MovimientosInventario
import com.zoho.inventarioapp.data.local.entities.TipoMovimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientosInventarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(movimiento: MovimientosInventario): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(movimientos: List<MovimientosInventario>)

    @Update
    suspend fun actualizar(movimiento: MovimientosInventario)

    @Delete
    suspend fun eliminar(movimiento: MovimientosInventario)

    @Query("SELECT * FROM movimientos_inventario ORDER BY fecha_movimiento DESC")
    fun obtenerTodos(): Flow<List<MovimientosInventario>>

    @Query("SELECT * FROM movimientos_inventario WHERE idMovimiento = :id")
    suspend fun obtenerPorId(id: Int): MovimientosInventario?

    @Query("SELECT * FROM movimientos_inventario WHERE tipo_movimiento = :tipo ORDER BY fecha_movimiento DESC")
    fun obtenerPorTipo(tipo: TipoMovimiento): Flow<List<MovimientosInventario>>

    @Query("SELECT * FROM movimientos_inventario WHERE idProducto = :productoId ORDER BY fecha_movimiento DESC")
    fun obtenerPorProducto(productoId: Int): Flow<List<MovimientosInventario>>

    @Query("SELECT * FROM movimientos_inventario WHERE idSucursal = :sucursalId ORDER BY fecha_movimiento DESC")
    fun obtenerPorSucursal(sucursalId: Int): Flow<List<MovimientosInventario>>

    @Query("SELECT * FROM movimientos_inventario WHERE idUsuario = :usuarioId ORDER BY fecha_movimiento DESC")
    fun obtenerPorUsuario(usuarioId: Int): Flow<List<MovimientosInventario>>

    // Obtener movimientos en un rango de fechas
    @Query("""
        SELECT * FROM movimientos_inventario 
        WHERE fecha_movimiento BETWEEN :fechaInicio AND :fechaFin
        ORDER BY fecha_movimiento DESC
    """)
    fun obtenerPorRangoFechas(fechaInicio: Long, fechaFin: Long): Flow<List<MovimientosInventario>>

    // Obtener últimos N movimientos
    @Query("SELECT * FROM movimientos_inventario ORDER BY fecha_movimiento DESC LIMIT :limite")
    fun obtenerUltimos(limite: Int): Flow<List<MovimientosInventario>>
}