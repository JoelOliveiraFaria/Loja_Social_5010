package com.example.lojasocial.ui.campanhas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.Campanha
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)

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
    var inicio by remember { mutableStateOf("") }
    var fim by remember { mutableStateOf("") }

    // quando a campanha chegar, preencher campos 1x
    LaunchedEffect(detalhe.campanha?.id) {
        val c = detalhe.campanha ?: return@LaunchedEffect
        nome = c.nome
        descricao = c.descricao
        inicio = c.dataInicio
        fim = c.dataFim
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarWithMenu(navController)
        Divider(color = LineGreen)

        Text(
            text = "Editar Campanha",
            color = Color.White,
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

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

            FieldWhite(label = "Data de início", value = inicio, onChange = { inicio = it })
            Spacer(Modifier.height(12.dp))

            FieldWhite(label = "Data de fim", value = fim, onChange = { fim = it })
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
                    viewModel.atualizar(
                        Campanha(
                            id = id,
                            nome = nome,
                            descricao = descricao,
                            dataInicio = inicio,
                            dataFim = fim
                        )
                    ) {
                        navController.popBackStack()
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
