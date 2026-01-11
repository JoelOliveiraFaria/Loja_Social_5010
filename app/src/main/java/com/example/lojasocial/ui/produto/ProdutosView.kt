package com.example.lojasocial.ui.produtos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.R

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun ProdutosView(navController: NavController, viewModel: ProdutosViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val hoje = viewModel.getHojeStr()

    Box(modifier = Modifier.fillMaxSize().background(BgGreen)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

            // --- CABEÇALHO (Logo centralizado + Botão Voltar) ---
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = WhiteColor)
                }

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.height(50.dp).align(Alignment.Center).clickable { navController.navigate("welcome") },
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Inventário",
                    style = MaterialTheme.typography.headlineMedium,
                    color = WhiteColor,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { navController.navigate("inventario/add") }) {
                    Icon(Icons.Default.Add, "Novo", tint = WhiteColor, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.itens) { produto ->
                    val temExpirados = (state.lotesPorProduto[produto.id] ?: emptyList()).any {
                        it.dataValidade != null && it.dataValidade!! < hoje
                    }

                    // Card Branco conforme o modelo
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { navController.navigate("inventario/${produto.id}") },
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