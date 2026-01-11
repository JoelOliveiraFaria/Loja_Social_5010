package com.example.lojasocial.ui.campanhas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
private val LabelColor = Color(0xFFB7D7CC)

@Composable
fun DetalhesCampanhaView(
    navController: NavController,
    id: String,
    viewModel: CampanhasViewModel = hiltViewModel()
) {
    val detalhe by viewModel.detalheState.collectAsState()

    LaunchedEffect(id) {
        viewModel.carregarCampanha(id)
    }

    Box(modifier = Modifier.fillMaxSize().background(BgGreen)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

            // --- CABEÇALHO ---
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = WhiteColor)
                }
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.height(50.dp).align(Alignment.Center).clickable { navController.navigate("welcome") },
                    contentScale = ContentScale.Fit
                )
            }

            val c = detalhe.campanha
            if (c != null) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Detalhes da Campanha", style = MaterialTheme.typography.headlineMedium, color = WhiteColor, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(32.dp))

                    LabelValue("Nome da Campanha", c.nome)
                    Spacer(Modifier.height(24.dp))

                    LabelValue("Descrição", c.descricao)
                    Spacer(Modifier.height(24.dp))

                    Row(Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) { LabelValue("Início", c.dataInicio) }
                        Column(Modifier.weight(1f)) { LabelValue("Fim", c.dataFim) }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = { navController.navigate("campanhas/${id}/edit") },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Editar Campanha", fontWeight = FontWeight.Bold, color = WhiteColor)
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Text(text = label, color = LabelColor, fontSize = 14.sp)
    Text(text = value.ifBlank { "—" }, color = WhiteColor, fontSize = 20.sp, fontWeight = FontWeight.Medium)
}