package com.zoho.inventarioapp.ui.categorias

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import com.zoho.inventarioapp.AdminActivity
import com.zoho.inventarioapp.R
import com.zoho.inventarioapp.data.local.entities.Categoria
import kotlinx.coroutines.launch

class CategoriasActivity : AppCompatActivity() {

    private val viewModel: CategoriasViewModel by viewModels()
    private lateinit var adapter: CategoriaAdapter

    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvCategorias: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var btnAgregar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categorias)

        // Configurar toolbar
        toolbar = findViewById(R.id.topAppBar)
        //abrir menu admin desde el logo de la app
        val logoApp = findViewById<ImageView>(R.id.logo_app)
        logoApp.setOnClickListener {
            val intent = Intent(this, AdminActivity::class.java)
            startActivity(intent)
        }
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Inicializar vistas
        rvCategorias = findViewById(R.id.rvCategorias)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnAgregar = findViewById(R.id.btnCrearCategoria)

        setupRecyclerView()
        observarDatos()
        setupListeners()
    }



    private fun setupRecyclerView() {
        adapter = CategoriaAdapter(
            onEditClick = { categoria ->
                mostrarDialogoEditar(categoria)
            },
            onDeleteClick = { categoria ->
                confirmarEliminar(categoria)
            }
        )
        rvCategorias.adapter = adapter
    }

    private fun observarDatos() {
        // Observar lista de categorías
        lifecycleScope.launch {
            viewModel.categorias.collect { lista ->
                adapter.submitList(lista)

                if (lista.isEmpty()) {
                    tvEmpty.visibility = android.view.View.VISIBLE
                    rvCategorias.visibility = android.view.View.GONE
                } else {
                    tvEmpty.visibility = android.view.View.GONE
                    rvCategorias.visibility = android.view.View.VISIBLE
                }
            }
        }

        // Observar mensajes
        lifecycleScope.launch {
            viewModel.mensaje.collect { mensaje ->
                mensaje?.let {
                    Toast.makeText(this@CategoriasActivity, it, Toast.LENGTH_SHORT).show()
                    viewModel.limpiarMensaje()
                }
            }
        }
    }

    private fun setupListeners() {
        btnAgregar.setOnClickListener {
            mostrarDialogoAgregar()
        }
    }

    private fun mostrarDialogoAgregar() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_categoria_form, null)

        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre)

        AlertDialog.Builder(this, R.style.EstiloDialog)
            .setTitle("Agregar Categoría")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString()

                if (nombre.isNotEmpty()) {
                    viewModel.agregarCategoria(nombre)
                } else {
                    Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditar(categoria: Categoria) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_categoria_form, null)

        val etNombre = dialogView.findViewById<TextInputEditText>(R.id.etNombre)

        etNombre.setText(categoria.nombre)

        AlertDialog.Builder(this, R.style.EstiloDialog)
            .setTitle("Editar Categoría")
            .setView(dialogView)
            .setPositiveButton("Actualizar") { _, _ ->
                val nombre = etNombre.text.toString()

                if (nombre.isNotEmpty()) {
                    viewModel.actualizarCategoria(categoria.idCategoria, nombre)
                } else {
                    Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminar(categoria: Categoria) {
        AlertDialog.Builder(this, R.style.EstiloDialog)
            .setTitle("Eliminar Categoría")
            .setMessage("¿Estás seguro de eliminar '${categoria.nombre}'?")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.eliminarCategoria(categoria)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Menú de opciones (3 puntos)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_top_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_editperfil -> {
                Toast.makeText(this, "Editar Perfil", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_cerrarsesion -> {
                Toast.makeText(this, "Cerrar Sesión", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}