package com.example.lojasocial.ui.entrega

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.EntregaStatus
import com.example.lojasocial.models.ItemEntrega
import com.example.lojasocial.models.Produto
import com.example.lojasocial.ui.components.TopBarWithMenu
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// -------- CORES --------
private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val ButtonGreenLight = Color(0xFF2C6B55)
private val CardGreen = Color(0xFF1F6F43)
private val TextWhite = Color.White

@Composable
fun EntregaDetalhesView(
    navController: NavController,
    entregaId: String,
    viewModel: EntregaDetalhesViewModel = hiltViewModel()
) {
    var mostrarDialog by remember { mutableStateOf(false) }
    var dropdownEstado by remember { mutableStateOf(false) }
    var mostrarDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.carregarEntrega(entregaId)
    }

    val entrega by viewModel.entrega

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {

        // -------- TOPBAR --------
        TopBarWithMenu(navController)
        Divider(color = LineGreen)

        // -------- HEADER --------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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

            Spacer(Modifier.width(12.dp))

            Text(
                text = "Detalhes da Entrega",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // -------- CONTEÚDO --------
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {

            Text(
                text = "Beneficiário: ${viewModel.nomeBeneficiario.value}",
                color = TextWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = "Pedido: ${viewModel.textoPedido.value}",
                color = TextWhite
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entrega?.itens ?: emptyList()) { item ->
                    CardItemEntrega(
                        item = item,
                        bloqueado = viewModel.estadoSelecionado.value == EntregaStatus.ENTREGUE,
                        onDiminuir = { viewModel.diminuirQuantidade(item.produtoId) },
                        onAumentar = { viewModel.aumentarQuantidade(item.produtoId) },
                        onRemover = { viewModel.removerProduto(item.produtoId) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // -------- ESTADO + DATA (FIXADO) --------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ESTADO
                Box(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = { dropdownEstado = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen)
                    ) {
                        Text(
                            viewModel.estadoSelecionado.value.name,
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    DropdownMenu(
                        expanded = dropdownEstado,
                        onDismissRequest = { dropdownEstado = false },
                        modifier = Modifier.background(BgGreen)
                    ) {
                        listOf(
                            EntregaStatus.EM_ANDAMENTO,
                            EntregaStatus.PRONTO,
                            EntregaStatus.ENTREGUE
                        ).forEach { estado ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        estado.name,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    dropdownEstado = false
                                    viewModel.alterarEstado(estado)
                                }
                            )
                            Divider(color = LineGreen)
                        }
                    }
                }

                // DATA
                Button(
                    onClick = { mostrarDatePicker = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen)
                ) {
                    Text(
                        viewModel.dataEntrega.value?.let {
                            SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            ).format(Date(it))
                        } ?: "Data",
                        color = TextWhite
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // -------- ADICIONAR PRODUTO --------
            Button(
                onClick = { mostrarDialog = true },
                enabled = viewModel.estadoSelecionado.value != EntregaStatus.ENTREGUE,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonGreen,
                    disabledContainerColor = ButtonGreenLight.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    "Adicionar Produto",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            // -------- GUARDAR --------
            Button(
                onClick = { viewModel.guardar { navController.popBackStack() } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CardGreen)
            ) {
                Text(
                    "Guardar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // -------- DATE PICKER --------
    if (mostrarDatePicker) {
        val context = LocalContext.current
        val cal = Calendar.getInstance()

        val picker = DatePickerDialog(
            context,
            { _, y, m, d ->
                cal.set(y, m, d)
                viewModel.alterarData(cal.timeInMillis)
                mostrarDatePicker = false
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )

        picker.setOnDismissListener { mostrarDatePicker = false }
        picker.show()
    }

    // -------- DIALOG PARA ADICIONAR PRODUTO --------
    if (mostrarDialog) {
        val produtos by viewModel.produtosDisponiveis.collectAsState()

        AlertDialog(
            onDismissRequest = { mostrarDialog = false },
            containerColor = BgGreen,
            title = { Text("Selecionar Produto", color = TextWhite) },
            text = {
                Box(modifier = Modifier.heightIn(max = 400.dp)) {
                    LazyColumn {
                        items(produtos) { produto ->
                            ListItem(
                                headlineContent = { Text(produto.nome.toString(), color = TextWhite) },
                                supportingContent = { Text("Stock: ${produto.quantidadeTotal}", color = TextWhite.copy(0.7f)) },
                                modifier = androidx.compose.ui.Modifier.clickable {
                                    viewModel.adicionarProduto(produto, 1)
                                    mostrarDialog = false
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                            Divider(color = LineGreen)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarDialog = false }) {
                    Text("Fechar", color = TextWhite)
                }
            }
        )
    }
}

// -------- CARD ITEM --------
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
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onDiminuir,
                    enabled = !bloqueado,
                    modifier = Modifier.size(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonGreen,
                        disabledContainerColor = ButtonGreenLight.copy(alpha = 0.5f)
                    )
                ) { Text("-", color = TextWhite, fontSize = 20.sp) }

                Text(
                    item.lotesConsumidos.sumOf { it.quantidade }.toString(),
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = onAumentar,
                    enabled = !bloqueado,
                    modifier = Modifier.size(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonGreen,
                        disabledContainerColor = ButtonGreenLight.copy(alpha = 0.5f)
                    )
                ) { Text("+", color = TextWhite, fontSize = 20.sp) }

                Spacer(Modifier.weight(1f))

                IconButton(onClick = onRemover, enabled = !bloqueado) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remover",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
