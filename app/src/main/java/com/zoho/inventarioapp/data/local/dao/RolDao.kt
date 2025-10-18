package com.zoho.inventarioapp.data.local.dao

import androidx.room.*
import com.zoho.inventarioapp.data.local.entities.Rol
import kotlinx.coroutines.flow.Flow

@Dao
interface RolDao {
    //
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(rol: Rol): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(roles: List<Rol>)

    @Update
    suspend fun actualizar(rol: Rol)

    @Delete
    suspend fun eliminar(rol: Rol)

    @Query("SELECT * FROM roles ORDER BY tipoRol ASC")
    fun obtenerTodos(): Flow<List<Rol>>

    //metodo para probar la BD
    @Query("SELECT * FROM roles ORDER BY tipoRol ASC")
    suspend fun obtenerTodosSuspend(): List<Rol>

    @Query("SELECT * FROM roles WHERE idRol = :id")
    suspend fun obtenerPorId(id: Int): Rol?

    @Query("SELECT * FROM roles WHERE tipoRol = :tipo")
    suspend fun obtenerPorTipo(tipo: String): Rol?
}