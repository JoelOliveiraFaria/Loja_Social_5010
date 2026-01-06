package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lojasocial.ui.entrega.EntregaViewModel

@Composable
fun EntregaView(
    beneficiarioId: String,
    pedidoId: String,
    viewModel: EntregaViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.iniciarEntrega(
            beneficiarioId = beneficiarioId,
            pedidoId = pedidoId
        )
    }

    val entrega = viewModel.uiState.value

    if (entrega == null) {
        CircularProgressIndicator()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Criar Entrega",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Beneficiário ID: ${entrega.beneficiarioId}")
        Text("Pedido ID: ${entrega.pedidoId}")

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Produtos",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        entrega.produtos.forEach {
            Text("• Produto: ${it.produtoId} | Quantidade: ${it.quantidade}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Exemplo simples (depois ligas a um seletor real)
                viewModel.adicionarProduto(
                    produtoId = "produto_teste",
                    quantidade = 1
                )
            }
        ) {
            Text("Adicionar produto")
        }
    }
}
