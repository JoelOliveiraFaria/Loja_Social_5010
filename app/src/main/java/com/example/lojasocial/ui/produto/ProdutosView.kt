package com.example.lojasocial.ui.produtos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun ProdutosView(navController: NavController, viewModel: ProdutosViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val hoje = viewModel.getHojeStr()

    Scaffold(
        containerColor = BgGreen,
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("inventario/add") },
                containerColor = IpcaButtonGreen,
                contentColor = WhiteColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- NAVBAR COM MENU ---
            TopBarWithMenu(navController = navController)

            Divider(color = Color(0xFF2C6B55))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Inventário",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.itens) { produto ->
                    val temExpirados = (state.lotesPorProduto[produto.id] ?: emptyList()).any {
                        it.dataValidade != null && it.dataValidade!! < hoje
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("inventario/${produto.id}") },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = produto.nome ?: "",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                if (temExpirados) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Warning, null, tint = Color.Red, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Itens expirados!", color = Color.Red, fontSize = 12.sp)
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${produto.quantidadeTotal}",
                                    color = IpcaButtonGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text("unidades", fontSize = 11.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}