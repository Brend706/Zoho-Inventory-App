package com.zoho.inventarioapp.data.local.dao

import androidx.room.*
import com.zoho.inventarioapp.data.local.entities.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(productos: List<Producto>)

    @Update
    suspend fun actualizar(producto: Producto)

    @Delete
    suspend fun eliminar(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE idProducto = :id")
    suspend fun obtenerPorId(id: Int): Producto?

    @Query("SELECT * FROM productos WHERE codProducto = :codigo")
    suspend fun obtenerPorCodigo(codigo: String): Producto?

    @Query("SELECT * FROM productos WHERE idCategoria = :categoriaId")
    fun obtenerPorCategoria(categoriaId: Int): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%'")
    fun buscarPorNombre(query: String): Flow<List<Producto>>
}