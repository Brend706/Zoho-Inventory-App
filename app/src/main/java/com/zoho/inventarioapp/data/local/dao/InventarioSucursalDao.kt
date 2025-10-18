package com.zoho.inventarioapp.data.local.dao

import androidx.room.*
import com.zoho.inventarioapp.data.local.entities.InventarioSucursal
import kotlinx.coroutines.flow.Flow

@Dao
interface InventarioSucursalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(inventario: InventarioSucursal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(inventarios: List<InventarioSucursal>)

    @Update
    suspend fun actualizar(inventario: InventarioSucursal)

    @Delete
    suspend fun eliminar(inventario: InventarioSucursal)

    @Query("SELECT * FROM inventario_sucursal")
    fun obtenerTodos(): Flow<List<InventarioSucursal>>

    @Query("SELECT * FROM inventario_sucursal WHERE idInventario = :id")
    suspend fun obtenerPorId(id: Int): InventarioSucursal?

    @Query("SELECT * FROM inventario_sucursal WHERE idProducto = :productoId AND idSucursal = :sucursalId")
    suspend fun obtenerPorProductoYSucursal(productoId: Int, sucursalId: Int): InventarioSucursal?

    @Query("SELECT * FROM inventario_sucursal WHERE idSucursal = :sucursalId")
    fun obtenerPorSucursal(sucursalId: Int): Flow<List<InventarioSucursal>>

    @Query("SELECT * FROM inventario_sucursal WHERE idProducto = :productoId")
    fun obtenerPorProducto(productoId: Int): Flow<List<InventarioSucursal>>

    // Obtener productos con stock bajo (menor al stock mínimo)
    @Query("""
        SELECT i.* FROM inventario_sucursal i
        INNER JOIN productos p ON i.idProducto = p.idProducto
        WHERE i.stock_actual < p.stockMinimo
    """)
    fun obtenerStockBajo(): Flow<List<InventarioSucursal>>

    // Obtener productos sin stock
    @Query("SELECT * FROM inventario_sucursal WHERE stock_actual = 0")
    fun obtenerSinStock(): Flow<List<InventarioSucursal>>
}