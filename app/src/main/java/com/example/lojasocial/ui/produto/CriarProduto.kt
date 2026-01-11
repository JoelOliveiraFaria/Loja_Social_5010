package com.example.lojasocial.ui.produtos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarVoltar

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun CriarProdutoView(navController: NavController, viewModel: ProdutosViewModel = hiltViewModel()) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BgGreen,
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- NAVBAR VOLTAR ---
            TopBarVoltar(navController = navController, title = "Novo Produto")

            Divider(color = Color(0xFF2C6B55))

            Column(modifier = Modifier.padding(24.dp)) {

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Produto") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IpcaButtonGreen,
                        unfocusedBorderColor = WhiteColor.copy(alpha = 0.5f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IpcaButtonGreen,
                        unfocusedBorderColor = WhiteColor.copy(alpha = 0.5f),
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (nome.isNotBlank()) {
                            viewModel.criarProdutoMestre(nome, descricao)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Registar", color = WhiteColor, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}