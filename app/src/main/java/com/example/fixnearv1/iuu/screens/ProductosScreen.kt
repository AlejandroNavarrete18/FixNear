package com.example.fixnearv1.iuu.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.fixnearv1.modelo.entity.Producto
import com.example.fixnearv1.viewmodel.ProductoViewModel

@Composable
fun ProductosScreen(
    viewModel: ProductoViewModel
) {

    val productos by viewModel.productos
        .collectAsStateWithLifecycle(
            initialValue = emptyList()
        )

    var nombre by remember { mutableStateOf("") }
    var codigoQR by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Registro de Productos",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = codigoQR,
            onValueChange = { codigoQR = it },
            label = { Text("Código QR") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                viewModel.agregarProducto(
                    nombre,
                    codigoQR,
                    cantidad,
                    precio,
                    categoria
                )

                nombre = ""
                codigoQR = ""
                cantidad = ""
                precio = ""
                categoria = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Producto")
        }

        Spacer(modifier = Modifier.height(20.dp))

        LazyColumn {

            items(productos) { producto ->

                ProductoItem(
                    producto = producto,
                    onEliminar = {
                        viewModel.eliminarProducto(producto)
                    }
                )
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onEliminar: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text("Nombre: ${producto.nombre}")

            Text("QR: ${producto.codigoQR}")

            Text("Cantidad: ${producto.cantidad}")

            Text("Precio: $${producto.precio}")

            Text("Categoría: ${producto.categoria}")

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = onEliminar) {
                Text("Eliminar")
            }
        }
    }
}