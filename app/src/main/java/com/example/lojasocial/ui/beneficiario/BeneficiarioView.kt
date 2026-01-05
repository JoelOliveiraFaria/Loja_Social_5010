package com.example.lojasocial.ui.beneficiario

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.ui.components.TopBarWithMenu

// Cores do tema (iguais às CampanhasView)
private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val ErrorRed = Color(0xFFEF5350)
private val TextWhite = Color.White
private val TextWhiteSecondary = Color.White.copy(alpha = 0.7f)

// ---------------------------------------------------------
// 1. ENTRY POINT (Lógica + ViewModel)
// ---------------------------------------------------------
@Composable
fun BeneficiarioView(
    navController: NavController,
    viewModel: BeneficiarioViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.value

    BeneficiarioContent(
        navController = navController,
        state = state,
        onAddClick = { navController.navigate("beneficiarios/add") },
        onItemClick = { id -> navController.navigate("beneficiarios/$id") }
    )
}

// ---------------------------------------------------------
// 2. CONTEÚDO VISUAL (Stateless para suportar Preview)
// ---------------------------------------------------------
@Composable
fun BeneficiarioContent(
    navController: NavController,
    state: BeneficiarioState,
    onAddClick: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {

        // TopBar + Menu
        TopBarWithMenu(navController)

        Divider(color = LineGreen)

        // Título centrado
        Text(
            text = "Beneficiários",
            color = TextWhite,
            fontSize = 34.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // Loading
        if (state.isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        // Erro
        state.error?.let {
            Text(
                text = it,
                color = ErrorRed,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // Lista
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (!state.isLoading && state.beneficiarios.isEmpty()) {
                // Estado vazio
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sem beneficiários registados.",
                        color = TextWhite
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    itemsIndexed(state.beneficiarios) { index, item ->
                        BeneficiarioRow(
                            beneficiario = item,
                            onClick = { item.id?.let { id -> onItemClick(id) } }
                        )

                        if (index != state.beneficiarios.lastIndex) {
                            Divider(color = LineGreen)
                        }
                    }
                }
            }
        }

        // Botão Adicionar
        Button(
            onClick = onAddClick,
            colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
        ) {
            Text(
                text = "Adicionar Beneficiário",
                color = TextWhite,
                fontSize = 16.sp
            )
        }
    }
}

// ---------------------------------------------------------
// 3. ROW ITEM (ATUALIZADO)
// ---------------------------------------------------------
@Composable
private fun BeneficiarioRow(
    beneficiario: Beneficiario,
    onClick: () -> Unit
) {
    // Determina a opacidade baseada no estado (Inativo = texto mais apagado)
    val nameColor = if (beneficiario.estado == true) TextWhite else TextWhite.copy(alpha = 0.5f)
    val contactColor = if (beneficiario.estado == true) TextWhiteSecondary else TextWhiteSecondary.copy(alpha = 0.5f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nome
        Text(
            text = beneficiario.nome?.ifBlank { "Sem Nome" } ?: "—",
            color = nameColor,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        // Lado Direito: Contacto e Label Inativo
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Contacto (Telefone ou Email)
            Text(
                text = beneficiario.telefone?.ifBlank { beneficiario.email } ?: "—",
                color = contactColor,
                fontSize = 14.sp
            )

            // Se estiver Inativo, mostra label vermelho
            if (beneficiario.estado == false) {
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Inativo",
                    color = ErrorRed,
                    fontSize = 12.sp,
                    // Opcional: style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

// ---------------------------------------------------------
// 4. PREVIEW
// ---------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun BeneficiarioViewPreview() {
    // Dados Mock (incluindo um inativo para teste visual)
    val listaTeste = listOf(
        Beneficiario(id = "1", nome = "João Silva", telefone = "912345678", estado = true),
        Beneficiario(id = "2", nome = "Maria Santos", email = "maria@email.com", estado = false), // Inativo
        Beneficiario(id = "3", nome = "António Costa", telefone = "960000000", estado = true)
    )

    val stateTeste = BeneficiarioState(
        isLoading = false,
        error = null,
        beneficiarios = listaTeste
    )

    // Renderiza apenas o Content
    BeneficiarioContent(
        navController = rememberNavController(),
        state = stateTeste,
        onAddClick = {},
        onItemClick = {}
    )
}