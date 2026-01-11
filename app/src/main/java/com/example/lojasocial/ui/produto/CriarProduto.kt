package com.example.lojasocial.ui.produtos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.lojasocial.R

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun CriarProdutoView(navController: NavController, viewModel: ProdutosViewModel = hiltViewModel()) {
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(BgGreen)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

            // --- CABEÇALHO ---
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = WhiteColor)
                }
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.height(50.dp).align(Alignment.Center).clickable { navController.navigate("welcome") },
                    contentScale = ContentScale.Fit
                )
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text("Novo Produto", style = MaterialTheme.typography.headlineMedium, color = WhiteColor, fontWeight = FontWeight.Bold)

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome do Produto") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = IpcaButtonGreen,
                        unfocusedBorderColor = WhiteColor.copy(alpha = 0.5f)
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
                        unfocusedBorderColor = WhiteColor.copy(alpha = 0.5f)
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