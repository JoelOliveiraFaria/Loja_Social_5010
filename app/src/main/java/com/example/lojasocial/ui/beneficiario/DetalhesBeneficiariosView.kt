package com.example.lojasocial.ui.beneficiario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.ui.components.TopBarVoltar
import com.example.lojasocial.ui.components.TopBarWithMenu

// Cores
private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val DangerRed = Color(0xFFD84343)
private val TextWhite = Color.White
private val LabelColor = Color(0xFFB7D7CC)
private val ActiveGreen = Color(0xFF4CAF50) // Verde para Ativo

// ---------------------------------------------------------
// 1. ENTRY POINT (Lógica + ViewModel)
// ---------------------------------------------------------
@Composable
fun DetalhesBeneficiarioView(
    navController: NavController,
    id: String,
    viewModel: BeneficiarioViewModel = hiltViewModel()
) {
    val detalhe by viewModel.detalheState.collectAsState()

    // Carregar dados ao entrar
    LaunchedEffect(id) {
        viewModel.carregarBeneficiario(id)
    }

    DetalhesBeneficiarioContent(
        navController = navController,
        state = detalhe,
        onEditClick = { benId -> navController.navigate("beneficiarios/$benId/edit") },
        onBack = { navController.popBackStack() }
    )
}

// ---------------------------------------------------------
// 2. CONTEÚDO VISUAL (Stateless para Preview)
// ---------------------------------------------------------
@Composable
fun DetalhesBeneficiarioContent(
    navController: NavController,
    state: BeneficiarioDetalheState,
    onEditClick: (String) -> Unit,
    onBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    val b = state.beneficiario

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarVoltar(navController, "Detalhes do Beneficiário")
        Divider(color = LineGreen)

        Text(
            text = "Beneficiário",
            color = TextWhite,
            fontSize = 34.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // Loading
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Erro
        state.error?.let {
            Text(
                text = it,
                color = Color(0xFFEF5350),
                modifier = Modifier.padding(16.dp)
            )
        }

        // Dados
        if (b != null) {
            Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                LabelValue("Nome", b.nome)
                Spacer(Modifier.height(14.dp))
                LabelValue("NIF", b.nif)
                Spacer(Modifier.height(14.dp))
                LabelValue("Email", b.email)
                Spacer(Modifier.height(14.dp))
                LabelValue("Telefone", b.telefone)
                Spacer(Modifier.height(14.dp))

                // --- Campo Estado (Ativo/Inativo) ---
                Text(text = "Estado", color = LabelColor, fontSize = 14.sp)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Ponto indicador de cor
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = if (b.estado == true) ActiveGreen else DangerRed,
                                shape = RoundedCornerShape(50)
                            )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (b.estado == true) "Ativo" else "Inativo",
                        color = TextWhite,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Botões de Ação
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botão Editar
                Button(
                    onClick = { b.id?.let { onEditClick(it) } },
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) { Text("Editar", color = TextWhite) }

                Spacer(Modifier.width(12.dp))

                /*
                // Botão Eliminar
                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                ) { Text("Eliminar", color = TextWhite) } */
            }
        }
    }
/*
    // Dialog de Confirmação
    if (showDeleteDialog && b != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar beneficiário?") },
            text = { Text("Tens a certeza que queres eliminar este registo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        b.id?.let { onDeleteClick(it) }
                    }
                ) { Text("Eliminar", color = DangerRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
    */
}

// ---------------------------------------------------------
// 3. COMPONENTES AUXILIARES
// ---------------------------------------------------------
@Composable
private fun LabelValue(label: String, value: String?) {
    Text(text = label, color = LabelColor, fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    Text(text = value?.ifBlank { "—" } ?: "—", color = TextWhite, fontSize = 18.sp)
}

// ---------------------------------------------------------
// 4. PREVIEW
// ---------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun DetalhesBeneficiarioPreview() {
    val bMock = Beneficiario(
        id = "123",
        nome = "Maria Fernanda",
        nif = "123456789",
        email = "maria@exemplo.com",
        telefone = "910000000",
        estado = true // Simular estado Ativo
    )

    val stateMock = BeneficiarioDetalheState(
        isLoading = false,
        error = null,
        beneficiario = bMock
    )

    DetalhesBeneficiarioContent(
        navController = rememberNavController(),
        state = stateMock,
        onEditClick = {},
        onBack = {}
    )
}