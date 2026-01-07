package com.example.lojasocial.ui.entrega

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.lojasocial.ui.entrega.EntregasTerminadasListViewModel

private val BgGreen = Color(0xFF0B3B2E)
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

        Text(
            text = "Entregas Terminadas",
            color = TextWhite,
            fontSize = 28.sp,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn {
            items(entregas) { entrega ->
                EntregaCard(entrega)
            }
        }
    }
}

@Composable
private fun EntregaCard(entrega: Entrega) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Entrega ID: ${entrega.id}")
            Text("Beneficiário: ${entrega.beneficiarioId}")
            Text("Status: ${entrega.status}")
        }
    }
}
