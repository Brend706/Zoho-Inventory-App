package com.zoho.inventarioapp.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zoho.inventarioapp.data.local.database.AppDatabase
import com.zoho.inventarioapp.data.repository.UsuarioRepository

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val usuarioDao = AppDatabase.getDatabase(context).usuarioDao()
        val repository = UsuarioRepository(usuarioDao)
        return LoginViewModel(repository) as T
    }
}