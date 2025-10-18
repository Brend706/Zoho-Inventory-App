package com.zoho.inventarioapp.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fechas_productos",
    foreignKeys = [
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["idProducto"],
            childColumns = ["idProducto"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("idProducto")
    ]
)
data class FechasProducto(
    @PrimaryKey(autoGenerate = true)
    val idFechasP: Int = 0,
    @ColumnInfo(name = "idProducto")
    val idProducto: Int,
    @ColumnInfo(name = "fecha_fabricacion")
    val fechaFabricacion: Long,
    @ColumnInfo(name = "fecha_caducidad")
    val fechaCaducidad: Long
)