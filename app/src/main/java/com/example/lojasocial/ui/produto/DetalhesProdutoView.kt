package com.example.lojasocial.ui.produtos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.LoteStock
import com.example.lojasocial.ui.campanhas.DateMaskVisualTransformation
import com.example.lojasocial.ui.campanhas.onlyDateDigits
import com.example.lojasocial.ui.components.TopBarVoltar

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

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

    Scaffold(
        containerColor = BgGreen,
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            Surface(color = BgGreen, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp).navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Adicionar Stock", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.apagarLotesExpirados(produtoId) },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CleaningServices, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Limpar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- NAVBAR VOLTAR ---
            TopBarVoltar(navController = navController, title = "Detalhes")

            Divider(color = Color(0xFF2C6B55))

            Text(
                text = produto?.nome ?: "Produto",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(lotes) { lote ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Quantidade: ${lote.quantidade}", fontWeight = FontWeight.Bold, color = Color.Black)
                                Text("Validade: ${lote.dataValidade ?: "Sem validade"}", color = Color.Gray, fontSize = 14.sp)
                            }
                            IconButton(onClick = { viewModel.eliminarLote(produtoId, lote.id!!) }) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red)
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIÁLOGO ---
    if (showAddDialog) {
        var qtdInput by remember { mutableStateOf("") }
        var dateDigits by remember { mutableStateOf("") } // Apenas números (DDMMYYYY)
        var erroMsg by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Novo Lote") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = qtdInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) qtdInput = it },
                        label = { Text("Quantidade") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = dateDigits,
                        onValueChange = { dateDigits = onlyDateDigits(it) },
                        label = { Text("Validade (DD/MM/AAAA)") },
                        placeholder = { Text("DD/MM/AAAA") },
                        visualTransformation = DateMaskVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (erroMsg != null) {
                        Text(erroMsg!!, color = Color.Red, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val qtd = qtdInput.toIntOrNull()
                    if (qtd == null || qtd <= 0) {
                        erroMsg = "Quantidade inválida"
                        return@TextButton
                    }

                    val finalDate = if (dateDigits.length == 8) {
                        val dia = dateDigits.substring(0, 2)
                        val mes = dateDigits.substring(2, 4)
                        val ano = dateDigits.substring(4, 8)
                        "$ano-$mes-$dia"
                    } else if (dateDigits.isEmpty()) {
                        null
                    } else {
                        erroMsg = "Data incompleta"
                        return@TextButton
                    }

                    viewModel.adicionarLote(produtoId, LoteStock(quantidade = qtd, dataValidade = finalDate))
                    showAddDialog = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
            }
        )
    }
}