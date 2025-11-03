package com.zoho.inventarioapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.zoho.inventarioapp.ui.categorias.CategoriasActivity


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
        val cardCategoria = findViewById<CardView>(R.id.cardCategoria)

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

        cardCategoria.setOnClickListener {
            val intent = Intent(this, CategoriasActivity::class.java)
            startActivity(intent)
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
                android.widget.Toast.makeText(this, "Editar Perfil", android.widget.Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_cerrarsesion -> {
                android.widget.Toast.makeText(this, "Cerrar Sesion", android.widget.Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}