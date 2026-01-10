package com.example.lojasocial.ui.campanhas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarCampanhaView(
    navController: NavController,
    viewModel: CampanhasViewModel = hiltViewModel()
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    var inicioDigits by remember { mutableStateOf("") }
    var fimDigits by remember { mutableStateOf("") }

    var erroData by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarVoltar(navController, "Criar Campanha")
        Divider(color = LineGreen)

        Text(
            text = "Adicionar Campanha",
            color = Color.White,
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome da campanha", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                )
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = inicioDigits,
                onValueChange = { input ->
                    inicioDigits = onlyDateDigits(input)
                    erroData = null
                },
                label = { Text("Data de início", color = Color.White) },
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
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = fimDigits,
                onValueChange = { input ->
                    fimDigits = onlyDateDigits(input)
                    erroData = null
                },
                label = { Text("Data de fim", color = Color.White) },
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
                        viewModel.criar(
                            Campanha(
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
