package com.example.lojasocial.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
private val ButtonGreen = Color(0xFF1F6F43)
private val ErrorRed = Color(0xFFEF5350)
private val TextWhite = Color.White

@Composable
fun PedidoDetalhesView(
    navController: NavController,
    pedidoId: String,
    viewModel: PedidoDetalhesViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.value
    var showRecusarDialog by remember { mutableStateOf(false) }
    var showAvisoAtivaDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(pedidoId) {
        viewModel.carregarPedido(pedidoId)
    }

    // --- POP-UP DE AVISO (Entrega em curso) ---
    if (showAvisoAtivaDialog) {
        AlertDialog(
            onDismissRequest = { showAvisoAtivaDialog = false },
            title = { Text("Entrega em Curso", fontWeight = FontWeight.Bold) },
            text = { Text("Este beneficiário já possui uma entrega ativa. Deseja criar outra?") },
            confirmButton = {
                Button(
                    onClick = {
                        showAvisoAtivaDialog = false
                        viewModel.aceitarPedido(pedidoId) {
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen)
                ) { Text("Sim, Aceitar") }
            },
            dismissButton = {
                TextButton(onClick = { showAvisoAtivaDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.DarkGray
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // --- NAVBAR ---
            TopBarVoltar(navController = navController, title = null)

            Divider(color = Color(0xFF2C6B55))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TextWhite)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.error ?: "Erro desconhecido",
                        color = ErrorRed,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Detalhes do Pedido",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- INFO BENEFICIÁRIO ---
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextWhite)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Beneficiário", color = TextWhite.copy(alpha = 0.6f), fontSize = 12.sp)
                                Text(state.nomeBeneficiario ?: "N/A", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                // MOSTRAR O EMAIL AQUI
                                Text(state.emailBeneficiario ?: "", color = TextWhite.copy(alpha = 0.8f), fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- MENSAGEM DO PEDIDO ---
                    Text("Mensagem do pedido:", color = TextWhite.copy(alpha = 0.7f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = state.pedido?.textoPedido ?: "",
                            modifier = Modifier.padding(20.dp),
                            color = Color.Black,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(32.dp))

                    // --- BOTÕES ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showRecusarDialog = true },
                            modifier = Modifier.weight(1f).height(56.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Recusar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.verificarEaceitar(
                                    pedidoId = pedidoId,
                                    onAvisoEntregaAtiva = { showAvisoAtivaDialog = true },
                                    onSuccess = { navController.popBackStack() }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Aceitar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showRecusarDialog) {
        RecusarPedidoDialog(
            onDismiss = { showRecusarDialog = false },
            onConfirm = { motivo ->
                viewModel.recusarPedido(pedidoId, motivo) {
                    showRecusarDialog = false
                    navController.popBackStack()
                }
            }
        )
    }
}