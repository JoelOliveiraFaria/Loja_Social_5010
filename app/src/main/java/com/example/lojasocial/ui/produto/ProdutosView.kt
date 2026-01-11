package com.example.lojasocial.ui.produtos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.lojasocial.ui.components.TopBarWithMenu

private val LineGreen = Color(0xFF2C6B55)

@Composable
fun ProdutosView(navController: NavController, viewModel: ProdutosViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0B3B2E))) {
        TopBarWithMenu(navController)
        Divider(color = LineGreen)
        Text("Inventário", color = Color.White, fontSize = 32.sp, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            items(state.itens) { produto ->
                val temExpirados = (state.lotesPorProduto[produto.id] ?: emptyList()).any {
                    it.dataValidade != null && it.dataValidade!! < viewModel.getHojeStr()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("inventario/${produto.id}") }
                        .background(if (temExpirados) Color(0xFF441111) else Color.Transparent)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(produto.nome ?: "", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Stock Válido: ${produto.quantidadeTotal}", color = Color(0xFF81C784))
                        if (temExpirados) Text("⚠ Contém lotes expirados!", color = Color.Red, fontSize = 12.sp)
                    }
                }
                HorizontalDivider(color = Color(0xFF2C6B55))
            }
        }

        Button(
            onClick = { navController.navigate("inventario/add") },
            modifier = Modifier.fillMaxWidth().padding(16.dp).height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F6F43))
        ) {
            Text("Adicionar Produto Geral", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}