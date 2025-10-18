package com.zoho.inventarioapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sucursales")
data class Sucursal (

    @PrimaryKey(autoGenerate = true)
    val idSucursal: Int = 0,
    val nombre: String,
    val direccion: String,
    val telefono: String
)