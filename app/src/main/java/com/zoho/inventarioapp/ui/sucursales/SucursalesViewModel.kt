package com.zoho.inventarioapp.ui.sucursales

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zoho.inventarioapp.data.local.database.AppDatabase
import com.zoho.inventarioapp.data.local.entities.Sucursal
import com.zoho.inventarioapp.data.repository.SucursalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SucursalesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SucursalRepository

    // Lista de sucursales
    private val _sucursales = MutableStateFlow<List<Sucursal>>(emptyList())
    val sucursales: StateFlow<List<Sucursal>> = _sucursales.asStateFlow()

    // Estado de carga
    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Mensajes
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        val sucursalDao = database.sucursalDao()
        repository = SucursalRepository(sucursalDao)

        cargarSucursales()
    }

    fun cargarSucursales() {
        viewModelScope.launch {
            _cargando.value = true
            repository.todasLasSucursales.collect { lista ->
                _sucursales.value = lista
                _cargando.value = false
            }
        }
    }

    fun agregarSucursal(nombre: String, direccion: String, telefono: String) {
        viewModelScope.launch {
            try {
                val sucursal = Sucursal(
                    nombre = nombre,
                    direccion = direccion,
                    telefono = telefono
                )
                repository.insertar(sucursal)
                _mensaje.value = "Sucursal agregada correctamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al agregar: ${e.message}"
            }
        }
    }

    fun actualizarSucursal(id: Int, nombre: String, direccion: String, telefono: String) {
        viewModelScope.launch {
            try {
                val sucursal = Sucursal(
                    idSucursal = id,
                    nombre = nombre,
                    direccion = direccion,
                    telefono = telefono
                )
                repository.actualizar(sucursal)
                _mensaje.value = "Sucursal actualizada correctamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar: ${e.message}"
            }
        }
    }

    fun eliminarSucursal(sucursal: Sucursal) {
        viewModelScope.launch {
            try {
                repository.eliminar(sucursal)
                _mensaje.value = "Sucursal eliminada"
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}