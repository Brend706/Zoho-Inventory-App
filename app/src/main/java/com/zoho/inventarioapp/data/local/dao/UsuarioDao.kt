package com.zoho.inventarioapp.data.local.dao

import androidx.room.*
import com.zoho.inventarioapp.data.local.entities.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(usuario: Usuario): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(usuarios: List<Usuario>)

    @Update
    suspend fun actualizar(usuario: Usuario)

    @Delete
    suspend fun eliminar(usuario: Usuario)

    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Usuario>>

    //metodo para probar la bd
    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    suspend fun obtenerTodosSuspend(): List<Usuario>

    @Query("SELECT * FROM usuarios WHERE idUsuario = :id")
    suspend fun obtenerPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE codUsuario = :codigo")
    suspend fun obtenerPorCodigo(codigo: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE correo = :correo")
    suspend fun obtenerPorCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND password = :password")
    suspend fun login(correo: String, password: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE idRol = :idRol")
    fun obtenerPorRol(idRol: Int): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE idSucursal = :idSucursal")
    fun obtenerPorSucursal(idSucursal: Int): Flow<List<Usuario>>
}