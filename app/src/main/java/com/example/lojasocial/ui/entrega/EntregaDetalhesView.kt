package com.example.lojasocial.ui.entrega

import android.app.DatePickerDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.R
import com.example.lojasocial.models.EntregaStatus
import java.text.SimpleDateFormat
import java.util.*

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun EntregaDetalhesView(
    navController: NavController,
    entregaId: String,
    viewModel: EntregaDetalhesViewModel = hiltViewModel()
) {
    val entrega by viewModel.entrega
    val produtosInventario by viewModel.produtosDisponiveis.collectAsState()
    val avisoStock by viewModel.avisoStock
    val context = LocalContext.current
    var showProdutoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(entregaId) { viewModel.carregarEntrega(entregaId) }

    // DatePicker Logic
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d ->
            calendar.set(y, m, d)
            viewModel.atualizarData(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        containerColor = Color(0xFF0B3B2E),
        bottomBar = {
            Button(
                onClick = { viewModel.guardar { navController.popBackStack() } },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F6F43)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar Alterações", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).statusBarsPadding()) {

            // Header (Logo + Back)
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                }
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    modifier = Modifier.height(50.dp).align(Alignment.Center).clickable { navController.navigate("welcome") },
                    contentScale = ContentScale.Fit
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                item { Text("Editar Entrega", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold) }

                item {
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Beneficiário: ${viewModel.nomeBeneficiario.value}", fontWeight = FontWeight.Bold)
                            Text("Pedido: ${viewModel.textoPedido.value}", fontSize = 14.sp, color = Color.Gray)

                            Spacer(Modifier.height(12.dp))
                            Text("Estado", fontSize = 12.sp, color = Color.Gray)

                            // CORREÇÃO: Row com weights para não cortar o texto
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                EntregaStatus.values().forEach { status ->
                                    FilterChip(
                                        modifier = Modifier.weight(1f),
                                        selected = entrega?.status == status,
                                        onClick = { viewModel.atualizarStatus(status) },
                                        label = {
                                            Text(status.name, fontSize = 9.sp, maxLines = 1)
                                        }
                                    )
                                }
                            }

                            if (entrega?.status == EntregaStatus.ENTREGUE) {
                                OutlinedButton(onClick = { datePickerDialog.show() }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                                    Icon(Icons.Default.DateRange, null)
                                    Spacer(Modifier.width(8.dp))
                                    val dataT = entrega?.dataEntrega?.let { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it)) } ?: "Data de Entrega"
                                    Text(dataT)
                                }
                            }
                        }
                    }
                }

                item {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text("Produtos", color = Color.White.copy(alpha = 0.7f))
                        TextButton(onClick = { showProdutoDialog = true }) {
                            Icon(Icons.Default.Add, null, tint = Color.White)
                            Text("Adicionar", color = Color.White)
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
            onProdutoSelected = { viewModel.adicionarProduto(it); showProdutoDialog = false },
            onDismiss = { showProdutoDialog = false }
        )
    }
}