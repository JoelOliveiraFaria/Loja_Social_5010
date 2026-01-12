package com.example.lojasocial.ui.campanhas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarVoltar

private val BgGreenColor = Color(0xFF0B3B2E)
private val IpcaGreen = Color(0xFF1F6F43)
private val WhiteFixed = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCampanhaView(
    navController: NavController,
    id: String,
    viewModel: CampanhasViewModel = hiltViewModel()
) {
    val detalhe by viewModel.detalheState.collectAsState()

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var inicioDigits by remember { mutableStateOf("") }
    var fimDigits by remember { mutableStateOf("") }

    LaunchedEffect(id) {
        viewModel.carregarCampanha(id)
    }

    LaunchedEffect(detalhe.campanha) {
        detalhe.campanha?.let {
            nome = it.nome
            descricao = it.descricao
            inicioDigits = it.dataInicio.replace("-", "")
            fimDigits = it.dataFim.replace("-", "")
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgGreenColor)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

            // --- NAVBAR (Seta + Logo) ---
            TopBarVoltar(navController = navController, title = null)

            Divider(color = Color(0xFF2C6B55))

            // --- TÍTULO ---
            Text(
                text = "Editar Campanha",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteFixed,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                FieldWhite("Nome da Campanha", nome) { nome = it }
                Spacer(Modifier.height(16.dp))

                FieldWhite("Descrição", descricao, minLines = 3) { descricao = it }
                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(Modifier.weight(1f)) {
                        DateFieldWhite("Data Início", inicioDigits) { inicioDigits = it }
                    }
                    Column(Modifier.weight(1f)) {
                        DateFieldWhite("Data Fim", fimDigits) { fimDigits = it }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        val c = detalhe.campanha?.copy(
                            nome = nome,
                            descricao = descricao,
                            dataInicio = digitsToDashedDatePartial(inicioDigits),
                            dataFim = digitsToDashedDatePartial(fimDigits)
                        )
                        if (c != null) {
                            viewModel.atualizar(c) { navController.popBackStack() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IpcaGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Salvar Alterações", fontWeight = FontWeight.Bold, color = WhiteFixed)
                }
            }
        }
    }
}

// Funções auxiliares (reutilizando as que já estão no ficheiro)
@Composable
private fun FieldWhite(label: String, value: String, minLines: Int = 1, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
        )
    )
}

@Composable
private fun DateFieldWhite(label: String, digitsValue: String, onDigitsChange: (String) -> Unit) {
    OutlinedTextField(
        value = digitsValue,
        onValueChange = { input -> onDigitsChange(onlyDateDigits(input)) },
        label = { Text(label) },
        placeholder = { Text("DD/MM/AAAA", color = Color.White.copy(alpha = 0.4f)) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = DateMaskVisualTransformation(),
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
        )
    )
}