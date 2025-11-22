package com.zoho.inventarioapp.ui.movimientos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zoho.inventarioapp.data.repository.MovimientosInventarioRepository
import com.zoho.inventarioapp.data.repository.InventarioSucursalRepository
import com.zoho.inventarioapp.data.repository.FechasProductoRepository

class MovimientosVMFactory(

    private val movimientosRepository: MovimientosInventarioRepository,
    private val inventarioRepository: InventarioSucursalRepository,
    private val fechasRepository: FechasProductoRepository

) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovimientosInventarioViewModel::class.java)) {
            return MovimientosInventarioViewModel(
                movimientosRepository,
                inventarioRepository,
                fechasRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel Desconocido")
    }
}
