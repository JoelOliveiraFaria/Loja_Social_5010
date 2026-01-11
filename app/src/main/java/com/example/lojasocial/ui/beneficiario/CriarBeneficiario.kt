package com.example.lojasocial.ui.beneficiario

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.lojasocial.models.Beneficiario

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun CriarBeneficiarioView(
    navController: NavController,
    viewModel: BeneficiarioViewModel = hiltViewModel()
) {
    var nome by remember { mutableStateOf("") }
    var nif by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(true) }

    val uiState = viewModel.uiState.value

    Box(modifier = Modifier.fillMaxSize().background(BgGreen)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            // Cabeçalho
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                IconButton(onClick = { navController.popBackStack() }, modifier = Modifier.align(Alignment.CenterStart)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = WhiteColor)
                }
                Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.height(50.dp).align(Alignment.Center).clickable { navController.navigate("welcome") }, contentScale = ContentScale.Fit)
            }

            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                Spacer(Modifier.height(16.dp))
                Text("Novo Beneficiário", style = MaterialTheme.typography.headlineMedium, color = WhiteColor, fontWeight = FontWeight.Bold)

                if (uiState.error != null) {
                    Text(uiState.error!!, color = Color.Red, fontSize = 14.sp)
                }

                Spacer(Modifier.height(32.dp))
                BeneficiarioTextField(nome, { nome = it }, "Nome Completo")
                Spacer(Modifier.height(16.dp))
                BeneficiarioTextField(nif, { nif = it }, "NIF")
                Spacer(Modifier.height(16.dp))
                BeneficiarioTextField(email, { email = it }, "E-mail (Obrigatório)")
                Spacer(Modifier.height(16.dp))
                BeneficiarioTextField(telefone, { telefone = it }, "Telefone")

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 16.dp)) {
                    Checkbox(checked = estado, onCheckedChange = { estado = it }, colors = CheckboxDefaults.colors(checkedColor = IpcaButtonGreen, uncheckedColor = WhiteColor))
                    Text("Ativo", color = WhiteColor)
                }

                Button(
                    onClick = {
                        viewModel.criar(
                            com.example.lojasocial.models.Beneficiario(
                                nome = nome, nif = nif, email = email, telefone = telefone, estado = estado
                            )
                        ) { navController.popBackStack() }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                    enabled = !uiState.isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = WhiteColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar Registo", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BeneficiarioTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = IpcaButtonGreen,
            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
        ),
        shape = RoundedCornerShape(12.dp)
    )
}