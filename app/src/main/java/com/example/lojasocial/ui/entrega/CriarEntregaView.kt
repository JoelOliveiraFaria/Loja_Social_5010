package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarVoltar

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun CriarEntregaView(
    navController: NavController,
    pedidoId: String? = null,
    viewModel: CriarEntregaViewModel = hiltViewModel()
) {
    val entrega by viewModel.entrega
    val beneficiarioNome by viewModel.nomeBeneficiario
    val beneficiarios by viewModel.beneficiariosDisponiveis
    val produtosInventario by viewModel.produtosDisponiveis
    val avisoStock by viewModel.avisoStock

    var showBeneficiarioDialog by remember { mutableStateOf(false) }
    var showProdutoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.inicializar(pedidoId) }

    // --- POP-UP DE AVISO DE STOCK ---
    avisoStock?.let { mensagem ->
        AlertDialog(
            onDismissRequest = { viewModel.limparAviso() },
            title = { Text("Stock Insuficiente", color = Color.Red, fontWeight = FontWeight.Bold) },
            text = { Text(mensagem) },
            confirmButton = {
                TextButton(onClick = { viewModel.limparAviso() }) {
                    Text("OK", color = IpcaButtonGreen, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }

    Scaffold(
        containerColor = BgGreen,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            Surface(
                color = BgGreen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.salvarEntrega { navController.popBackStack() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                        .navigationBarsPadding(),
                    colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Criar Entrega", color = WhiteColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- NAVBAR (Seta + Logo) ---
            TopBarVoltar(navController = navController, title = null)

            Divider(color = Color(0xFF2C6B55))

            // --- TÍTULO ---
            Text(
                text = "Nova Entrega",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Beneficiário
                item {
                    Text("Beneficiário", color = WhiteColor.copy(alpha = 0.7f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = pedidoId == null) { showBeneficiarioDialog = true },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, null, tint = IpcaButtonGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (beneficiarioNome.isEmpty()) "Selecionar Beneficiário" else beneficiarioNome,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Header Produtos
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Produtos", color = WhiteColor.copy(alpha = 0.7f), fontSize = 14.sp)
                        TextButton(onClick = { showProdutoDialog = true }) {
                            Icon(Icons.Default.Add, null, tint = WhiteColor, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Adicionar", color = WhiteColor)
                        }
                    }
                }

                // Lista de Produtos
                items(entrega?.itens ?: emptyList()) { item ->
                    ProdutoItemCard(
                        item = item,
                        onAumentar = { viewModel.aumentarQuantidade(item.produtoId) },
                        onDiminuir = { viewModel.diminuirQuantidade(item.produtoId) },
                        onRemover = { viewModel.removerProduto(item.produtoId) }
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // --- DIALOGS (Mantidos) ---
    if (showProdutoDialog) {
        InventarioDialog(
            produtos = produtosInventario,
            onProdutoSelected = { p ->
                viewModel.adicionarProduto(p)
                showProdutoDialog = false
            },
            onDismiss = { showProdutoDialog = false }
        )
    }

    if (showBeneficiarioDialog) {
        AlertDialog(
            onDismissRequest = { showBeneficiarioDialog = false },
            title = { Text("Selecione o Beneficiário") },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(beneficiarios) { b ->
                        Text(
                            text = b.nome ?: "Sem Nome",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selecionarBeneficiarioManual(b)
                                    showBeneficiarioDialog = false
                                }
                                .padding(16.dp)
                        )
                        Divider(color = Color.LightGray.copy(alpha = 0.5f))
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showBeneficiarioDialog = false }) { Text("Fechar") } },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }
}