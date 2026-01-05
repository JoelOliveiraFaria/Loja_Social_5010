package com.example.lojasocial.ui.produtos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
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
import com.example.lojasocial.models.LoteStock
import com.example.lojasocial.ui.components.TopBarWithMenu

@Composable
fun DetalhesProdutoView(
    produtoId: String,
    navController: NavController,
    viewModel: ProdutosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val produto = state.itens.find { it.id == produtoId }
    val lotes = state.lotesPorProduto[produtoId] ?: emptyList()
    var showAddDialog by remember { mutableStateOf(false) }

    // Converte AAAA-MM-DD em DD-MM-AAAA para o utilizador ver corretamente
    fun formatarParaExibicao(data: String?): String {
        if (data == null) return "Sem validade"
        val p = data.split("-")
        return if (p.size == 3) "${p[2]}-${p[1]}-${p[0]}" else data
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0B3B2E))) {
        TopBarWithMenu(navController)

        Column(modifier = Modifier.padding(16.dp)) {
            IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.offset(x = (-12).dp)) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text(produto?.nome ?: "", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text(produto?.descricao ?: "Sem descrição", color = Color.LightGray, fontSize = 16.sp)
            Text("Stock Válido: ${produto?.quantidadeTotal}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            items(lotes) { lote ->
                val expirado = lote.dataValidade != null && lote.dataValidade!! < viewModel.getHojeStr()
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = if (expirado) Color(0xFF741B1B) else Color(0xFF164D3E))
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Quantidade: ${lote.quantidade}", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Validade: ${formatarParaExibicao(lote.dataValidade)}", color = if (expirado) Color(0xFFFF5252) else Color.LightGray)
                        }
                        if (expirado) Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                    }
                }
            }
        }

        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.apagarLotesExpirados(produtoId) }, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84343))) {
                Text("Limpar Expirados", fontSize = 12.sp)
            }
            Button(onClick = { showAddDialog = true }, modifier = Modifier.weight(1f).height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F6F43))) {
                Text("Adicionar Stock")
            }
        }
    }

    if (showAddDialog) {
        var qtd by remember { mutableStateOf("1") }
        var valInput by remember { mutableStateOf("") }
        var erro by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Novo Lote de Stock") },
            text = {
                Column {
                    OutlinedTextField(
                        value = qtd,
                        onValueChange = { if (it.all { c -> c.isDigit() }) qtd = if (it.isEmpty()) "1" else it },
                        label = { Text("Quantidade") }
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = valInput,
                        onValueChange = { valInput = it.filter { c -> c.isDigit() || c == '-' } },
                        label = { Text("Validade (DD-MM-AAAA)") },
                        isError = erro != null,
                        supportingText = { erro?.let { Text(it, color = Color.Red) } }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (valInput.isEmpty()) {
                        viewModel.adicionarLote(produtoId, LoteStock(quantidade = qtd.toInt(), dataValidade = null))
                        showAddDialog = false
                    } else {
                        val regex = Regex("""^(\d{2})-(\d{2})-(\d{4})$""")
                        val match = regex.find(valInput)
                        if (match != null) {
                            val (d, m, a) = match.destructured
                            val dia = d.toInt(); val mes = m.toInt(); val ano = a.toInt()

                            // Validação rigorosa incluindo Fevereiro e Bissextos
                            val diasNoMes = when(mes) {
                                2 -> if ((ano % 4 == 0 && ano % 100 != 0) || (ano % 400 == 0)) 29 else 28
                                4,6,9,11 -> 30
                                in 1..12 -> 31
                                else -> 0
                            }

                            if (mes in 1..12 && dia in 1..diasNoMes) {
                                viewModel.adicionarLote(produtoId, LoteStock(quantidade = qtd.toInt(), dataValidade = String.format("%04d-%02d-%02d", ano, mes, dia)))
                                showAddDialog = false
                            } else { erro = "Data inválida para este mês" }
                        } else { erro = "Formato DD-MM-AAAA" }
                    }
                }) { Text("Adicionar") }
            }
        )
    }
}