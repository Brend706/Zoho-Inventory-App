package com.zoho.inventarioapp.data.local.dao

import androidx.room.*
import com.zoho.inventarioapp.data.local.entities.MovimientosInventario
import com.zoho.inventarioapp.data.local.entities.TipoMovimiento
import kotlinx.coroutines.flow.Flow
// Data class para la consulta con detalles de las fechas de fabricacion y vencimiento
data class MovimientoConDetalles(
    @Embedded val movimiento: MovimientosInventario,
    val nombreUsuario: String,
    val nombreSucursal: String,
    val fechaFabricacion: Long?,
    val fechaVencimiento: Long?
)
@Dao
interface MovimientosInventarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(mov: MovimientosInventario): Long

    @Update
    suspend fun actualizar(mov: MovimientosInventario)

    @Delete
    suspend fun eliminar(mov: MovimientosInventario)

    @Query("SELECT * FROM movimientos_inventario ORDER BY fecha_movimiento DESC")
    fun obtenerTodos(): Flow<List<MovimientosInventario>>

    @Query("SELECT * FROM movimientos_inventario WHERE idMovimiento = :id")
    suspend fun obtenerPorId(id: Int): MovimientosInventario?

    /*obtener por tipo de movimiento: entrada/salida*/
    @Query("SELECT * FROM movimientos_inventario WHERE tipo_movimiento = :tipo ORDER BY fecha_movimiento DESC")
    fun obtenerPorTipo(tipo: TipoMovimiento): Flow<List<MovimientosInventario>>

    /*obtiene todos los movimientos de un producto*/
    @Query("SELECT * FROM movimientos_inventario WHERE idProducto = :productoId ORDER BY fecha_movimiento DESC")
    fun obtenerPorProducto(productoId: Int): Flow<List<MovimientosInventario>>

    @Query("SELECT * FROM movimientos_inventario WHERE idSucursal = :sucursalId ORDER BY fecha_movimiento DESC")
    fun obtenerPorSucursal(sucursalId: Int): Flow<List<MovimientosInventario>>

    @Query("SELECT * FROM movimientos_inventario WHERE idUsuario = :usuarioId ORDER BY fecha_movimiento DESC")
    fun obtenerPorUsuario(usuarioId: Int): Flow<List<MovimientosInventario>>

    /*devuelve los movimientos de un inventario, osea de un producto en una sucursal especifica*/
    @Query("""
        SELECT 
            m.*,
            u.nombre as nombreUsuario,
            s.nombre as nombreSucursal,
            f.fecha_fabricacion as fechaFabricacion,
            f.fecha_caducidad as fechaVencimiento
        FROM movimientos_inventario m
        INNER JOIN usuarios u ON m.idUsuario = u.idUsuario
        INNER JOIN sucursales s ON m.idSucursal = s.idSucursal
        LEFT JOIN fechas_productos f ON m.idFechasP = f.idFechasP
        WHERE m.idInventario = :idInventario
        ORDER BY m.fecha_movimiento DESC
    """)
    fun obtenerMovimientosPorInventario(idInventario: Int): Flow<List<MovimientoConDetalles>>
}
