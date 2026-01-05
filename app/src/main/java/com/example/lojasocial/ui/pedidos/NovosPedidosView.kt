package com.example.lojasocial.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.Pedido
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val TextWhite = Color.White
private val TextWhiteSecondary = Color.White.copy(alpha = 0.7f)

@Composable
fun NovosPedidosView(
    navController: NavController,
    viewModel: NovosPedidosViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {

        TopBarWithMenu(navController)
        Divider(color = LineGreen)

        Text(
            text = "Novos Pedidos",
            color = TextWhite,
            fontSize = 34.sp,
            modifier = Modifier.padding(16.dp)
        )

        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (!state.isLoading && state.pedidos.isEmpty()) {
                Text(
                    text = "Sem pedidos novos.",
                    color = TextWhite,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn {
                    itemsIndexed(state.pedidos) { index, pedido ->
                        PedidoRow(
                            pedido = pedido,
                            onClick = {
                                navController.navigate("pedidos/${pedido.id}")
                            }
                        )

                        if (index != state.pedidos.lastIndex) {
                            Divider(color = LineGreen)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PedidoRow(
    pedido: Pedido,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = "Beneficiário: ${pedido.beneficiarioId}",
            color = TextWhite,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = pedido.textoPedido,
            color = TextWhiteSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
