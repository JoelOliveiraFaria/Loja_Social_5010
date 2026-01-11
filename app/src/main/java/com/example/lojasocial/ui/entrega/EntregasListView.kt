package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.R
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.models.Entrega
import com.example.lojasocial.models.EntregaStatus
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
        containerColor = BgGreen
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
        ) {
            // --- CABEÇALHO ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = WhiteColor
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .height(50.dp)
                        .align(Alignment.Center)
                        .clickable { navController.navigate("welcome") },
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Entregas",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            // --- FILTROS DE STATUS ATUALIZADOS ---
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
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = IpcaButtonGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = entrega.status.name,
                            color = IpcaButtonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = dataFmt,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
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