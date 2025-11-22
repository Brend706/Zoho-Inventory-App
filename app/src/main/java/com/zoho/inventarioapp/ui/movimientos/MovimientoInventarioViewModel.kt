package com.zoho.inventarioapp.ui.movimientos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zoho.inventarioapp.data.local.entities.FechasProducto
import com.zoho.inventarioapp.data.local.entities.InventarioSucursal
import com.zoho.inventarioapp.data.local.entities.MovimientosInventario
import com.zoho.inventarioapp.data.local.entities.TipoMovimiento
import com.zoho.inventarioapp.data.repository.FechasProductoRepository
import com.zoho.inventarioapp.data.repository.InventarioSucursalRepository
import com.zoho.inventarioapp.data.repository.MovimientosInventarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovimientosInventarioViewModel(
    private val movimientosRepo: MovimientosInventarioRepository,
    private val inventarioRepo: InventarioSucursalRepository,
    private val fechasRepo: FechasProductoRepository
) : ViewModel() {

    private val _nombreProducto = MutableStateFlow<String?>(null)
    val nombreProducto: StateFlow<String?> = _nombreProducto.asStateFlow()

    fun obtenerMovimientos(idInventario: Int) =
        movimientosRepo.obtenerMovimientosPorInventario(idInventario)

    fun cargarNombreProducto(idInventario: Int) = viewModelScope.launch {
        _nombreProducto.value = inventarioRepo.obtenerNombreProductoPorInventario(idInventario)
    }

    fun registrarMovimiento(mov: MovimientosInventario) = viewModelScope.launch {

        movimientosRepo.insertar(mov)

        val inventario = inventarioRepo.obtenerPorId(mov.idInventario) ?: return@launch

        val nuevoStock = when (mov.tipoMovimiento) {
            TipoMovimiento.INGRESO -> inventario.stockActual + mov.cantidad
            TipoMovimiento.SALIDA -> inventario.stockActual - mov.cantidad
            TipoMovimiento.AJUSTE -> mov.cantidad
        }

        inventarioRepo.actualizarStock(mov.idInventario, nuevoStock)
    }

    suspend fun obtenerInventarioPorId(idInventario: Int): InventarioSucursal? {
        return inventarioRepo.obtenerPorId(idInventario)
    }

    suspend fun insertarFechasProducto(fechasProducto: FechasProducto): Long {
        return fechasRepo.insertar(fechasProducto)
    }

    fun actualizarStock(idInventario: Int, nuevoStock: Int) = viewModelScope.launch {
        inventarioRepo.actualizarStock(idInventario, nuevoStock)
    }

    fun eliminarMovimiento(mov: MovimientosInventario) = viewModelScope.launch {
        movimientosRepo.eliminar(mov)
    }
}