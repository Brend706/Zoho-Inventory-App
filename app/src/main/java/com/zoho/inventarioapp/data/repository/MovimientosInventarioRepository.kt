package com.zoho.inventarioapp.data.repository

import com.zoho.inventarioapp.data.local.dao.MovimientoConDetalles
import com.zoho.inventarioapp.data.local.dao.MovimientosInventarioDao
import com.zoho.inventarioapp.data.local.entities.MovimientosInventario
import com.zoho.inventarioapp.data.local.entities.TipoMovimiento
import kotlinx.coroutines.flow.Flow

class MovimientosInventarioRepository(
    private val movimientoDao: MovimientosInventarioDao
) {

    // Obtener todos los movimientos (si se usa en alguna vista del admin)
    val todosLosMovimientos: Flow<List<MovimientosInventario>> =
        movimientoDao.obtenerTodos()

    // Insertar
    suspend fun insertar(movimiento: MovimientosInventario): Long {
        return movimientoDao.insertar(movimiento)
    }

    // Actualizar
    suspend fun actualizar(movimiento: MovimientosInventario) {
        movimientoDao.actualizar(movimiento)
    }

    // Eliminar
    suspend fun eliminar(movimiento: MovimientosInventario) {
        movimientoDao.eliminar(movimiento)
    }

    // Obtener por ID
    suspend fun obtenerPorId(id: Int): MovimientosInventario? {
        return movimientoDao.obtenerPorId(id)
    }

    fun obtenerMovimientosPorInventario(inventarioId: Int): Flow<List<MovimientoConDetalles>> {
        return movimientoDao.obtenerMovimientosPorInventario(inventarioId)
    }

    fun obtenerPorTipo(tipo: TipoMovimiento): Flow<List<MovimientosInventario>> {
        return movimientoDao.obtenerPorTipo(tipo)
    }

    fun obtenerPorProducto(productoId: Int): Flow<List<MovimientosInventario>> {
        return movimientoDao.obtenerPorProducto(productoId)
    }

    fun obtenerPorSucursal(sucursalId: Int): Flow<List<MovimientosInventario>> {
        return movimientoDao.obtenerPorSucursal(sucursalId)
    }

    fun obtenerPorUsuario(usuarioId: Int): Flow<List<MovimientosInventario>> {
        return movimientoDao.obtenerPorUsuario(usuarioId)
    }
}