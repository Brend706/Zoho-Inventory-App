package com.zoho.inventarioapp.data.repository

import com.zoho.inventarioapp.data.local.dao.MovimientosInventarioDao
import com.zoho.inventarioapp.data.local.entities.MovimientosInventario
import com.zoho.inventarioapp.data.local.entities.TipoMovimiento
import kotlinx.coroutines.flow.Flow

class MovimientosInventarioRepository(private val movimientoInventarioDao: MovimientosInventarioDao) {

    val todosLosMovimientos: Flow<List<MovimientosInventario>> = movimientoInventarioDao.obtenerTodos()

    suspend fun insertar(movimiento: MovimientosInventario): Long {
        return movimientoInventarioDao.insertar(movimiento)
    }

    suspend fun insertarTodos(movimientos: List<MovimientosInventario>) {
        movimientoInventarioDao.insertarTodos(movimientos)
    }

    suspend fun actualizar(movimiento: MovimientosInventario) {
        movimientoInventarioDao.actualizar(movimiento)
    }

    suspend fun eliminar(movimiento: MovimientosInventario) {
        movimientoInventarioDao.eliminar(movimiento)
    }

    suspend fun obtenerPorId(id: Int): MovimientosInventario? {
        return movimientoInventarioDao.obtenerPorId(id)
    }

    fun obtenerPorTipo(tipo: TipoMovimiento): Flow<List<MovimientosInventario>> {
        return movimientoInventarioDao.obtenerPorTipo(tipo)
    }

    fun obtenerPorProducto(productoId: Int): Flow<List<MovimientosInventario>> {
        return movimientoInventarioDao.obtenerPorProducto(productoId)
    }

    fun obtenerPorSucursal(sucursalId: Int): Flow<List<MovimientosInventario>> {
        return movimientoInventarioDao.obtenerPorSucursal(sucursalId)
    }

    fun obtenerPorUsuario(usuarioId: Int): Flow<List<MovimientosInventario>> {
        return movimientoInventarioDao.obtenerPorUsuario(usuarioId)
    }

    fun obtenerPorRangoFechas(fechaInicio: Long, fechaFin: Long): Flow<List<MovimientosInventario>> {
        return movimientoInventarioDao.obtenerPorRangoFechas(fechaInicio, fechaFin)
    }

    fun obtenerUltimos(limite: Int): Flow<List<MovimientosInventario>> {
        return movimientoInventarioDao.obtenerUltimos(limite)
    }
}