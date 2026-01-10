package com.example.lojasocial.ui.campanhas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.Campanha
import com.example.lojasocial.ui.components.TopBarVoltar
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val TextWhite = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCampanhaView(
    navController: NavController,
    id: String,
    viewModel: CampanhasViewModel = hiltViewModel()
) {
    val detalhe by viewModel.detalheState.collectAsState()

    LaunchedEffect(id) { viewModel.carregarCampanha(id) }

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    var inicioDigits by remember { mutableStateOf("") }
    var fimDigits by remember { mutableStateOf("") }

    var erroData by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(detalhe.campanha?.id) {
        val c = detalhe.campanha ?: return@LaunchedEffect
        nome = c.nome
        descricao = c.descricao
        inicioDigits = onlyDateDigits(c.dataInicio)
        fimDigits = onlyDateDigits(c.dataFim)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarVoltar(navController, "Editar Campanha")
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
                text = "Editar Campanha",
                color = Color.White,
                fontSize = 30.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (detalhe.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        detalhe.error?.let {
            Text(text = it, color = Color(0xFFEF5350), modifier = Modifier.padding(16.dp))
        }

        Column(modifier = Modifier.padding(16.dp)) {

            FieldWhite(label = "Nome da campanha", value = nome, onChange = { nome = it })
            Spacer(Modifier.height(12.dp))

            FieldWhite(label = "Descrição", value = descricao, onChange = { descricao = it }, minLines = 2)
            Spacer(Modifier.height(12.dp))

            FieldWhiteDate(
                label = "Data de início",
                digitsValue = inicioDigits,
                onDigitsChange = {
                    inicioDigits = it
                    erroData = null
                }
            )
            Spacer(Modifier.height(12.dp))

            FieldWhiteDate(
                label = "Data de fim",
                digitsValue = fimDigits,
                onDigitsChange = {
                    fimDigits = it
                    erroData = null
                }
            )
        }

        erroData?.let {
            Text(
                text = it,
                color = Color(0xFFEF5350),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f).height(50.dp)
            ) { Text("Cancelar", color = Color.White) }

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = {
                    val inicioVal = dateDigitsToSortableInt(inicioDigits)
                    val fimVal = dateDigitsToSortableInt(fimDigits)

                    erroData = when {
                        inicioVal == null || fimVal == null ->
                            "Preenche corretamente as datas."
                             fimVal < inicioVal ->
                            "A data de fim não pode ser anterior à data de início."
                        else -> null
                    }

                    if (erroData == null) {
                        viewModel.atualizar(
                            Campanha(
                                id = id,
                                nome = nome,
                                descricao = descricao,
                                dataInicio = digitsToDashedDatePartial(inicioDigits),
                                dataFim = digitsToDashedDatePartial(fimDigits)
                            )
                        ) {
                            navController.popBackStack()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f).height(50.dp)
            ) { Text("Guardar", color = Color.White) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldWhiteDate(
    label: String,
    digitsValue: String,
    onDigitsChange: (String) -> Unit
) {
    OutlinedTextField(
        value = digitsValue,
        onValueChange = { input -> onDigitsChange(onlyDateDigits(input)) },
        label = { Text(label, color = Color.White) },
        placeholder = { Text("DD/MM/AAAA", color = Color.White.copy(alpha = 0.6f)) },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = DateMaskVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldWhite(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = Color.White) },
        modifier = Modifier.fillMaxWidth(),
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White
        )
    )
}
