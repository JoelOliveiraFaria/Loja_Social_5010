package com.example.lojasocial.ui.campanhas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.Campanha
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)

@Composable
fun CampanhasView(
    navController: NavController,
    viewModel: CampanhasViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {

        TopBarWithMenu(navController)

        Divider(color = LineGreen)

        Text(
            text = "Campanhas",
            color = Color.White,
            fontSize = 34.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        state.error?.let {
            Text(
                text = it,
                color = Color(0xFFEF5350),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (!state.isLoading && state.campanhas.isEmpty()) {
                // vazio (mockup-friendly)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sem campanhas.",
                        color = Color.White
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(state.campanhas) { index, campanha ->
                        CampanhaRow(
                            campanha = campanha,
                            onClick = { navController.navigate("campanhas/${campanha.id}") }
                        )

                        if (index != state.campanhas.lastIndex) {
                            Divider(color = LineGreen)
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("campanhas/add") },
            colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
        ) {
            Text(
                text = "Adicionar Campanha",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun CampanhaRow(
    campanha: Campanha,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = campanha.nome.ifBlank { "—" },
            color = Color.White,
            fontSize = 16.sp
        )

        Text(
            text = "${campanha.dataInicio}  |  ${campanha.dataFim}",
            color = Color.White,
            fontSize = 14.sp
        )
    }
}
