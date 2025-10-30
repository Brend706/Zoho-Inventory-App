package com.zoho.inventarioapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zoho.inventarioapp.data.repository.UsuarioRepository
import com.zoho.inventarioapp.data.local.entities.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun loginAmbos(correo: String, password: String) {
        viewModelScope.launch {
            //val user = repository.login(correo, password)
            //_usuario.value = user
            try {
                val user = repository.login(correo, password)

                if (user != null) {
                    _usuario.value = user
                    _loginError.value = null  // Limpiar error
                } else {
                    // Verificar si el correo existe
                    val existeCorreo = repository.obtenerPorCorreo(correo)
                    _usuario.value = null
                    _loginError.value = null
                    _loginError.value = if (existeCorreo) {
                        "Contraseña incorrecta"
                    } else {
                        "Correo no registrado"
                    }
                }
            } catch (e: Exception) {
                _usuario.value = null
                _loginError.value = "Error al iniciar sesión: ${e.message}"
            }

        }
    }
}
