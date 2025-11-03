package com.zoho.inventarioapp.ui.categorias

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.zoho.inventarioapp.data.local.database.AppDatabase
import com.zoho.inventarioapp.data.local.entities.Categoria
import com.zoho.inventarioapp.data.repository.CategoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoriasViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CategoriaRepository

    // Lista de categorías
    private val _categorias = MutableStateFlow<List<Categoria>>(emptyList())
    val categorias: StateFlow<List<Categoria>> = _categorias.asStateFlow()

    // Mensajes
    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        val categoriaDao = database.categoriaDao()
        repository = CategoriaRepository(categoriaDao)

        cargarCategorias()
    }

    fun cargarCategorias() {
        viewModelScope.launch {
            repository.todasLasCategorias.collect { lista ->
                _categorias.value = lista
            }
        }
    }

    fun agregarCategoria(nombre: String) {
        viewModelScope.launch {
            try {
                if (nombre.isEmpty()) {
                    _mensaje.value = "El nombre es obligatorio"
                    return@launch
                }

                val categoria = Categoria(
                    nombre = nombre
                )

                repository.insertar(categoria)
                _mensaje.value = "Categoría agregada correctamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al agregar: ${e.message}"
            }
        }
    }

    fun actualizarCategoria(id: Int, nombre: String) {
        viewModelScope.launch {
            try {
                if (nombre.isEmpty()) {
                    _mensaje.value = "El nombre es obligatorio"
                    return@launch
                }

                val categoria = Categoria(
                    idCategoria = id,
                    nombre = nombre
                )

                repository.actualizar(categoria)
                _mensaje.value = "Categoría actualizada correctamente"
            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar: ${e.message}"
            }
        }
    }

    fun eliminarCategoria(categoria: Categoria) {
        viewModelScope.launch {
            try {
                repository.eliminar(categoria)
                _mensaje.value = "Categoría eliminada"
            } catch (e: Exception) {
                _mensaje.value = "Error al eliminar: ${e.message}"
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}