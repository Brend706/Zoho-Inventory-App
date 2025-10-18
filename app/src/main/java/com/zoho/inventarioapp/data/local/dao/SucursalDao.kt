package com.zoho.inventarioapp.data.local.dao

import androidx.room.*
import com.zoho.inventarioapp.data.local.entities.Sucursal
import kotlinx.coroutines.flow.Flow

@Dao
interface SucursalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(sucursal: Sucursal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(sucursales: List<Sucursal>)

    @Update
    suspend fun actualizar(sucursal: Sucursal)

    @Delete
    suspend fun eliminar(sucursal: Sucursal)

    @Query("SELECT * FROM sucursales ORDER BY nombre ASC")
    fun obtenerTodas(): Flow<List<Sucursal>>

    //metodo para probar la bd
    @Query("SELECT * FROM sucursales ORDER BY nombre ASC")
    suspend fun obtenerTodasSuspend(): List<Sucursal>

    @Query("SELECT * FROM sucursales WHERE idSucursal = :id")
    suspend fun obtenerPorId(id: Int): Sucursal?

    @Query("SELECT * FROM sucursales WHERE nombre LIKE '%' || :query || '%'")
    fun buscarPorNombre(query: String): Flow<List<Sucursal>>
}