package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lojasocial.models.ItemEntrega
import com.example.lojasocial.models.Produto

private val IpcaButtonGreen = Color(0xFF1F6F43)

@Composable
fun ProdutoItemCard(
    item: ItemEntrega,
    onAumentar: () -> Unit,
    onDiminuir: () -> Unit,
    onRemover: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.produtoNome ?: "Produto",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                fontSize = 16.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDiminuir) {
                        Text("-", fontSize = 24.sp, color = Color.Black)
                    }
                    Text(
                        text = item.lotesConsumidos.sumOf { it.quantidade }.toString(),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    IconButton(onClick = onAumentar) {
                        Text("+", fontSize = 24.sp, color = IpcaButtonGreen)
                    }
                }
                Text(
                    text = "Remover",
                    color = Color.Red,
                    modifier = Modifier.clickable { onRemover() },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun InventarioDialog(
    produtos: List<Produto>,
    onProdutoSelected: (Produto) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Inventário Disponível", fontWeight = FontWeight.Bold) },
        text = {
            Box(modifier = Modifier.heightIn(max = 400.dp)) {
                LazyColumn {
                    items(produtos) { produto ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProdutoSelected(produto) }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Text(text = produto.nome ?: "", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                            Text(text = "Stock: ${produto.quantidadeTotal}", color = Color.Gray, fontSize = 13.sp)
                        }
                        HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        }
    )
}