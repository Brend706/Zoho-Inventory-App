package com.zoho.inventarioapp.data.repository

import com.zoho.inventarioapp.data.local.dao.ProductoDao
import com.zoho.inventarioapp.data.local.entities.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao) {

    val todosLosProductos: Flow<List<Producto>> = productoDao.obtenerTodos()

    suspend fun insertar(producto: Producto): Long {
        return productoDao.insertar(producto)
    }

    suspend fun insertarTodos(productos: List<Producto>) {
        productoDao.insertarTodos(productos)
    }

    suspend fun actualizar(producto: Producto) {
        productoDao.actualizar(producto)
    }

    suspend fun eliminar(producto: Producto) {
        productoDao.eliminar(producto)
    }

    suspend fun obtenerPorId(id: Int): Producto? {
        return productoDao.obtenerPorId(id)
    }

    suspend fun obtenerPorCodigo(codigo: String): Producto? {
        return productoDao.obtenerPorCodigo(codigo)
    }

    fun obtenerPorCategoria(categoriaId: Int): Flow<List<Producto>> {
        return productoDao.obtenerPorCategoria(categoriaId)
    }

    fun buscarPorNombre(query: String): Flow<List<Producto>> {
        return productoDao.buscarPorNombre(query)
    }
}