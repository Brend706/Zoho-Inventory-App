package com.zoho.inventarioapp.ui.usuarios

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.zoho.inventarioapp.R
//Importamos los atributos de la tabla Usuarios
import com.zoho.inventarioapp.data.local.entities.Usuario
import kotlinx.coroutines.launch
import java.util.*

class UsuariosFragment : Fragment() {

    private val viewModel: UsuariosViewModel by viewModels()
    private lateinit var contenedorUsuarios: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_usuarios, container, false)
        contenedorUsuarios = view.findViewById(R.id.contenedorUsuarios)

        view.findViewById<Button>(R.id.btnCrearUsuario).setOnClickListener {
            mostrarDialogoCrearUsuario()
        }

        observarUsuarios()
        return view
    }

    private fun observarUsuarios() {
        lifecycleScope.launch {
            viewModel.todosLosUsuarios.collect { lista ->
                contenedorUsuarios.removeAllViews()
                lista.forEach { usuario ->
                    crearCardUsuario(usuario)
                }
            }
        }
    }

    //Esta parte ocupa el item_usuario, fragment_usuario y dialog_usuario

    //Item_usurio es el modelo que tiene que tener cada card para cada usuario
    //fragment_usuario es la representacion del contenedor donde tiene que ir cada item_usuario
    //aparte ahi te define el boton crear (no encontre una manera de hacerlo en un solo archivo asique lo hice asi)
    //dialog_usuario es como los modals de mvc, es lo que vemos cuando le damos a crear o editar, y
    //ahi esta el diseño de esa parte
    private fun crearCardUsuario(usuario: Usuario) {
        // Inflamos un CardView a partir de un layout de ejemplo que solo tenga la estructura
        val card = layoutInflater.inflate(R.layout.item_usuario, contenedorUsuarios, false)

        // Referencias a los elementos del CardView
        val tvNombre = card.findViewById<TextView>(R.id.tvNombreUsuario)
        val tvCorreo = card.findViewById<TextView>(R.id.tvCorreoUsuario)
        val btnEditar = card.findViewById<Button>(R.id.btnEditar)
        val btnEliminar = card.findViewById<Button>(R.id.btnEliminar)

        // Asignamos los datos del usuario
        tvNombre.text = "Nombre: ${usuario.nombre}"
        tvCorreo.text = "Correo: ${usuario.correo}"

        // Listeners
        btnEditar.setOnClickListener { mostrarDialogoEditarUsuario(usuario) }
        btnEliminar.setOnClickListener { viewModel.eliminarUsuario(usuario) }

        // Agregamos el CardView al contenedor
        contenedorUsuarios.addView(card)
    }

    private fun mostrarDialogoCrearUsuario() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_usuario, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)
        val etRol = dialogView.findViewById<EditText>(R.id.etRol)
        val etSucursal = dialogView.findViewById<EditText>(R.id.etSucursal)
        val etPassword = dialogView.findViewById<EditText>(R.id.etPassword)

        AlertDialog.Builder(requireContext())
            .setTitle("Crear Usuario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoUsuario = Usuario(
                    codUsuario = UUID.randomUUID().toString(),
                    nombre = etNombre.text.toString(),
                    correo = etCorreo.text.toString(),
                    password = etPassword.text.toString(),
                    idRol = etRol.text.toString().toIntOrNull() ?: 1,
                    idSucursal = etSucursal.text.toString().toIntOrNull()
                )
                viewModel.agregarUsuario(nuevoUsuario)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEditarUsuario(usuario: Usuario) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_usuario, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etCorreo = dialogView.findViewById<EditText>(R.id.etCorreo)
        val etRol = dialogView.findViewById<EditText>(R.id.etRol)
        val etSucursal = dialogView.findViewById<EditText>(R.id.etSucursal)

        etNombre.setText(usuario.nombre)
        etCorreo.setText(usuario.correo)
        etRol.setText(usuario.idRol.toString())
        etSucursal.setText(usuario.idSucursal?.toString() ?: "")

        val builder = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
        builder.setTitle("Editar Usuario")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val actualizado = usuario.copy(
                    nombre = etNombre.text.toString(),
                    correo = etCorreo.text.toString(),
                    idRol = etRol.text.toString().toIntOrNull() ?: usuario.idRol,
                    idSucursal = etSucursal.text.toString().toIntOrNull()
                )
                viewModel.editarUsuario(actualizado)
            }
            .setNegativeButton("Cancelar", null)
            // Creamos una variables para modificar los botones
            val dialog = builder.create()
            dialog.show()

        // Botones personalizados
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setTextColor(resources.getColor(R.color.morado_suave))
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            setTextColor(resources.getColor(R.color.morado_suave))
        }
    }
}
