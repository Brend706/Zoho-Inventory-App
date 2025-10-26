package com.zoho.inventarioapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configuración de navegación
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNav.setupWithNavController(navController)

        // Revisar si MainActivity fue abierta desde AdminActivity
        val vista = intent.getStringExtra("vista")
        if (vista != null) {
            when (vista) {
                "usuarios" -> bottomNav.selectedItemId = R.id.navigation_usuarios
                "productos" -> bottomNav.selectedItemId = R.id.navigation_productos
            }
        }
    }
}
