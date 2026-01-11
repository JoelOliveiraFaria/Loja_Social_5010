package com.example.lojasocial.ui.beneficiario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.ui.components.TopBarVoltar

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

    Scaffold(
        containerColor = BgGreen,
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // --- NAVBAR CORRETA ---
            TopBarVoltar(navController = navController, title = "Novo Beneficiário")

            // --- LINHA ---
            Divider(color = Color(0xFF2C6B55))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(Modifier.height(24.dp))

                if (uiState.error != null) {
                    Text(uiState.error!!, color = Color.Red, fontSize = 14.sp)
                    Spacer(Modifier.height(8.dp))
                }

                BeneficiarioTextField(nome, { nome = it }, "Nome Completo")
                Spacer(Modifier.height(16.dp))
                BeneficiarioTextField(nif, { nif = it }, "NIF")
                Spacer(Modifier.height(16.dp))
                BeneficiarioTextField(email, { email = it }, "E-mail (Obrigatório)")
                Spacer(Modifier.height(16.dp))
                BeneficiarioTextField(telefone, { telefone = it }, "Telefone")

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 16.dp)) {
                    Checkbox(
                        checked = estado,
                        onCheckedChange = { estado = it },
                        colors = CheckboxDefaults.colors(checkedColor = IpcaButtonGreen, uncheckedColor = WhiteColor)
                    )
                    Text("Ativo", color = WhiteColor)
                }

                Spacer(Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.criar(
                            Beneficiario(
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
                Spacer(Modifier.height(24.dp))
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