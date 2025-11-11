package com.zoho.inventarioapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.view.GravityCompat
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración barra de estado
        WindowCompat.setDecorFitsSystemWindows(window, false)
//window.statusBarColor = getColor(R.color.morado_suave)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }

        setContentView(R.layout.activity_main)

        // Leer si el usuario es admin
        val prefs = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val esAdmin = prefs.getBoolean("esAdmin", false)

        // Abrir menú correspondiente desde el logo de la app
        val logoApp = findViewById<ImageView>(R.id.logo_app)
        logoApp.setOnClickListener {
            val intent = if (esAdmin) {
                Intent(this, AdminActivity::class.java)
            } else {
                Intent(this, EmpleadosActivity::class.java)
            }
            startActivity(intent)
        }

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Configuración de navegación
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)

        // Ocultar opción de Usuarios si no es admin
        if (!esAdmin) {
            bottomNav.menu.findItem(R.id.navigation_usuarios)?.isVisible = false
        }

        bottomNav.setupWithNavController(navController)

        // Revisar si MainActivity fue abierta desde otra vista
        val vista = intent.getStringExtra("vista")
        if (vista != null) {
            when (vista) {
                "usuarios" -> if (esAdmin) bottomNav.selectedItemId = R.id.navigation_usuarios
                "productos" -> bottomNav.selectedItemId = R.id.navigation_productos
                "sucursales" -> bottomNav.selectedItemId = R.id.navigation_sucursales
            }
        }

        // Panel de notificaciones
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val btnNotificaciones = findViewById<ImageButton>(R.id.btn_notificaciones)
        btnNotificaciones.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
            } else {
                drawerLayout.openDrawer(GravityCompat.END)
            }
        }
    }

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
