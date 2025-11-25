package com.zoho.inventarioapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.zoho.inventarioapp.data.local.database.AppDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración barra de estado
        WindowCompat.setDecorFitsSystemWindows(window, false)
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

        // Referencias paneles del drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        val btnNotificaciones = findViewById<ImageButton>(R.id.btn_notificaciones)

        val panelNotificaciones = findViewById<View>(R.id.notificaciones_panel)
        val panelPerfil = findViewById<View>(R.id.drawer_perfil)

        btnNotificaciones.setOnClickListener {

            // Si está abierto, solo ciérralo
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END)
                return@setOnClickListener
            }

            // Mostrar panel notificaciones
            panelNotificaciones.visibility = View.VISIBLE
            panelPerfil.visibility = View.GONE

            drawerLayout.openDrawer(GravityCompat.END)
        }

        // Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Configuración de navegación
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)

        if (!esAdmin) {
            bottomNav.menu.findItem(R.id.navigation_usuarios)?.isVisible = false
        }

        bottomNav.setupWithNavController(navController)

        // Redirecciones
        val vista = intent.getStringExtra("vista")
        Log.d("MainActivity", "Vista recibida: $vista")
        if (vista != null) {
            bottomNav.post {
                when (vista) {
                    "usuarios" -> if (esAdmin) {
                        navController.navigate(R.id.navigation_usuarios)
                        bottomNav.selectedItemId = R.id.navigation_usuarios
                    }
                    "categorias", "productos" -> {
                        navController.navigate(R.id.navigation_productos)
                        bottomNav.selectedItemId = R.id.navigation_productos
                    }
                    "inventario", "movimientos" -> {
                        navController.navigate(R.id.navigation_inventario)
                        bottomNav.selectedItemId = R.id.navigation_inventario
                    }
                    "sucursales" -> {
                        navController.navigate(R.id.navigation_sucursales)
                        bottomNav.selectedItemId = R.id.navigation_sucursales
                    }
                }
            }
        }

        // Botón cerrar perfil
        val btnCerrarPerfil = findViewById<Button>(R.id.btnCerrarPerfil)
        btnCerrarPerfil.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        cargarDatosPerfil()
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_editperfil -> {
                drawerLayout.openDrawer(GravityCompat.END)

                // Mostrar panel perfil
                findViewById<View>(R.id.drawer_perfil).visibility = View.VISIBLE

                // Ocultar panel notificaciones
                findViewById<View>(R.id.notificaciones_panel).visibility = View.GONE
                true
            }

            R.id.action_cerrarsesion -> {
                val prefs = getSharedPreferences("userPrefs", MODE_PRIVATE)
                prefs.edit().clear().apply()

                val intent = Intent(this, com.zoho.inventarioapp.ui.login.LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun cargarDatosPerfil() {
        val prefs = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)

        if (idUsuario != -1) {
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@MainActivity)
                val usuario = db.usuarioDao().obtenerPorId(idUsuario)

                if (usuario != null) {
                    val rol = db.rolDao().obtenerPorId(usuario.idRol)
                    val sucursal = usuario.idSucursal?.let { db.sucursalDao().obtenerPorId(it) }

                    findViewById<TextView>(R.id.tvPerfilNombre).text = usuario.nombre
                    findViewById<TextView>(R.id.tvPerfilCorreo).text = usuario.correo
                    findViewById<TextView>(R.id.tvPerfilRol).text = rol?.tipoRol ?: "Sin rol"
                    findViewById<TextView>(R.id.tvPerfilSucursal).text = sucursal?.nombre ?: "Sin sucursal"
                    findViewById<TextView>(R.id.tvPerfilCodigo).text = usuario.codUsuario
                }
            }
        }
    }
}
