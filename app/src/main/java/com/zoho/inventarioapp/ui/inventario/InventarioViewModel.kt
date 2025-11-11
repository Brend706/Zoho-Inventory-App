package com.zoho.inventarioapp.ui.inventario

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zoho.inventarioapp.data.local.database.AppDatabase
import com.zoho.inventarioapp.data.local.entities.InventarioSucursal
import com.zoho.inventarioapp.data.local.entities.Producto
import com.zoho.inventarioapp.data.local.entities.Sucursal
import com.zoho.inventarioapp.data.repository.InventarioSucursalRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class InventarioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: InventarioSucursalRepository
    private val todosLosInventarios: StateFlow<List<InventarioSucursal>>

    private val _inventariosFiltrados = MutableStateFlow<List<InventarioSucursal>>(emptyList())
    val inventariosFiltrados: StateFlow<List<InventarioSucursal>> = _inventariosFiltrados

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _sucursales = MutableStateFlow<List<Sucursal>>(emptyList())
    val sucursales: StateFlow<List<Sucursal>> = _sucursales

    private val _sucursalSeleccionada = MutableStateFlow<Sucursal?>(null)
    val sucursalSeleccionada: StateFlow<Sucursal?> = _sucursalSeleccionada

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    init {
        val inventarioDao = AppDatabase.getDatabase(application).inventarioSucursalDao()
        val productoDao = AppDatabase.getDatabase(application).productoDao()
        val sucursalDao = AppDatabase.getDatabase(application).sucursalDao()

        repository = InventarioSucursalRepository(inventarioDao)

        // muestra inventarios directamente como StateFlow
        todosLosInventarios = repository.todosLosInventarios
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        // muestra productos en tiempo real
        viewModelScope.launch {
            productoDao.obtenerTodos().collect { productos ->
                _productos.value = productos
            }
        }

        // muestra sucursales en tiempo real
        viewModelScope.launch {
            sucursalDao.obtenerTodas().collect { sucursales ->
                _sucursales.value = sucursales
            }
        }

        // muestra los cambios en inventarios para actualizar la lista filtrada
        viewModelScope.launch {
            todosLosInventarios.collect { inventarios ->
                aplicarFiltro(inventarios)
            }
        }
    }

    private fun aplicarFiltro(inventarios: List<InventarioSucursal>) {
        val sucursal = _sucursalSeleccionada.value
        _inventariosFiltrados.value = if (sucursal == null) {
            inventarios
        } else {
            inventarios.filter { it.idSucursal == sucursal.idSucursal }
        }
    }

    fun filtrarPorSucursal(sucursal: Sucursal?) {
        _sucursalSeleccionada.value = sucursal
        aplicarFiltro(todosLosInventarios.value)
    }

    fun agregarInventario(inventario: InventarioSucursal) = viewModelScope.launch {
        try {
            // Verificar si ya existe un inventario para ese producto y sucursal
            val existente = repository.obtenerPorProductoYSucursal(
                inventario.idProducto,
                inventario.idSucursal
            )

            if (existente != null) {
                _mensaje.value = "Ya existe un inventario para este producto en esta sucursal"
                return@launch
            }

            val id = repository.insertar(inventario)
            android.util.Log.d("InventarioViewModel", "Inventario insertado con ID: $id")
            _mensaje.value = "Inventario creado correctamente"
        } catch (e: Exception) {
            android.util.Log.e("InventarioViewModel", "Error insertando inventario: ${e.message}")
            _mensaje.value = "Error al crear inventario: ${e.message}"
        }
    }

    fun editarInventario(inventario: InventarioSucursal) = viewModelScope.launch {
        try {
            repository.actualizar(inventario)
            _mensaje.value = "Inventario actualizado correctamente"
        } catch (e: Exception) {
            _mensaje.value = "Error al actualizar inventario: ${e.message}"
        }
    }

    fun eliminarInventario(inventario: InventarioSucursal) = viewModelScope.launch {
        try {
            repository.eliminar(inventario)
            _mensaje.value = "Inventario eliminado correctamente"
        } catch (e: Exception) {
            _mensaje.value = "Error al eliminar inventario: ${e.message}"
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}