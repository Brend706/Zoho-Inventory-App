package com.zoho.inventarioapp.ui.productos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zoho.inventarioapp.data.local.database.AppDatabase
import com.zoho.inventarioapp.data.local.entities.Producto
import com.zoho.inventarioapp.data.local.entities.Categoria
import com.zoho.inventarioapp.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

class ProductosViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductoRepository
    val todosLosProductos: StateFlow<List<Producto>>

    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias

    init {
        val productoDao = AppDatabase.getDatabase(application).productoDao()
        val categoriaDao = AppDatabase.getDatabase(application).categoriaDao()
        repository = ProductoRepository(productoDao)

        // Observar productos directamente como StateFlow
        todosLosProductos = repository.todosLosProductos
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

        // Cargar categorías
        viewModelScope.launch {
            _categorias.value = categoriaDao.obtenerTodas().first()
        }
    }

    fun agregarProducto(producto: Producto) = viewModelScope.launch {
        try {
            val id = repository.insertar(producto)
            android.util.Log.d("ProductosViewModel", "Producto insertado con ID: $id")
        } catch (e: Exception) {
            android.util.Log.e("ProductosViewModel", "Error insertando producto: ${e.message}")
        }
    }

    fun editarProducto(producto: Producto) = viewModelScope.launch {
        repository.actualizar(producto)
    }

    fun eliminarProducto(producto: Producto) = viewModelScope.launch {
        repository.eliminar(producto)
    }
}

