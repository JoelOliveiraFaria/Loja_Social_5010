package com.example.lojasocial.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.Pedido
import com.example.lojasocial.models.PedidoStatus
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun PedidosListView(
    navController: NavController,
    viewModel: PedidosListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // 1. NAVBAR
            TopBarWithMenu(navController = navController)
            Divider(color = Color(0xFF2C6B55))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Gestão de Pedidos",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            // 2. BOTÕES DE FILTRO (TABS)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Botão Novos
                FilterTabButton(
                    text = "Novos",
                    isSelected = state.currentTab == PedidosTab.NOVOS,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.mudarTab(PedidosTab.NOVOS) }
                )

                // Botão Histórico
                FilterTabButton(
                    text = "Histórico",
                    isSelected = state.currentTab == PedidosTab.HISTORICO,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.mudarTab(PedidosTab.HISTORICO) }
                )
            }

            // 3. LISTA
            if (state.isLoading && state.pedidos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WhiteColor)
                }
            } else if (state.pedidos.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if(state.currentTab == PedidosTab.NOVOS) "Não há novos pedidos." else "Histórico vazio.",
                        color = WhiteColor.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.pedidos) { pedido ->
                        val nomeBeneficiario = state.beneficiarios[pedido.beneficiarioId] ?: "A carregar..."

                        PedidoItemCard(
                            pedido = pedido,
                            beneficiarioNome = nomeBeneficiario,
                            onClick = {
                                navController.navigate("pedidos/${pedido.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterTabButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) IpcaButtonGreen else Color(0xFF164A3D),
            contentColor = WhiteColor
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(text = text, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun PedidoItemCard(
    pedido: Pedido,
    beneficiarioNome: String,
    onClick: () -> Unit
) {
    // Cores e Ícones
    val (statusColor, statusBg, statusText, statusIcon) = when (pedido.status) {
        PedidoStatus.NOVO -> Tuple4(Color(0xFFE65100), Color(0xFFFFF3E0), "NOVO", Icons.Default.AccessTime)
        PedidoStatus.EM_ANDAMENTO -> Tuple4(Color(0xFF1B5E20), Color(0xFFE8F5E9), "ACEITE", Icons.Default.LocalShipping)
        PedidoStatus.PRONTO -> Tuple4(Color(0xFF0277BD), Color(0xFFE1F5FE), "PRONTO", Icons.Default.Inventory)
        PedidoStatus.ENTREGUE -> Tuple4(Color(0xFF424242), Color(0xFFEEEEEE), "ENTREGUE", Icons.Default.DoneAll)
        PedidoStatus.RECUSADO -> Tuple4(Color(0xFFC62828), Color(0xFFFFEBEE), "RECUSADO", Icons.Default.Cancel)
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Chip de Estado
                Surface(color = statusBg, shape = RoundedCornerShape(8.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = statusText, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = beneficiarioNome,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = pedido.textoPedido,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F1F1F)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Ver detalhes", fontSize = 12.sp, color = IpcaButtonGreen, fontWeight = FontWeight.Bold)
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = IpcaButtonGreen, modifier = Modifier.size(16.dp))
            }
        }
    }
}

// Classe auxiliar para devolver 4 valores
private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)