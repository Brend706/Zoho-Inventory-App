package com.zoho.inventarioapp.data.repository

import com.zoho.inventarioapp.data.local.dao.SucursalDao
import com.zoho.inventarioapp.data.local.entities.Sucursal
import kotlinx.coroutines.flow.Flow

class SucursalRepository(private val sucursalDao: SucursalDao) {

    val todasLasSucursales: Flow<List<Sucursal>> = sucursalDao.obtenerTodas()

    suspend fun insertar(sucursal: Sucursal): Long {
        return sucursalDao.insertar(sucursal)
    }

    suspend fun insertarTodas(sucursales: List<Sucursal>) {
        sucursalDao.insertarTodas(sucursales)
    }

    suspend fun actualizar(sucursal: Sucursal) {
        sucursalDao.actualizar(sucursal)
    }

    suspend fun eliminar(sucursal: Sucursal) {
        sucursalDao.eliminar(sucursal)
    }

    suspend fun obtenerPorId(id: Int): Sucursal? {
        return sucursalDao.obtenerPorId(id)
    }

    fun buscarPorNombre(query: String): Flow<List<Sucursal>> {
        return sucursalDao.buscarPorNombre(query)
    }
}