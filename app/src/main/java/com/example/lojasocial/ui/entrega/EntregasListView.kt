package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.Entrega
import com.example.lojasocial.ui.components.TopBarWithMenu
import com.example.lojasocial.ui.entregas.EntregasListViewModel

private val BgGreen = Color(0xFF0B3B2E)
private val CardGreen = Color(0xFF1F6F43)
private val TextWhite = Color.White

@Composable
fun EntregasListView(
    navController: NavController,
    viewModel: EntregasListViewModel = hiltViewModel()
) {
    val entregas by viewModel.entregas.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {

        TopBarWithMenu(navController)

        Text(
            text = "Entregas em Andamento",
            color = TextWhite,
            fontSize = 28.sp,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
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
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = CardGreen
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = nomeBeneficiario ?: "A carregar...",
                color = TextWhite,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Estado: ${entrega.status}",
                color = TextWhite.copy(alpha = 0.8f)
            )
        }
    }
}
