package com.zoho.inventarioapp.data.repository

import com.zoho.inventarioapp.data.local.dao.UsuarioDao
import com.zoho.inventarioapp.data.local.entities.Usuario
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    val todosLosUsuarios: Flow<List<Usuario>> = usuarioDao.obtenerTodos()

    suspend fun insertar(usuario: Usuario): Long {
        return usuarioDao.insertar(usuario)
    }

    suspend fun insertarTodos(usuarios: List<Usuario>) {
        usuarioDao.insertarTodos(usuarios)
    }

    suspend fun actualizar(usuario: Usuario) {
        usuarioDao.actualizar(usuario)
    }

    suspend fun eliminar(usuario: Usuario) {
        usuarioDao.eliminar(usuario)
    }

    suspend fun obtenerPorId(id: Int): Usuario? {
        return usuarioDao.obtenerPorId(id)
    }

    suspend fun obtenerPorCodigo(codigo: String): Usuario? {
        return usuarioDao.obtenerPorCodigo(codigo)
    }

    suspend fun obtenerPorCorreo(email: String): Boolean {
        return usuarioDao.obtenerPorCorreo(email) != null
    }

    suspend fun login(email: String, password: String): Usuario? {
        return usuarioDao.login(email, password)
    }

    fun obtenerPorRol(rolId: Int): Flow<List<Usuario>> {
        return usuarioDao.obtenerPorRol(rolId)
    }

    fun obtenerPorSucursal(sucursalId: Int): Flow<List<Usuario>> {
        return usuarioDao.obtenerPorSucursal(sucursalId)
    }
}