package com.zoho.inventarioapp.data.repository

import com.zoho.inventarioapp.data.local.dao.InventarioSucursalDao
import com.zoho.inventarioapp.data.local.entities.InventarioSucursal
import kotlinx.coroutines.flow.Flow

class InventarioSucursalRepository(private val inventarioSucursalDao: InventarioSucursalDao) {

    val todosLosInventarios: Flow<List<InventarioSucursal>> = inventarioSucursalDao.obtenerTodos()

    suspend fun insertar(inventario: InventarioSucursal): Long {
        return inventarioSucursalDao.insertar(inventario)
    }

    suspend fun insertarTodos(inventarios: List<InventarioSucursal>) {
        inventarioSucursalDao.insertarTodos(inventarios)
    }

    suspend fun actualizar(inventario: InventarioSucursal) {
        inventarioSucursalDao.actualizar(inventario)
    }

    suspend fun eliminar(inventario: InventarioSucursal) {
        inventarioSucursalDao.eliminar(inventario)
    }

    suspend fun obtenerPorId(id: Int): InventarioSucursal? {
        return inventarioSucursalDao.obtenerPorId(id)
    }

    suspend fun obtenerPorProductoYSucursal(productoId: Int, sucursalId: Int): InventarioSucursal? {
        return inventarioSucursalDao.obtenerPorProductoYSucursal(productoId, sucursalId)
    }

    suspend fun actualizarStock(idInventario: Int, nuevoStock: Int) {
        inventarioSucursalDao.actualizarStock(idInventario, nuevoStock)
    }

    suspend fun obtenerNombreProductoPorInventario(idInventario: Int): String? {
        return inventarioSucursalDao.obtenerNombreProductoPorInventario(idInventario)
    }

    fun obtenerPorSucursal(sucursalId: Int): Flow<List<InventarioSucursal>> {
        return inventarioSucursalDao.obtenerPorSucursal(sucursalId)
    }

    fun obtenerPorProducto(productoId: Int): Flow<List<InventarioSucursal>> {
        return inventarioSucursalDao.obtenerPorProducto(productoId)
    }

    fun obtenerStockBajo(): Flow<List<InventarioSucursal>> {
        return inventarioSucursalDao.obtenerStockBajo()
    }

    fun obtenerSinStock(): Flow<List<InventarioSucursal>> {
        return inventarioSucursalDao.obtenerSinStock()
    }
}