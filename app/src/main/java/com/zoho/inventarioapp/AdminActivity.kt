package com.zoho.inventarioapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = getColor(R.color.morado_suave)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
        setContentView(R.layout.activity_admin)
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val cardUsuarios = findViewById<CardView>(R.id.cardUsuarios)
        val cardProductos = findViewById<CardView>(R.id.cardProductos)
        val cardInventario = findViewById<CardView>(R.id.cardInventario)
        val cardMovimientos = findViewById<CardView>(R.id.cardMovimientoInventario)
        val cardSucursales = findViewById<CardView>(R.id.cardSucursales)

        // Abrir las vistas usando Intents a MainActivity
        cardUsuarios.setOnClickListener {
            abrirEnMainActivity("usuarios")
        }

        cardProductos.setOnClickListener {
            abrirEnMainActivity("productos")
        }

        cardInventario.setOnClickListener {
            abrirEnMainActivity("inventario")
        }

        cardMovimientos.setOnClickListener {
            abrirEnMainActivity("movimientos")
        }

        cardSucursales.setOnClickListener {
            abrirEnMainActivity("sucursales")
        }
    }

    private fun abrirEnMainActivity(vista: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("vista", vista)
        startActivity(intent)
        finish()
    }

    // menu emergente (el icono de 3 puntos)
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_editperfil -> {
                Toast.makeText(this, "Editar Perfil", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_cerrarsesion -> {
                val prefs = getSharedPreferences("userPrefs", MODE_PRIVATE)
                prefs.edit().clear().apply()

                // Redirigir al LoginActivity
                val intent = Intent(this, com.zoho.inventarioapp.ui.login.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish() // cerrar MainActivity
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}