package com.example.lojasocial.ui.pedidos

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ErrorRed = Color(0xFFEF5350)
private val TextWhite = Color.White

@Composable
fun RecusarPedidoDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var texto by remember { mutableStateOf(TextFieldValue("")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Recusar Pedido",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    "Motivo da recusa",
                    color = TextWhite.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = texto,
                    onValueChange = { texto = it },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LineGreen,
                        unfocusedBorderColor = LineGreen.copy(alpha = 0.7f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(texto.text) },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
            ) {
                Text("Confirmar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar", color = LineGreen)
            }
        },
        containerColor = BgGreen,
        titleContentColor = TextWhite,
        textContentColor = TextWhite
    )
}
