package com.example.lojasocial.ui.entrega

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.EntregaStatus
import com.example.lojasocial.models.Produto
import com.example.lojasocial.models.ItemEntrega
import com.example.lojasocial.ui.components.TopBarWithMenu
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.foundation.layout.Arrangement

// Cores
private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val ButtonGreenLight = Color(0xFF2C6B55)
private val CardGreen = Color(0xFF1F6F43)
private val TextWhite = Color.White

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EntregaDetalhesView(
    navController: NavController,
    entregaId: String,
    viewModel: EntregaDetalhesViewModel = hiltViewModel()
) {
    var mostrarDialog by remember { mutableStateOf(false) }
    var produtoSelecionado by remember { mutableStateOf<Produto?>(null) }
    var quantidade by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.carregarEntrega(entregaId)
    }

    val entrega by viewModel.entrega
    val produtos by viewModel.produtosDisponiveis.collectAsState()
    val bloqueado = entrega?.status == EntregaStatus.TERMINADO

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarWithMenu(navController)
        Divider(color = LineGreen)


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = TextWhite,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Detalhes da Entrega",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Text(
                text = "Beneficiário: ${viewModel.nomeBeneficiario.value}",
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            if (viewModel.textoPedido.value.isNotBlank()) {
                Text(
                    text = "Pedido: ${viewModel.textoPedido.value}",
                    color = TextWhite,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }


            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entrega?.itens ?: emptyList()) { item ->
                    CardItemEntrega(
                        item = item,
                        bloqueado = bloqueado,
                        onDiminuir = { viewModel.diminuirQuantidade(item.produtoId) },
                        onAumentar = { viewModel.aumentarQuantidade(item.produtoId) },
                        onRemover = { viewModel.removerProduto(item.produtoId) }
                    )
                }
            }

            Button(
                onClick = { mostrarDialog = true },
                enabled = !bloqueado,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text("Adicionar Produto", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = { viewModel.salvarAlteracoes { navController.popBackStack() } },
                enabled = !bloqueado,
                colors = ButtonDefaults.buttonColors(containerColor = CardGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text("Guardar Alterações", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            if (!bloqueado) {
                Button(
                    onClick = { viewModel.marcarComoTerminada { navController.popBackStack() } },
                    colors = ButtonDefaults.buttonColors(containerColor = CardGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Marcar como Terminada", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }

    if (mostrarDialog) {
        Dialog(onDismissRequest = { mostrarDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BgGreen),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        "Adicionar Produto",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    produtos.forEach { produto ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { produtoSelecionado = produto }
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = ButtonGreenLight)
                        ) {
                            Text(
                                text = "${produto.nome} (${produto.quantidadeTotal})",
                                color = TextWhite,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    if (produtoSelecionado != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = quantidade,
                            onValueChange = { quantidade = it },
                            label = { Text("Quantidade", color = TextWhite.copy(alpha = 0.8f)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = LineGreen,
                                unfocusedBorderColor = ButtonGreenLight,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { mostrarDialog = false }) {
                                Text("Cancelar", color = LineGreen)
                            }
                            Button(
                                onClick = {
                                    quantidade.toIntOrNull()?.let { q ->
                                        viewModel.adicionarProduto(produtoSelecionado!!, q)
                                    }
                                    mostrarDialog = false
                                    produtoSelecionado = null
                                    quantidade = ""
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen)
                            ) {
                                Text("Confirmar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardItemEntrega(
    item: ItemEntrega,
    bloqueado: Boolean,
    onDiminuir: () -> Unit,
    onAumentar: () -> Unit,
    onRemover: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = LineGreen),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.produtoNome ?: "",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDiminuir,
                    enabled = !bloqueado,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonGreen,
                        disabledContainerColor = ButtonGreenLight.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("-", fontSize = 20.sp, color = TextWhite)
                }
                Text(
                    text = item.lotesConsumidos.sumOf { it.quantidade }.toString(),
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Button(
                    onClick = onAumentar,
                    enabled = !bloqueado,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonGreen,
                        disabledContainerColor = ButtonGreenLight.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("+", fontSize = 20.sp, color = TextWhite)
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onRemover,
                    enabled = !bloqueado
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remover",
                        tint = if (!bloqueado) Color.Red else Color.Red.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


