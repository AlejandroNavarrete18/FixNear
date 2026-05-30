package com.example.fixnearv1.modelo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimiento")
data class Movimiento(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "usuario_id")
    val usuarioId: Int,
    val tipo: String, // 'ingreso', 'egreso', 'reembolso', 'comision'
    @ColumnInfo(name = "referencia_tipo")
    val referenciaTipo: String = "otro",
    @ColumnInfo(name = "referencia_id")
    val referenciaId: Int? = null,
    val monto: Double,
    val descripcion: String? = null,
    val fecha: String = ""
)
