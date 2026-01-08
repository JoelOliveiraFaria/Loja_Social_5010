package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.lojasocial.models.Entrega
import com.example.lojasocial.ui.components.TopBarWithMenu
import com.example.lojasocial.ui.entrega.EntregasTerminadasListViewModel

private val BgGreen = Color(0xFF0B3B2E)
private val CardGreen = Color(0xFF1F6F43)
private val LineGreen = Color(0xFF2C6B55)
private val TextWhite = Color.White

@Composable
fun EntregasTerminadasListView(
    navController: NavController,
    viewModel: EntregasTerminadasListViewModel = hiltViewModel()
) {
    val entregas by viewModel.entregas.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarWithMenu(navController)
        Divider(color = LineGreen)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 16.dp, bottom = 24.dp),
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
                text = "Entregas Terminadas",
                color = TextWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (entregas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sem entregas terminadas",
                    color = TextWhite.copy(alpha = 0.7f),
                    fontSize = 18.sp
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entregas) { entrega ->
                    val beneficiarioId = entrega.beneficiarioId ?: return@items
                    val nome = viewModel.nomesBeneficiarios[beneficiarioId]

                    EntregaCardTerminada(
                        entrega = entrega,
                        nomeBeneficiario = nome,
                        onLoadNome = { viewModel.carregarNomeBeneficiario(beneficiarioId) },
                        onClick = { navController.navigate("entrega/detalhes/${entrega.id}") }
                    )
                }
            }
        }
    }
}

@Composable
private fun EntregaCardTerminada(
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
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Beneficiário: ${nomeBeneficiario ?: "A carregar..."}",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            // ID Entrega
            Text(
                text = "ID: ${entrega.id}",
                color = TextWhite.copy(alpha = 0.8f),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Status
            Text(
                text = "Status: ${entrega.status}",
                color = TextWhite.copy(alpha = 0.8f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}