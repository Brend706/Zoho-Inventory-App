package com.zoho.inventarioapp.data.repository

import com.zoho.inventarioapp.data.local.dao.RolDao
import com.zoho.inventarioapp.data.local.entities.Rol
import kotlinx.coroutines.flow.Flow

class RolRepository(private val rolDao: RolDao) {

    val todosLosRoles: Flow<List<Rol>> = rolDao.obtenerTodos()

    suspend fun insertar(rol: Rol): Long {
        return rolDao.insertar(rol)
    }

    suspend fun insertarTodos(roles: List<Rol>) {
        rolDao.insertarTodos(roles)
    }

    suspend fun actualizar(rol: Rol) {
        rolDao.actualizar(rol)
    }

    suspend fun eliminar(rol: Rol) {
        rolDao.eliminar(rol)
    }

    suspend fun obtenerPorId(id: Int): Rol? {
        return rolDao.obtenerPorId(id)
    }

    suspend fun obtenerPorTipo(tipo: String): Rol? {
        return rolDao.obtenerPorTipo(tipo)
    }
}