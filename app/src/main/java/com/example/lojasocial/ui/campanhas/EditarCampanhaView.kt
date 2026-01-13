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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

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
    var erroData by remember { mutableStateOf<String?>(null) }

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

    LaunchedEffect(inicioDigits, fimDigits) {
        // Só valida quando ambas tiverem 8 dígitos (ddMMyyyy)
        if (inicioDigits.length < 8 || fimDigits.length < 8) {
            erroData = null
            return@LaunchedEffect
        }

        val inicioVal = dateDigitsToSortableInt(inicioDigits)
        val fimVal = dateDigitsToSortableInt(fimDigits)

        erroData = when {
            inicioVal == null || fimVal == null -> "Preenche corretamente as datas."
            fimVal < inicioVal -> "A data de fim não pode ser anterior ao início."
            else -> null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreenColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {

            TopBarVoltar(navController = navController, title = null)

            Divider(color = Color(0xFF2C6B55))

            Text(
                text = "Editar Campanha",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteFixed,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                FieldWhite("Nome da Campanha", nome) { nome = it }
                Spacer(Modifier.height(16.dp))

                FieldWhite("Descrição", descricao, minLines = 3) { descricao = it }
                Spacer(Modifier.height(16.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        DateFieldWhite("Data Início", inicioDigits) { inicioDigits = it }
                    }
                    Column(Modifier.weight(1f)) {
                        DateFieldWhite("Data Fim", fimDigits) { fimDigits = it }
                    }
                }

                if (erroData != null) {
                    Text(
                        text = erroData!!,
                        color = Color(0xFFEF5350),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = {
                        if (inicioDigits.length < 8 || fimDigits.length < 8) {
                            erroData = "Preenche corretamente as datas."
                            return@Button
                        }

                        if (erroData == null) {
                            val c = detalhe.campanha?.copy(
                                nome = nome,
                                descricao = descricao,
                                dataInicio = digitsToDashedDatePartial(inicioDigits),
                                dataFim = digitsToDashedDatePartial(fimDigits)
                            )
                            if (c != null) {
                                viewModel.atualizar(c) { navController.popBackStack() }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IpcaGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Salvar Alterações", fontWeight = FontWeight.Bold, color = WhiteFixed)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
