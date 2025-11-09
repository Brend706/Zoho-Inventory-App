package com.zoho.inventarioapp.ui.usuarios

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        btnEliminar.setOnClickListener {
            eliminarUsuarioConConfirmacion(usuario)
        }

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
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val rolText = etRol.text.toString().trim()
                val password = etPassword.text.toString().trim()
                val sucursalText = etSucursal.text.toString().trim()

                // Validaciónes
                if (nombre.isEmpty() || correo.isEmpty() || rolText.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Todos los campos son obligatorios.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val nuevoUsuario = Usuario(
                    codUsuario = UUID.randomUUID().toString(),
                    nombre = nombre,
                    correo = correo,
                    password = password,
                    idRol = rolText.toIntOrNull() ?: 1,
                    idSucursal = sucursalText.toIntOrNull()
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
                val nombre = etNombre.text.toString().trim()
                val correo = etCorreo.text.toString().trim()
                val rolText = etRol.text.toString().trim()
                val sucursalText = etSucursal.text.toString().trim()

                // Validaciónes
                if (nombre.isEmpty() || correo.isEmpty() || rolText.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "Todos los campos obligatorios.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val actualizado = usuario.copy(
                    nombre = nombre,
                    correo = correo,
                    idRol = rolText.toIntOrNull() ?: usuario.idRol,
                    idSucursal = sucursalText.toIntOrNull()
                )
                viewModel.editarUsuario(actualizado)
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

    private fun eliminarUsuarioConConfirmacion(usuario: Usuario) {
        val dialog = AlertDialog.Builder(requireContext(), R.style.EstiloDialog)
            .setTitle("Eliminar Usuario")
            .setMessage("¿Estás seguro de que deseas eliminar a ${usuario.nombre}?")
            .setPositiveButton("Sí") { _, _ ->
                viewModel.eliminarUsuario(usuario)
            }
            .setNegativeButton("Cancelar", null)
            .create()
 
        dialog.show()

        // Botones personalizados
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).apply {
            setTextColor(resources.getColor(R.color.morado_mas_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).apply {
            setTextColor(resources.getColor(R.color.morado_mas_suave))
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
    }

}
