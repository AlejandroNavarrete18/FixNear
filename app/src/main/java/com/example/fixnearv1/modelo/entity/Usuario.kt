package com.example.fixnearv1.modelo.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuario")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val email: String,
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    val telefono: String? = null,
    @ColumnInfo(name = "foto_url")
    val fotoUrl: String? = null,
    val rol: String = "cliente",
    val activo: Int = 1,
    @ColumnInfo(name = "email_verificado")
    val emailVerificado: Int = 0,
    @ColumnInfo(name = "token_verificacion")
    val tokenVerificacion: String? = null,
    @ColumnInfo(name = "codigo_qr")
    val codigoQr: String? = null,
    val latitud: Double? = null,
    val longitud: Double? = null,
    @ColumnInfo(name = "fecha_registro")
    val fechaRegistro: String = "",
    val bio: String? = null,
    @ColumnInfo(name = "experiencia_anios")
    val experienciaAnios: Int = 0,
    val disponible: Int = 1,
    @ColumnInfo(name = "calificacion_promedio")
    val calificacionPromedio: Double = 0.0
)
