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
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
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

    LaunchedEffect(Unit) {
        viewModel.iniciarEntrega(beneficiarioId, pedidoId)
        viewModel.carregarProdutos()
    }

    val entrega by viewModel.entrega
    val nome by viewModel.nomeBeneficiario
    val pedido by viewModel.textoPedido
    val produtos by viewModel.produtosDisponiveis
    val erro by viewModel.erro

    Column(
        modifier = Modifier.fillMaxSize().background(BgGreen).padding(16.dp)
    ) {
        TopBarWithMenu(navController)

        Text("Criar Entrega", color = TextWhite, fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

        Spacer(Modifier.height(16.dp))

        Text("Beneficiário: $nome", color = TextWhite)
        if (pedido.isNotBlank()) Text("Pedido: $pedido", color = TextWhite)

        Spacer(Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(entrega?.itens ?: emptyList()) {
                Card(colors = CardDefaults.cardColors(LineGreen),
                    modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(it.produtoNome ?: "", color = TextWhite)
                        Text(
                            "Qtd: ${it.lotesConsumidos.sumOf { l -> l.quantidade }}",
                            color = TextWhite
                        )
                    }
                }
            }
        }

        Button(
            onClick = { mostrarDialog = true },
            colors = ButtonDefaults.buttonColors(ButtonGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("Adicionar Produto", color = TextWhite) }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.salvarEntrega(
                    onSuccess = { navController.popBackStack() },
                    onError = {}
                )
            },
            colors = ButtonDefaults.buttonColors(ButtonGreen),
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) { Text("Finalizar Entrega", color = TextWhite) }

        erro?.let { Text(it, color = Color.Red) }
    }

    /* ---------- MODAL ---------- */

    if (mostrarDialog) {
        Dialog(onDismissRequest = {
            mostrarDialog = false
            produtoSelecionado = null
            quantidade = ""
        }) {
            Surface(color = LineGreen, modifier = Modifier.padding(16.dp)) {
                Column(Modifier.padding(16.dp)) {

                    Text(produtoSelecionado?.nome ?: "Selecionar Produto",
                        color = TextWhite, fontSize = 20.sp)

                    Spacer(Modifier.height(12.dp))

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
                            singleLine = true
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
