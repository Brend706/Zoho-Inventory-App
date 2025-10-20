package com.zoho.inventarioapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zoho.inventarioapp.data.repository.UsuarioRepository
import com.zoho.inventarioapp.data.local.entities.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun loginAmbos(correo: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(correo, password)
            _usuario.value = user
        }
    }
}
