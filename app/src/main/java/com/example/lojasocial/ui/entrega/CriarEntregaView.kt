package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.R
import com.example.lojasocial.models.ItemEntrega

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
    val erro by viewModel.erro
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
            }
        )
    }

    Scaffold(
        containerColor = BgGreen,
        bottomBar = {
            Button(
                onClick = { viewModel.salvarEntrega { navController.popBackStack() } },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Criar Entrega", color = WhiteColor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).statusBarsPadding()) {

            // Header
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = WhiteColor)
                }
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.height(50.dp).align(Alignment.Center).clickable { navController.navigate("welcome") },
                    contentScale = ContentScale.Fit
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item { Text("Nova Entrega", style = MaterialTheme.typography.headlineMedium, color = WhiteColor, fontWeight = FontWeight.Bold) }

                // Beneficiário
                item {
                    Text("Beneficiário", color = WhiteColor.copy(alpha = 0.7f), fontSize = 14.sp)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable(enabled = pedidoId == null) { showBeneficiarioDialog = true },
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, tint = IpcaButtonGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = if (beneficiarioNome.isEmpty()) "Selecionar Beneficiário" else beneficiarioNome, color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Produtos
                item {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text("Produtos", color = WhiteColor.copy(alpha = 0.7f), fontSize = 14.sp)
                        TextButton(onClick = { showProdutoDialog = true }) {
                            Icon(Icons.Default.Add, null, tint = WhiteColor)
                            Text("Adicionar", color = WhiteColor)
                        }
                    }
                }

                items(entrega?.itens ?: emptyList()) { item ->
                    ProdutoItemCard(
                        item = item,
                        onAumentar = { viewModel.aumentarQuantidade(item.produtoId) },
                        onDiminuir = { viewModel.diminuirQuantidade(item.produtoId) },
                        onRemover = { viewModel.removerProduto(item.produtoId) }
                    )
                }
            }
        }
    }

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
                        Text(b.nome ?: "", modifier = Modifier.fillMaxWidth().clickable { viewModel.selecionarBeneficiarioManual(b); showBeneficiarioDialog = false }.padding(16.dp))
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showBeneficiarioDialog = false }) { Text("Fechar") } }
        )
    }
}
