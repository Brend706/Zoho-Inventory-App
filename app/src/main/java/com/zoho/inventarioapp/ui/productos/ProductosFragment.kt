package com.zoho.inventarioapp.ui.productos

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.zoho.inventarioapp.R
import com.zoho.inventarioapp.data.local.entities.Producto
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.*
import com.zoho.inventarioapp.ui.categorias.CategoriasActivity

class ProductosFragment : Fragment() {

    private val viewModel: ProductosViewModel by viewModels()
    private lateinit var contenedorProductos: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_productos, container, false)
        contenedorProductos = view.findViewById(R.id.contenedorProductos)

        view.findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.btnCrearProductos
        ).setOnClickListener {
            mostrarDialogoCrearProducto()
        }

        val btnVerCategorias = view.findViewById<Button>(R.id.btnVerCategorias)

        // Validar que el boton Categorias solo sea visible si el usuario es Administrador
        val sharedPrefs = requireContext().getSharedPreferences("userPrefs", android.content.Context.MODE_PRIVATE)
        val esAdmin = sharedPrefs.getBoolean("esAdmin", false)

        if (esAdmin) {
            btnVerCategorias.visibility = View.VISIBLE
            btnVerCategorias.setOnClickListener {
                val intent = Intent(requireContext(), CategoriasActivity::class.java)
                startActivity(intent)
            }
        } else {
            btnVerCategorias.visibility = View.GONE
        }

        observarProductos()
        return view
    }

    private fun observarProductos() {
        lifecycleScope.launch {
            viewModel.todosLosProductos.collect { lista ->
                contenedorProductos.removeAllViews()
                lista.forEach { producto ->
                    crearCardProducto(producto)
                }
            }
        }
    }

    private fun crearCardProducto(producto: Producto) {
        val card = layoutInflater.inflate(R.layout.item_productos, contenedorProductos, false)

        val tvNombre = card.findViewById<TextView>(R.id.tvNombreProducto)
        val tvCodigo = card.findViewById<TextView>(R.id.tvCodProducto)
        val tvCategoria = card.findViewById<TextView>(R.id.tvCategorias)
        val tvStock = card.findViewById<TextView>(R.id.tvStockMinimo)
        val tvFecha = card.findViewById<TextView>(R.id.tvFechaCreacion)
        val btnEditar = card.findViewById<ImageButton>(R.id.btnEditar)
        val btnEliminar = card.findViewById<ImageButton>(R.id.btnEliminar)

        tvNombre.text = producto.nombre
        tvCodigo.text = "Código: ${producto.codProducto}"

        val nombreCategoria = viewModel.categorias.value.find { it.idCategoria == producto.idCategoria }?.nombre ?: "Sin categoría"
        tvCategoria.text = "Categoría: $nombreCategoria"

        tvStock.text = "Stock mínimo: ${producto.stockMinimo}"

        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaFormateada = sdf.format(Date(producto.fechaCreacion))
        tvFecha.text = "Creado: $fechaFormateada"

        btnEditar.setOnClickListener { mostrarDialogoEditarProducto(producto) }
        btnEliminar.setOnClickListener { eliminarProductoConConfirmacion(producto) }

        contenedorProductos.addView(card)
    }

    private fun mostrarDialogoCrearProducto() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_productos, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etCodigo = dialogView.findViewById<EditText>(R.id.etCodigo)
        val etStock = dialogView.findViewById<EditText>(R.id.etStockMinimo)
        val spCategoria = dialogView.findViewById<Spinner>(R.id.spCategoria)

        // Cargar categorías
        lifecycleScope.launch {
            viewModel.categorias.collect { categorias ->
                val listaCategorias = mutableListOf("Selecciona una categoría")
                listaCategorias.addAll(categorias.map { it.nombre })
                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listaCategorias)
                adapter.setDropDownViewResource(R.layout.item_spinner)
                spCategoria.adapter = adapter
            }
        }

        val builder = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
        builder.setTitle("Crear Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val codigo = etCodigo.text.toString().trim()
                val stockStr = etStock.text.toString().trim()
                val categoriaSeleccionada = spCategoria.selectedItemPosition

                if (nombre.isEmpty() || codigo.isEmpty() || stockStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                if (categoriaSeleccionada == 0) {
                    Toast.makeText(requireContext(), "Selecciona una categoría válida", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val stock = try { stockStr.toInt() } catch (e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Stock debe ser un número válido", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val idCategoria = viewModel.categorias.value.getOrNull(categoriaSeleccionada - 1)?.idCategoria ?: 1

                val nuevoProducto = Producto(
                    codProducto = codigo,
                    nombre = nombre,
                    stockMinimo = stock,
                    idCategoria = idCategoria,
                    fechaCreacion = System.currentTimeMillis()
                )

                lifecycleScope.launch {
                    try {
                        viewModel.agregarProducto(nuevoProducto)
                        Toast.makeText(requireContext(), "Producto creado correctamente", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Error al crear producto: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)

        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setTextColor(resources.getColor(R.color.morado_suave))
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setTextColor(resources.getColor(R.color.morado_suave))
        }
    }

    private fun mostrarDialogoEditarProducto(producto: Producto) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_productos, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etCodigo = dialogView.findViewById<EditText>(R.id.etCodigo)
        val etStock = dialogView.findViewById<EditText>(R.id.etStockMinimo)
        val spCategoria = dialogView.findViewById<Spinner>(R.id.spCategoria)

        etNombre.setText(producto.nombre)
        etCodigo.setText(producto.codProducto)
        etStock.setText(producto.stockMinimo.toString())

        // Cargar categorías
        lifecycleScope.launch {
            viewModel.categorias.collect { categorias ->
                val listaCategorias = mutableListOf("Selecciona categoría")
                listaCategorias.addAll(categorias.map { it.nombre })
                val adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, listaCategorias)
                adapter.setDropDownViewResource(R.layout.item_spinner)
                spCategoria.adapter = adapter

                val index = categorias.indexOfFirst { it.idCategoria == producto.idCategoria }
                if (index != -1) spCategoria.setSelection(index + 1)
            }
        }

        val builder = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
        builder.setTitle("Editar Producto")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = etNombre.text.toString().trim()
                val codigo = etCodigo.text.toString().trim()
                val stock = etStock.text.toString().trim()
                val catPos = spCategoria.selectedItemPosition

                if (nombre.isEmpty() || codigo.isEmpty() || stock.isEmpty() || catPos == 0) {
                    Toast.makeText(requireContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val idCategoria = viewModel.categorias.value.getOrNull(catPos - 1)?.idCategoria ?: producto.idCategoria

                val productoActualizado = producto.copy(
                    nombre = nombre,
                    codProducto = codigo,
                    stockMinimo = stock.toInt(),
                    idCategoria = idCategoria
                )

                viewModel.editarProducto(productoActualizado)
            }
        val dialog = builder.create()
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.apply {
            setTextColor(resources.getColor(R.color.morado_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.apply {
            setTextColor(resources.getColor(R.color.morado_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }

    private fun eliminarProductoConConfirmacion(producto: Producto) {
        val dialog = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
            .setTitle("Eliminar Producto")
            .setMessage("¿Estás seguro de que deseas eliminar ${producto.nombre}?")
            .setPositiveButton("Sí") { _, _ -> viewModel.eliminarProducto(producto) }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }
}
