package com.example.lojasocial.ui.campanhas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarVoltar

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val ErrorRed = Color(0xFFEF5350)
private val WhiteColor = Color.White
private val LabelColor = Color(0xFFB7D7CC)

@Composable
fun DetalhesCampanhaView(
    navController: NavController,
    id: String,
    viewModel: CampanhasViewModel = hiltViewModel()
) {
    val detalhe by viewModel.detalheState.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(id) {
        viewModel.carregarCampanha(id)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Campanha") },
            text = { Text("Tem a certeza que deseja eliminar esta campanha?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminar(id)
                        showDeleteDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(BgGreen)) {
        Column(modifier = Modifier.fillMaxSize()) {

            // --- NAVBAR (Seta + Logo) ---
            TopBarVoltar(navController = navController, title = null)

            Divider(color = Color(0xFF2C6B55))

            if (detalhe.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WhiteColor)
                }
            } else {
                val c = detalhe.campanha
                if (c != null) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // --- TÍTULO (Nome da Campanha) ---
                        Text(
                            text = c.nome,
                            style = MaterialTheme.typography.headlineMedium,
                            color = WhiteColor,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        LabelValue("Descrição", c.descricao)
                        Spacer(Modifier.height(24.dp))

                        Row(Modifier.fillMaxWidth()) {
                            Column(Modifier.weight(1f)) { LabelValue("Início", c.dataInicio) }
                            Column(Modifier.weight(1f)) { LabelValue("Fim", c.dataFim) }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(Modifier.height(40.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.weight(1f).height(54.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Eliminar")
                            }

                            Button(
                                onClick = { navController.navigate("campanhas/${id}/edit") },
                                modifier = Modifier.weight(1f).height(54.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = WhiteColor)
                                Spacer(Modifier.width(8.dp))
                                Text("Editar", color = WhiteColor)
                            }
                        }
                    }
                } else if (detalhe.error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Erro: ${detalhe.error}", color = ErrorRed)
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Text(text = label, color = LabelColor, fontSize = 14.sp)
    Spacer(Modifier.height(4.dp))
    Text(text = value.ifBlank { "—" }, color = WhiteColor, fontSize = 18.sp, fontWeight = FontWeight.Medium)
}