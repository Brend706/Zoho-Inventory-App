package com.zoho.inventarioapp.data.local.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roles")
data class Rol (
    @PrimaryKey(autoGenerate = true)
    val idRol: Int = 0,
    val tipoRol: String
)
