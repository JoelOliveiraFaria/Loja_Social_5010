package com.example.lojasocial.ui.campanhas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val DangerRed = Color(0xFFD84343)
private val TextWhite = Color.White

@Composable
fun DetalhesCampanhaView(
    navController: NavController,
    id: String,
    viewModel: CampanhasViewModel = hiltViewModel()
) {
    val detalhe by viewModel.detalheState.collectAsState()

    LaunchedEffect(id) { viewModel.carregarCampanha(id) }

    var showDeleteDialog by remember { mutableStateOf(false) }

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
                text = "Campanha",
                color = Color.White,
                fontSize = 34.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }

        if (detalhe.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        detalhe.error?.let {
            Text(
                text = it,
                color = Color(0xFFEF5350),
                modifier = Modifier.padding(16.dp)
            )
        }

        val c = detalhe.campanha

        if (c != null) {
            Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                LabelValue("Nome", c.nome)
                Spacer(Modifier.height(14.dp))
                LabelValue("Descrição", c.descricao)
                Spacer(Modifier.height(14.dp))
                LabelValue("Data de início", c.dataInicio)
                Spacer(Modifier.height(14.dp))
                LabelValue("Data de fim", c.dataFim)
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate("campanhas/${c.id}/edit") },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) { Text("Editar", color = Color.White) }

                Spacer(Modifier.width(12.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) { Text("Eliminar", color = Color.White) }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar campanha?") },
            text = { Text("Tens a certeza que queres eliminar esta campanha?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.eliminar(id)
                        navController.popBackStack() // volta para a lista
                    }
                ) { Text("Eliminar", color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Text(text = label, color = Color(0xFFB7D7CC), fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    Text(text = value.ifBlank { "—" }, color = Color.White, fontSize = 18.sp)
}
