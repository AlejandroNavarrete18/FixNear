package com.example.fixnearv1.modelo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val codigoQR: String,
    val cantidad: Int,
    val precio: Double,
    val categoria: String
)