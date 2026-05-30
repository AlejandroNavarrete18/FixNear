package com.example.fixnearv1.modelo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "postulacion")
data class Postulacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "vacante_id")
    val vacanteId: Int,
    @ColumnInfo(name = "trabajador_id")
    val trabajadorId: Int,
    val mensaje: String? = null,
    val estado: String = "pendiente",
    @ColumnInfo(name = "fecha_postulacion")
    val fechaPostulacion: String = ""
)
