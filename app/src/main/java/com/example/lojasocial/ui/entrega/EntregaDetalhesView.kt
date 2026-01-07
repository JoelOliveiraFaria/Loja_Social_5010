package com.example.lojasocial.ui.entrega

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.Produto
import com.example.lojasocial.models.ItemEntrega
import com.example.lojasocial.ui.components.TopBarWithMenu

// Cores
private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
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

    // Carrega a entrega existente
    LaunchedEffect(Unit) {
        viewModel.carregarEntrega(entregaId)
    }

    val entrega by viewModel.entrega
    val nome by viewModel.nomeBeneficiario
    val pedido by viewModel.textoPedido
    val erro by viewModel.erro

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
            .padding(16.dp)
    ) {
        // TopBar já existente
        TopBarWithMenu(navController)

        Text(
            "Detalhes da Entrega",
            color = TextWhite,
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        Text("Beneficiário: $nome", color = TextWhite)
        if (pedido.isNotBlank()) Text("Pedido: $pedido", color = TextWhite)

        Spacer(Modifier.height(16.dp))

        // Lista de produtos da entrega
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(entrega?.itens ?: emptyList()) { item ->
                Card(
                    colors = CardDefaults.cardColors(LineGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(item.produtoNome ?: "", color = TextWhite)
                        Text(
                            "Qtd: ${item.lotesConsumidos.sumOf { l -> l.quantidade }}",
                            color = TextWhite
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // Botão Adicionar Produto
        Button(
            onClick = { mostrarDialog = true },
            colors = ButtonDefaults.buttonColors(ButtonGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Adicionar Produto", color = TextWhite)
        }

        Spacer(Modifier.height(8.dp))

        // Botão Guardar Alterações
        Button(
            onClick = {
                viewModel.salvarAlteracoes {
                    navController.popBackStack()
                }
            },
            colors = ButtonDefaults.buttonColors(ButtonGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Guardar Alterações", color = TextWhite)
        }

        Spacer(Modifier.height(8.dp))

        // Botão Marcar como Terminada
        Button(
            onClick = {
                viewModel.marcarComoTerminada {
                    navController.popBackStack()
                }
            },
            colors = ButtonDefaults.buttonColors(Color(0xFF2C6B55)),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Marcar como Terminada", color = TextWhite)
        }

        erro?.let { Text(it, color = Color.Red) }
    }

    // ---------------- Dialog para adicionar produto ----------------
    if (mostrarDialog) {
        Dialog(onDismissRequest = {
            mostrarDialog = false
            produtoSelecionado = null
            quantidade = ""
        }) {
            Surface(color = LineGreen, modifier = Modifier.padding(16.dp)) {
                Column(Modifier.padding(16.dp)) {

                    Text(
                        produtoSelecionado?.nome ?: "Selecionar Produto",
                        color = TextWhite,
                        fontSize = 20.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    val produtos = viewModel.produtosDisponiveis.collectAsState(initial = emptyList()).value

                    if (produtoSelecionado == null) {
                        produtos.forEach { produto ->
                            Button(
                                onClick = { produtoSelecionado = produto },
                                modifier = Modifier.fillMaxWidth().padding(4.dp),
                                colors = ButtonDefaults.buttonColors(ButtonGreen)
                            ) {
                                Text(produto.nome ?: "", color = TextWhite)
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = quantidade,
                            onValueChange = { quantidade = it },
                            label = { Text("Quantidade") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(12.dp))

                        Button(
                            onClick = {
                                quantidade.toIntOrNull()?.let {
                                    viewModel.adicionarProduto(produtoSelecionado!!, it)
                                }
                                mostrarDialog = false
                                produtoSelecionado = null
                                quantidade = ""
                            },
                            colors = ButtonDefaults.buttonColors(ButtonGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Confirmar", color = TextWhite)
                        }
                    }
                }
            }
        }
    }
}
