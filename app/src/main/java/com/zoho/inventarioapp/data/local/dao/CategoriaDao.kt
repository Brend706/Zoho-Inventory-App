package com.zoho.inventarioapp.data.local.dao

import androidx.room.*
import com.zoho.inventarioapp.data.local.entities.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(categoria: Categoria): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(categorias: List<Categoria>)

    @Update
    suspend fun actualizar(categoria: Categoria)

    @Delete
    suspend fun eliminar(categoria: Categoria)

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE idCategoria = :id")
    suspend fun obtenerPorId(id: Int): Categoria?

    @Query("SELECT * FROM categorias WHERE nombre LIKE '%' || :query || '%'")
    fun buscarPorNombre(query: String): Flow<List<Categoria>>
}