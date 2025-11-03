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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = getColor(R.color.morado_suave)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
        }
        setContentView(R.layout.activity_main)

        //abrir menu admin desde el logo de la app
        val logoApp = findViewById<ImageView>(R.id.logo_app)
        logoApp.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
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
        bottomNav.setupWithNavController(navController)

        // Revisar si MainActivity fue abierta desde AdminActivity
        val vista = intent.getStringExtra("vista")
        if (vista != null) {
            when (vista) {
                "usuarios" -> bottomNav.selectedItemId = R.id.navigation_usuarios
                "productos" -> bottomNav.selectedItemId = R.id.navigation_productos
            }
        }

        //panel de notificaciones
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

    // menu emergente (el icono de 3 puntos)
    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_bar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            //por el momento solo muestran un mensaje las opciones jajajjj
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
