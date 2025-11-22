package com.zoho.inventarioapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.zoho.inventarioapp.data.local.dao.*
import com.zoho.inventarioapp.data.local.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Rol::class,
        Sucursal::class,
        Categoria::class,
        Usuario::class,
        Producto::class,
        FechasProducto::class,
        InventarioSucursal::class,
        MovimientosInventario::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun rolDao(): RolDao
    abstract fun sucursalDao(): SucursalDao
    abstract fun categoriaDao(): CategoriaDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun productoDao(): ProductoDao
    abstract fun fechasProductoDao(): FechasProductoDao
    abstract fun inventarioSucursalDao(): InventarioSucursalDao
    abstract fun movimientosInventarioDao(): MovimientosInventarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventario_database"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            insertarDatos(context)
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private fun insertarDatos(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                val database = getDatabase(context)

                // 1. Insertar rol Administrador y empleado
                database.rolDao().insertarTodos(listOf(
                    Rol(tipoRol = "Administrador"),
                    Rol(tipoRol = "Empleado")
                ))

                // 2. Insertar sucursal por defecto
                database.sucursalDao().insertar(
                    Sucursal(
                        nombre = "Sucursal Central",
                        direccion = "San Salvador, El Salvador",
                        telefono = "2222-2222"
                    )
                )

                // 3. Insertar usuario Admin (sin sucursal asignada)
                database.usuarioDao().insertar(
                    Usuario(
                        codUsuario = "ADMIN001",
                        nombre = "Administrador",
                        correo = "admin@zoho.com",
                        password = "admin123",
                        idRol = 1,        // ID del rol Administrador
                        idSucursal = null   //relacionado a ninguna sucursal
                    )
                )
            }
        }
    }
}