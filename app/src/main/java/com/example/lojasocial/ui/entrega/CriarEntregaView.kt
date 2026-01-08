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
import com.example.lojasocial.models.Produto
import com.example.lojasocial.ui.components.TopBarWithMenu
import androidx.compose.ui.Alignment.Companion.CenterVertically



private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val ButtonGreenLight = Color(0xFF2C6B55)
private val CardGreen = Color(0xFF1F6F43)
private val TextWhite = Color.White

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CriarEntregaView(
    navController: NavController,
    beneficiarioId: String?,
    pedidoId: String?,
    viewModel: CriarEntregaViewModel = hiltViewModel()
) {
    var mostrarDialog by remember { mutableStateOf(false) }
    var produtoSelecionado by remember { mutableStateOf<Produto?>(null) }
    var quantidade by remember { mutableStateOf("") }
    var dropdownAberto by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.iniciarEntrega(beneficiarioId, pedidoId) }

    val entrega by viewModel.entrega
    val produtos by viewModel.produtosDisponiveis
    val erro by viewModel.erro

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarWithMenu(navController)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 24.dp),
            verticalAlignment = CenterVertically
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
                text = "Criar Entrega",
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
            // Beneficiário
            if (beneficiarioId != null) {
                Text(
                    text = "Beneficiário: ${viewModel.nomeBeneficiario.value}",
                    color = TextWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            } else {
                Text(
                    text = "Selecionar Beneficiário",
                    color = TextWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = ButtonGreenLight),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { dropdownAberto = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = CenterVertically
                    ) {
                        Text(
                            text = viewModel.nomeBeneficiario.value.ifBlank { "Escolher Beneficiário" },
                            color = TextWhite,
                            fontSize = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (dropdownAberto) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = BgGreen),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LazyColumn(
                            modifier = Modifier.heightIn(max = 300.dp)
                        ) {
                            items(viewModel.beneficiarios.value) { b ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            viewModel.setBeneficiarioManual(b.id!!, b.nome ?: "")
                                            dropdownAberto = false
                                        },
                                    colors = CardDefaults.cardColors(containerColor = ButtonGreen),
                                    shape = RoundedCornerShape(bottomStart = 0.dp, bottomEnd = 0.dp)
                                ) {
                                    Text(
                                        text = b.nome ?: "",
                                        color = TextWhite,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (pedidoId != null) {
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
                    CardProdutoEntrega(
                        item = item,
                        onDiminuir = { viewModel.diminuirQuantidade(item.produtoId) },
                        onAumentar = { viewModel.aumentarQuantidade(item.produtoId) },
                        onRemover = { viewModel.removerProduto(item.produtoId) }
                    )
                }
            }

            Button(
                onClick = { mostrarDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text("Adicionar Produto", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextWhite)
            }

            Button(
                onClick = {
                    viewModel.salvarEntrega(
                        onSuccess = { idEntrega -> navController.navigate("entrega/detalhes/$idEntrega") },
                        onError = { msg -> viewModel.erro.value = msg }
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = CardGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Finalizar Entrega", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextWhite)
            }

            erro?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }


    if (mostrarDialog) {
        Dialog(onDismissRequest = {
            mostrarDialog = false
            produtoSelecionado = null
            quantidade = ""
        }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = BgGreen),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = produtoSelecionado?.nome ?: "Selecionar Produto",
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )

                    if (produtoSelecionado == null) {
                        produtos.forEach { produto ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { produtoSelecionado = produto }
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = ButtonGreenLight),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = produto.nome ?: "",
                                    color = TextWhite,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
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
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                        Row(horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = {
                                mostrarDialog = false
                                produtoSelecionado = null
                                quantidade = ""
                            }) {
                                Text("Cancelar", color = LineGreen)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
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
                                Text("Confirmar", color = TextWhite)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CardProdutoEntrega(
    item: com.example.lojasocial.models.ItemEntrega,
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
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("-", fontSize = 20.sp, color = TextWhite)
                }
                Text(
                    text = item.lotesConsumidos.sumOf { it.quantidade }.toString(),
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = onAumentar,
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("+", fontSize = 20.sp, color = TextWhite)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onRemover,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Remover", color = TextWhite, fontSize = 12.sp)
                }
            }
        }
    }
}