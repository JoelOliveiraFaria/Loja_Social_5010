package com.example.lojasocial.ui.pedidos

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun RecusarPedidoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var texto by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Recusar Pedido") },
        text = {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                label = { Text("Motivo da recusa") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(texto.text)
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
