package com.example.fixnearv1.modelo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categoria")
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String? = null,
    val iconoUrl: String? = null,
    val activo: Int = 1
)
