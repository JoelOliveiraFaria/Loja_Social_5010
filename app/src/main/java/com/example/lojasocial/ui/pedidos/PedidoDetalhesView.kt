package com.example.lojasocial.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
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

    LaunchedEffect(pedidoId) {
        viewModel.carregarPedido(pedidoId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarWithMenu(navController)
        Divider(color = LineGreen)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 12.dp),
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
            Text(
                text = "Detalhes do Pedido",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LineGreen)
            }
            return@Column
        }

        state.error?.let {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it,
                    color = ErrorRed,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(32.dp)
                )
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Beneficiário",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            state.nomeBeneficiario?.let {
                Text(
                    text = it,
                    color = TextWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )
            }

            Text(
                text = "Pedido",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = state.pedido?.textoPedido ?: "",
                color = TextWhite,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { showRecusarDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("Recusar", fontSize = 18.sp)
                }

                Button(
                    onClick = {
                        viewModel.aceitarPedido(pedidoId) {
                            navController.popBackStack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text("Aceitar Pedido", fontSize = 18.sp)
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
