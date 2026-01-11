package com.example.lojasocial.ui.beneficiario

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

@Composable
fun DetalhesBeneficiarioView(
    id: String,
    navController: NavController,
    viewModel: BeneficiarioViewModel = hiltViewModel()
) {
    val state by viewModel.detalheState.collectAsState()
    val b = state.beneficiario

    LaunchedEffect(id) {
        viewModel.carregarBeneficiario(id)
    }

    Scaffold(
        containerColor = BgGreen,
        bottomBar = {
            Surface(color = BgGreen, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { navController.navigate("beneficiarios/editar/$id") },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp).navigationBarsPadding(),
                    colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Editar Perfil", fontWeight = FontWeight.Bold) }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = WhiteColor)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).statusBarsPadding()) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = WhiteColor)
                    }
                    Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.height(50.dp).align(Alignment.Center).clickable { navController.navigate("welcome") }, contentScale = ContentScale.Fit)
                }

                Column(modifier = Modifier.padding(24.dp)) {
                    Text(b?.nome ?: "Detalhes", style = MaterialTheme.typography.headlineMedium, color = WhiteColor, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(24.dp))
                    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            InfoItem("NIF", b?.nif)
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            InfoItem("E-mail", b?.email)
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            InfoItem("Telefone", b?.telefone)
                            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                            InfoItem("Estado", if (b?.estado == true) "Ativo" else "Inativo")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String?) {
    Column {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value ?: "Não definido", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}