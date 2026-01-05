package com.example.lojasocial.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
            return@Column
        }

        state.error?.let {
            Text(
                text = it,
                color = ErrorRed,
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }

        Text(
            text = "Beneficiário",
            color = TextWhite,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        state.nomeBeneficiario?.let {
            Text(
                text = it,
                color = TextWhite,
                fontSize = 20.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Pedido",
            color = TextWhite,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Text(
            text = state.pedido?.textoPedido ?: "",
            color = TextWhite,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = { showRecusarDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                modifier = Modifier.weight(1f)
            ) {
                Text("Recusar")
            }

            Button(
                onClick = {
                    viewModel.aceitarPedido(pedidoId) {
                        navController.navigate(
                            "encomendas/add?pedidoId=$pedidoId"
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                modifier = Modifier.weight(1f)
            ) {
                Text("Aceitar")
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
