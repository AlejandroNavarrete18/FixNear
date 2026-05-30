package com.example.fixnearv1.modelo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "producto")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "proveedor_id")
    val proveedorId: Int = 0,
    @ColumnInfo(name = "categoria_id")
    val categoriaId: Int = 0,
    val nombre: String,
    val descripcion: String? = null,
    val precio: Double,
    val stock: Int = 0,
    @ColumnInfo(name = "imagen_url")
    val imagenUrl: String? = null,
    val activo: Int = 1,
    @ColumnInfo(name = "fecha_creacion")
    val fechaCreacion: String = "",
    // Campos legacy del código original
    @ColumnInfo(name = "codigo_qr")
    val codigoQR: String = "",
    val cantidad: Int = 0,
    val categoria: String = ""
)
