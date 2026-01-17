package com.example.lojasocial.ui.entrega

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.EntregaStatus
import com.example.lojasocial.ui.components.TopBarVoltar
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
    val context = LocalContext.current
    var showProdutoDialog by remember { mutableStateOf(false) }

    LaunchedEffect(entregaId) {
        viewModel.carregarEntrega(entregaId)
    }

    // Lógica do DatePicker
    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, y, m, d ->
            calendar.set(y, m, d)
            viewModel.atualizarData(calendar.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        containerColor = BgGreen,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            Surface(
                color = BgGreen,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { viewModel.guardar { navController.popBackStack() } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                        .navigationBarsPadding(),
                    colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Guardar Alterações", color = WhiteColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TopBarVoltar(navController = navController, title = null)

            Divider(color = Color(0xFF2C6B55))

            Text(
                text = "Editar Entrega",
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
                // Cartão de Informações Principais
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Beneficiário: ${viewModel.nomeBeneficiario.value}", fontWeight = FontWeight.Bold, color = Color.Black)
                            Text("Pedido: ${viewModel.textoPedido.value}", fontSize = 14.sp, color = Color.Gray)

                            Spacer(Modifier.height(12.dp))
                            Text("Estado", fontSize = 12.sp, color = Color.Gray)

                            // Chips de Estado
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                EntregaStatus.values().forEach { status ->
                                    FilterChip(
                                        modifier = Modifier.weight(1f),
                                        selected = entrega?.status == status,
                                        onClick = { viewModel.atualizarStatus(status) },
                                        label = { Text(status.name, fontSize = 9.sp, maxLines = 1) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = IpcaButtonGreen,
                                            selectedLabelColor = WhiteColor
                                        )
                                    )
                                }
                            }

                            // Mostra o botão de data apenas para PRONTO ou ENTREGUE
                            if (entrega?.status == EntregaStatus.PRONTO || entrega?.status == EntregaStatus.ENTREGUE) {
                                OutlinedButton(
                                    onClick = { datePickerDialog.show() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = IpcaButtonGreen)
                                ) {
                                    Icon(Icons.Default.DateRange, null)
                                    Spacer(Modifier.width(8.dp))
                                    val dataTexto = entrega?.dataEntrega?.let {
                                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(it))
                                    } ?: "Selecionar Data de Entrega"
                                    Text(dataTexto)
                                }

                                if (entrega?.status == EntregaStatus.PRONTO) {
                                    val hoje = System.currentTimeMillis()
                                    val dataSelecionada = entrega?.dataEntrega ?: 0L

                                    val mensagem = if (dataSelecionada == 0L) {
                                        "Selecione uma data para agendar"
                                    } else if (dataSelecionada > hoje) {
                                        "Agendado: O estado mudará para ENTREGUE na data selecionada"
                                    } else {
                                        "Data confirmada: Estado alterado para ENTREGUE"
                                    }

                                    Text(
                                        text = mensagem,
                                        fontSize = 11.sp,
                                        color = if (dataSelecionada > hoje) IpcaButtonGreen else Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Seção de Produtos
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        Arrangement.SpaceBetween,
                        Alignment.CenterVertically
                    ) {
                        Text("Produtos", color = WhiteColor.copy(alpha = 0.7f))
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

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    // Dialogs (Inventário e Avisos)
    if (showProdutoDialog) {
        InventarioDialog(
            produtos = viewModel.produtosComStockAtualizado,
            onProdutoSelected = { p ->
                viewModel.adicionarProduto(p)
                showProdutoDialog = false
            },
            onDismiss = { showProdutoDialog = false }
        )
    }

    viewModel.avisoStock.value?.let { mensagem ->
        AlertDialog(
            onDismissRequest = { viewModel.limparAviso() },
            title = { Text("Aviso de Stock") },
            text = { Text(mensagem) },
            confirmButton = {
                TextButton(onClick = { viewModel.limparAviso() }) {
                    Text("OK")
                }
            }
        )
    }
}