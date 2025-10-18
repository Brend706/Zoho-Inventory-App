package com.zoho.inventarioapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventario_sucursal",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["idProducto"],
            childColumns = ["idProducto"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Sucursal::class,
            parentColumns = ["idSucursal"],
            childColumns = ["idSucursal"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("idProducto"),
        Index("idSucursal"),
        Index(value = ["idProducto", "idSucursal"], unique = true)
    ]
)
data class InventarioSucursal(
    @PrimaryKey(autoGenerate = true)
    val idInventario: Int = 0,
    @ColumnInfo(name = "idProducto")
    val idProducto: Int,
    @ColumnInfo(name = "idSucursal")
    val idSucursal: Int,
    @ColumnInfo(name = "stock_actual")
    val stockActual: Int,
    @ColumnInfo(name = "ultima_actualizacion")
    val ultimaActualizacion: Long = System.currentTimeMillis()
)