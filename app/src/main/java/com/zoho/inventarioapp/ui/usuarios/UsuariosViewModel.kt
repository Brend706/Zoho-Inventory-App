package com.zoho.inventarioapp.ui.usuarios

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zoho.inventarioapp.data.local.database.AppDatabase
import com.zoho.inventarioapp.data.local.entities.Usuario
import com.zoho.inventarioapp.data.local.entities.Rol
import com.zoho.inventarioapp.data.local.entities.Sucursal
import com.zoho.inventarioapp.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UsuariosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UsuarioRepository
    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val todosLosUsuarios: StateFlow<List<Usuario>> = _usuarios

    private val _roles = MutableStateFlow<List<Rol>>(emptyList())
    val roles: StateFlow<List<Rol>> = _roles

    private val _sucursales = MutableStateFlow<List<Sucursal>>(emptyList())
    val sucursales: StateFlow<List<Sucursal>> = _sucursales

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje
    fun obtenerRoles() = viewModelScope.launch {
        val rolDao = AppDatabase.getDatabase(getApplication()).rolDao()
        rolDao.obtenerTodos().collect { lista ->
            _roles.value = lista
        }
    }
    fun obtenerSucursales() = viewModelScope.launch {
        val sucursalDao = AppDatabase.getDatabase(getApplication()).sucursalDao()
        sucursalDao.obtenerTodas().collect { lista ->
            _sucursales.value = lista
        }
    }

    init {
        val usuarioDao = AppDatabase.getDatabase(application).usuarioDao()
        repository = UsuarioRepository(usuarioDao)
        cargarUsuarios()
    }

    private fun cargarUsuarios() = viewModelScope.launch {
        repository.todosLosUsuarios.collect { lista ->
            _usuarios.value = lista
        }
    }

    fun agregarUsuario(usuario: Usuario) = viewModelScope.launch {
        try {
            repository.insertar(usuario)
            _mensaje.value = "Usuario agregado correctamente"
        } catch (e: Exception) {
            _mensaje.value = "Error al agregar: ${e.message}"
        }
    }

    fun editarUsuario(usuario: Usuario) = viewModelScope.launch {
        try {
            repository.actualizar(usuario)
            _mensaje.value = "Usuario actualizado correctamente"
        } catch (e: Exception) {
            _mensaje.value = "Error al actualizar: ${e.message}"
        }
    }

    fun eliminarUsuario(usuario: Usuario) = viewModelScope.launch {
        try {
            repository.eliminar(usuario)
            _mensaje.value = "Usuario eliminado correctamente"
        } catch (e: Exception) {
            _mensaje.value = "Error al eliminar: ${e.message}"
        }
    }

    // limpiar mensaje
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
