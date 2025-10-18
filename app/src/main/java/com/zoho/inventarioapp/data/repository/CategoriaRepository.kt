package com.zoho.inventarioapp.data.repository

import com.zoho.inventarioapp.data.local.dao.CategoriaDao
import com.zoho.inventarioapp.data.local.entities.Categoria
import kotlinx.coroutines.flow.Flow

class CategoriaRepository(private val categoriaDao: CategoriaDao) {

    val todasLasCategorias: Flow<List<Categoria>> = categoriaDao.obtenerTodas()

    suspend fun insertar(categoria: Categoria): Long {
        return categoriaDao.insertar(categoria)
    }

    suspend fun insertarTodas(categorias: List<Categoria>) {
        categoriaDao.insertarTodas(categorias)
    }

    suspend fun actualizar(categoria: Categoria) {
        categoriaDao.actualizar(categoria)
    }

    suspend fun eliminar(categoria: Categoria) {
        categoriaDao.eliminar(categoria)
    }

    suspend fun obtenerPorId(id: Int): Categoria? {
        return categoriaDao.obtenerPorId(id)
    }

    fun buscarPorNombre(query: String): Flow<List<Categoria>> {
        return categoriaDao.buscarPorNombre(query)
    }
}