package com.example.lojasocial.ui.produtos

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
import com.example.lojasocial.ui.components.TopBarVoltar
import com.example.lojasocial.ui.components.TopBarWithMenu

@Composable
fun CriarProdutoView(
    navController: NavController,
    viewModel: ProdutosViewModel = hiltViewModel()
) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    // Fundo verde escuro consistente com o tema
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0B3B2E))) {
        TopBarVoltar(navController, "Criar Produto")

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Novo Produto Geral",
                color = Color.White,
                fontSize = 26.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Configuração de cores para visibilidade total
            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.LightGray,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color(0xFF2C6B55),
                cursorColor = Color.White
            )

            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome (ex: Arroz)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição (ex: Agulha 1kg)") },
                modifier = Modifier.fillMaxWidth(),
                colors = textFieldColors,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (nome.isNotBlank()) {
                        viewModel.criarProdutoMestre(nome, descricao)
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F6F43)),
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text("Registar Produto", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}