package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.lojasocial.models.Entrega
import com.example.lojasocial.models.EntregaStatus
import com.example.lojasocial.ui.components.TopBarWithMenu
import com.example.lojasocial.ui.entregas.EntregasListViewModel

private val BgGreen = Color(0xFF0B3B2E)
private val CardGreen = Color(0xFF1F6F43)
private val LineGreen = Color(0xFF2C6B55)
private val TextWhite = Color.White

@Composable
fun EntregasListView(
    navController: NavController,
    viewModel: EntregasListViewModel = hiltViewModel()
) {
    val entregas by viewModel.entregas.collectAsState()
    val filtroAtivo by viewModel.filtroStatus.collectAsState()


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("entrega/novo") },
                containerColor = CardGreen,
                contentColor = TextWhite,
                shape = androidx.compose.foundation.shape.CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Criar Entrega")
            }
        },
        floatingActionButtonPosition = FabPosition.Start
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgGreen)
                .padding(paddingValues)
        ) {
            TopBarWithMenu(navController)
            Divider(color = LineGreen)

            // -------- HEADER --------
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
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Entregas",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            // -------- FILTROS --------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val opcoes = listOf(
                    "Em Andamento" to EntregaStatus.EM_ANDAMENTO,
                    "Pronto" to EntregaStatus.PRONTO,
                    "Entregue" to EntregaStatus.ENTREGUE
                )

                opcoes.forEach { (label, status) ->
                    FilterChip(
                        selected = filtroAtivo == status,
                        onClick = { viewModel.alterarFiltro(status) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            labelColor = TextWhite,
                            selectedLabelColor = BgGreen,
                            selectedContainerColor = TextWhite,
                            containerColor = CardGreen
                        )
                    )
                }
            }

            // -------- LISTA --------
            if (entregas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sem entregas para este estado",
                        color = TextWhite.copy(alpha = 0.7f),
                        fontSize = 18.sp
                    )
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(entregas) { entrega ->
                        val beneficiarioId = entrega.beneficiarioId ?: return@items
                        val nome = viewModel.nomesBeneficiarios[beneficiarioId]

                        EntregaCard(
                            entrega = entrega,
                            nomeBeneficiario = nome,
                            onLoadNome = {
                                viewModel.carregarNomeBeneficiario(beneficiarioId)
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
fun EntregaCard(
    entrega: Entrega,
    nomeBeneficiario: String?,
    onLoadNome: () -> Unit,
    onClick: () -> Unit
) {
    LaunchedEffect(entrega.beneficiarioId) {
        entrega.beneficiarioId?.let { onLoadNome() }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardGreen),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Beneficiário: ${nomeBeneficiario ?: "A carregar..."}",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Status: ${entrega.status}",
                color = TextWhite.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
        }
    }
}
