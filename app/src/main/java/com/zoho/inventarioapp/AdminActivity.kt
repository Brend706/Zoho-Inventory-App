package com.zoho.inventarioapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val cardUsuarios = findViewById<CardView>(R.id.cardUsuarios)
        val cardProductos = findViewById<CardView>(R.id.cardProductos)
        val cardInventario = findViewById<CardView>(R.id.cardInventario)
        val cardMovimientos = findViewById<CardView>(R.id.cardMovimientoInventario)

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
    }

    private fun abrirEnMainActivity(vista: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("vista", vista)
        startActivity(intent)
        finish()
    }
}