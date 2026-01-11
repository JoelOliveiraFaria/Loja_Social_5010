package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.models.Entrega
import com.example.lojasocial.models.EntregaStatus
import com.example.lojasocial.ui.components.TopBarWithMenu
import com.example.lojasocial.ui.entregas.EntregasListViewModel
import java.text.SimpleDateFormat
import java.util.*

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun EntregasListView(
    navController: NavController,
    viewModel: EntregasListViewModel = hiltViewModel()
) {
    val entregas by viewModel.entregas.collectAsState()
    val filtroStatus by viewModel.filtroStatus.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("entrega/criar?pedidoId={pedidoId}") },
                containerColor = IpcaButtonGreen,
                contentColor = WhiteColor
            ) {
                Icon(Icons.Default.Add, contentDescription = "Criar Entrega")
            }
        },
        containerColor = BgGreen,
        // CORREÇÃO: Remove o padding automático da barra de estado para o verde ir até ao topo
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
            // Removido .statusBarsPadding() para igualar a WelcomeView
        ) {
            // --- NAVBAR ---
            TopBarWithMenu(navController = navController)

            Divider(color = Color(0xFF2C6B55))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Entregas",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            // --- FILTROS ---
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilterChip("Em Andamento", filtroStatus == EntregaStatus.EM_ANDAMENTO) {
                    viewModel.alterarFiltro(EntregaStatus.EM_ANDAMENTO)
                }

                StatusFilterChip("Prontas", filtroStatus == EntregaStatus.PRONTO) {
                    viewModel.alterarFiltro(EntregaStatus.PRONTO)
                }

                StatusFilterChip("Entregues", filtroStatus == EntregaStatus.ENTREGUE) {
                    viewModel.alterarFiltro(EntregaStatus.ENTREGUE)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (entregas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma entrega encontrada", color = WhiteColor.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(entregas) { entrega ->
                        val beneficiario = viewModel.dadosBeneficiarios[entrega.beneficiarioId ?: ""]

                        EntregaItemCard(
                            entrega = entrega,
                            beneficiario = beneficiario,
                            onLoadDados = {
                                entrega.beneficiarioId?.let { viewModel.carregarDadosBeneficiario(it) }
                            },
                            onClick = {
                                navController.navigate("entrega/detalhes/${entrega.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EntregaItemCard(
    entrega: Entrega,
    beneficiario: Beneficiario?,
    onLoadDados: () -> Unit,
    onClick: () -> Unit
) {
    LaunchedEffect(entrega.beneficiarioId) {
        onLoadDados()
    }

    val dataFmt = remember(entrega.dataCriacao) {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(entrega.dataCriacao))
    }

    val (statusBg, statusContent, statusIcon, statusLabel) = when (entrega.status) {
        EntregaStatus.EM_ANDAMENTO -> Tuple4(
            Color(0xFFE8F5E9), Color(0xFF1B5E20), Icons.Default.LocalShipping, "Em Andamento"
        )
        EntregaStatus.PRONTO -> Tuple4(
            Color(0xFFE1F5FE), Color(0xFF0277BD), Icons.Default.Inventory, "Pronto"
        )
        EntregaStatus.ENTREGUE -> Tuple4(
            Color(0xFFEEEEEE), Color(0xFF424242), Icons.Default.DoneAll, "Entregue"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = statusBg, shape = RoundedCornerShape(8.dp)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = statusIcon, contentDescription = null, tint = statusContent, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = statusLabel, color = statusContent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(text = dataFmt, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = beneficiario?.email ?: "Carregando email...",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1F1F1F)
            )

            Text(
                text = beneficiario?.nome ?: "Carregando nome...",
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ver detalhes",
                    fontSize = 12.sp,
                    color = IpcaButtonGreen,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = IpcaButtonGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun StatusFilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        color = if (isSelected) IpcaButtonGreen else Color.Transparent,
        shape = RoundedCornerShape(20.dp),
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, WhiteColor.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            color = WhiteColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)