package com.example.fixnearv1.iuu.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fixnearv1.modelo.entity.Producto

@Composable
fun ProductoCard(
    producto: Producto,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Nombre: ${producto.nombre}", style = MaterialTheme.typography.titleSmall)
            Text("Precio: $${producto.precio}")
            Text("Stock: ${producto.stock}")
            if (producto.categoria.isNotEmpty()) Text("Categoría: ${producto.categoria}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onEliminar) {
                Text("Eliminar")
            }
        }
    }
}
