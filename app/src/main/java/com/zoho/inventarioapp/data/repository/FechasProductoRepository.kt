package com.zoho.inventarioapp.data.repository

import com.zoho.inventarioapp.data.local.dao.FechasProductoDao
import com.zoho.inventarioapp.data.local.entities.FechasProducto
import kotlinx.coroutines.flow.Flow

class FechasProductoRepository(private val fechasProductoDao: FechasProductoDao) {

    val todasLasFechas: Flow<List<FechasProducto>> = fechasProductoDao.obtenerTodas()

    suspend fun insertar(fechasProducto: FechasProducto): Long {
        return fechasProductoDao.insertar(fechasProducto)
    }

    suspend fun insertarTodas(fechasProductos: List<FechasProducto>) {
        fechasProductoDao.insertarTodas(fechasProductos)
    }

    suspend fun actualizar(fechasProducto: FechasProducto) {
        fechasProductoDao.actualizar(fechasProducto)
    }

    suspend fun eliminar(fechasProducto: FechasProducto) {
        fechasProductoDao.eliminar(fechasProducto)
    }

    suspend fun obtenerPorId(id: Int): FechasProducto? {
        return fechasProductoDao.obtenerPorId(id)
    }

    fun obtenerPorProducto(productoId: Int): Flow<List<FechasProducto>> {
        return fechasProductoDao.obtenerPorProducto(productoId)
    }

    fun obtenerProximosAVencer(fechaActual: Long, fechaLimite: Long): Flow<List<FechasProducto>> {
        return fechasProductoDao.obtenerProximosAVencer(fechaActual, fechaLimite)
    }

    fun obtenerVencidos(fechaActual: Long): Flow<List<FechasProducto>> {
        return fechasProductoDao.obtenerVencidos(fechaActual)
    }
}