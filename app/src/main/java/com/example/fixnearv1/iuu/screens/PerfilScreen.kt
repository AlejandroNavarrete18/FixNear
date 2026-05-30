package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fixnearv1.modelo.entity.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    usuario: Usuario?,
    onRegresar: () -> Unit,
    onCerrarSesion: () -> Unit
) {

    var cuentaVerificada by remember { mutableStateOf(usuario?.emailVerificado == 1) }
    var mostrarDialogo by remember { mutableStateOf("") }

    val nombreCompleto = if (usuario != null) "${usuario.nombre} ${usuario.apellido}" else "Usuario"
    val rolTexto = when (usuario?.rol) {
        "trabajador" -> "Trabajador ClickWork"
        "admin" -> "Administrador"
        else -> "Cliente ClickWork"
    }

    if (mostrarDialogo.isNotEmpty()) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = "" },
            title = { Text(mostrarDialogo) },
            text = { Text("Funcionalidad disponible próximamente.") },
            confirmButton = {
                TextButton(onClick = { mostrarDialogo = "" }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B)
                        )
                    )
                ),
            contentPadding = PaddingValues(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Surface(
                    modifier = Modifier.size(105.dp),
                    shape = CircleShape,
                    color = Color(0xFF334155)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Foto de perfil",
                            tint = Color.White,
                            modifier = Modifier.size(88.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(text = nombreCompleto, color = Color.White, fontSize = 25.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = rolTexto, color = Color.LightGray, fontSize = 15.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Row {
                    Surface(shape = RoundedCornerShape(50.dp), color = Color(0xFF334155)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "%.1f".format(usuario?.calificacionPromedio ?: 0.0),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Surface(shape = RoundedCornerShape(50.dp), color = Color(0xFF334155)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = if (cuentaVerificada) Color(0xFF38BDF8) else Color.LightGray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (cuentaVerificada) "Verificada" else "No verificada",
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                PerfilSeccion {
                    PerfilFila(titulo = "Nombre", valor = usuario?.nombre ?: "") {
                        mostrarDialogo = "Editar nombre"
                    }
                    PerfilFila(titulo = "Apellido", valor = usuario?.apellido ?: "") {
                        mostrarDialogo = "Editar apellido"
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PerfilSeccion {
                    PerfilFila(
                        titulo = "Número de teléfono",
                        valor = usuario?.telefono?.let { t ->
                            if (t.length > 4) t.take(3) + "****" + t.takeLast(4) else t
                        } ?: "Sin registro"
                    ) { mostrarDialogo = "Editar teléfono" }

                    PerfilFila(
                        titulo = "Correo electrónico",
                        valor = usuario?.email ?: "Sin correo"
                    ) { mostrarDialogo = "Ver correo" }

                    PerfilFila(
                        titulo = "Correo verificado",
                        valor = if (cuentaVerificada) "Verificado" else "Sin verificar"
                    ) {
                        cuentaVerificada = true
                        mostrarDialogo = "Correo marcado como verificado"
                    }

                    PerfilFila(titulo = "Cambiar contraseña", valor = "") {
                        mostrarDialogo = "Cambiar contraseña"
                    }

                    PerfilFila(titulo = "Mis redes sociales", valor = "") {
                        mostrarDialogo = "Redes sociales"
                    }

                    PerfilFila(titulo = "Servicios solicitados", valor = "0") {
                        mostrarDialogo = "Historial de servicios"
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onCerrarSesion,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155))
                ) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Cerrar sesión", color = Color.White)
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { mostrarDialogo = "Eliminar mi cuenta" },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF6B6B))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Eliminar mi cuenta")
                }

                Spacer(modifier = Modifier.height(35.dp))
            }
        }
    }
}

@Composable
fun PerfilSeccion(contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF334155))
    ) {
        Column(modifier = Modifier.fillMaxWidth(), content = contenido)
    }
}

@Composable
fun PerfilFila(titulo: String, valor: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = titulo, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
            if (valor.isNotEmpty()) {
                Text(text = valor, color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(6.dp))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color(0xFF475569))
    }
}
