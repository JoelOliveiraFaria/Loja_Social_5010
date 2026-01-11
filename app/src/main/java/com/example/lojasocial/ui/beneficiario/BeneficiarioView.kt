package com.example.lojasocial.ui.beneficiario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
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
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeneficiarioView(navController: NavController, viewModel: BeneficiarioViewModel = hiltViewModel()) {
    val state = viewModel.uiState.value

    var filtroEstado by remember { mutableStateOf<Boolean?>(null) }

    val beneficiariosFiltrados = remember(state.beneficiarios, filtroEstado) {
        if (filtroEstado == null) {
            state.beneficiarios
        } else {
            state.beneficiarios.filter { it.estado == filtroEstado }
        }
    }

    Scaffold(
        containerColor = BgGreen,
        contentWindowInsets = WindowInsets(0.dp),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("beneficiarios/criar") },
                containerColor = IpcaButtonGreen,
                contentColor = WhiteColor,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- NAVBAR ---
            TopBarWithMenu(navController = navController)

            Divider(color = Color(0xFF2C6B55))

            Spacer(modifier = Modifier.height(16.dp))

            // Título
            Text(
                text = "Beneficiários",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            // --- FILTROS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filtroEstado == null,
                    onClick = { filtroEstado = null },
                    label = { Text("Todos") },
                    colors = filterChipColors()
                )
                FilterChip(
                    selected = filtroEstado == true,
                    onClick = { filtroEstado = true },
                    label = { Text("Ativos") },
                    leadingIcon = if (filtroEstado == true) { { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) } } else null,
                    colors = filterChipColors()
                )
                FilterChip(
                    selected = filtroEstado == false,
                    onClick = { filtroEstado = false },
                    label = { Text("Inativos") },
                    leadingIcon = if (filtroEstado == false) { { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) } } else null,
                    colors = filterChipColors()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- LISTA ---
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = WhiteColor)
                }
            } else if (beneficiariosFiltrados.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum beneficiário encontrado.", color = WhiteColor.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(beneficiariosFiltrados) { beneficiario ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("beneficiarios/detalhes/${beneficiario.id}") },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Surface(shape = RoundedCornerShape(8.dp), color = BgGreen.copy(alpha = 0.1f), modifier = Modifier.size(40.dp)) {
                                    Icon(Icons.Default.Person, null, tint = BgGreen, modifier = Modifier.padding(8.dp))
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(beneficiario.nome ?: "", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    // MUDANÇA AQUI: Mostrar Email em vez de telefone
                                    Text(beneficiario.email ?: "Sem email", color = Color.Gray, fontSize = 14.sp)
                                }
                                if (beneficiario.estado == false) {
                                    Text("Inativo", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("Ativo", color = IpcaButtonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun filterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = Color.Transparent,
    labelColor = WhiteColor.copy(alpha = 0.7f),
    selectedContainerColor = IpcaButtonGreen,
    selectedLabelColor = WhiteColor,
    selectedLeadingIconColor = WhiteColor
)