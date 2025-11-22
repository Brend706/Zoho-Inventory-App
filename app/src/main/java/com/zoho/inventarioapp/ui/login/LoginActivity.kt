package com.zoho.inventarioapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.zoho.inventarioapp.R
import kotlinx.coroutines.launch

// Importamos las vistas a las que redirige el login
import com.zoho.inventarioapp.AdminActivity
import com.zoho.inventarioapp.EmpleadosActivity

class LoginActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val txtCorreo = findViewById<TextInputEditText>(R.id.inputCorreo)
        val txtPassword = findViewById<TextInputEditText>(R.id.inputPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val correo = txtCorreo.text.toString()
            val pass = txtPassword.text.toString()

            if (correo.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.loginAmbos(correo, pass)
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {
            viewModel.usuario.collect { user ->
                if (user != null) {
                    // Guardar datos en SharedPreferences
                    val prefs = getSharedPreferences("userPrefs", MODE_PRIVATE)
                    val editor = prefs.edit()

                    editor.putInt("idUsuario", user.idUsuario)
                    editor.putBoolean("esAdmin", user.idRol == 1)

                    when (user.idRol) {
                        1 -> { // Admin
                            Toast.makeText(this@LoginActivity, "Bienvenido Admin", Toast.LENGTH_SHORT).show()
                            editor.apply()
                            val intent = Intent(this@LoginActivity, AdminActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        2 -> { // Empleado
                            Toast.makeText(this@LoginActivity, "Bienvenido Empleado", Toast.LENGTH_SHORT).show()
                            // Guardar idSucursal del empleado
                            editor.putInt("idSucursalUsuario", user.idSucursal ?: -1)
                            editor.apply() // Guardar cambios
                            val intent = Intent(this@LoginActivity, EmpleadosActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else -> {
                            Toast.makeText(this@LoginActivity, "Rol desconocido", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        // Manejo de errores de login
        lifecycleScope.launch {
            viewModel.loginError.collect { errorMessage ->
                if (errorMessage != null) {
                    Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
                    txtPassword.text?.clear()
                }
            }
        }
    }
}
