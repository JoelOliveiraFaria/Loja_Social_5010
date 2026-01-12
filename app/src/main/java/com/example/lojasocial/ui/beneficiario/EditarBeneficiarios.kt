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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarVoltar

private val BgGreen = Color(0xFF0B3B2E)
private val IpcaButtonGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White

@Composable
fun EditarBeneficiarioView(
    navController: NavController,
    id: String,
    viewModel: BeneficiarioViewModel = hiltViewModel()
) {
    val state by viewModel.detalheState.collectAsState()

    var nome by remember { mutableStateOf("") }
    var nif by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(true) }

    LaunchedEffect(id) { viewModel.carregarBeneficiario(id) }

    LaunchedEffect(state.beneficiario) {
        state.beneficiario?.let {
            nome = it.nome ?: ""
            nif = it.nif ?: ""
            email = it.email ?: ""
            telefone = it.telefone ?: ""
            estado = it.estado ?: true
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(BgGreen)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {

            // --- NAVBAR (Seta + Logo) ---
            TopBarVoltar(navController = navController, title = null)

            Divider(color = Color(0xFF2C6B55))

            // --- TÍTULO ---
            Text(
                text = "Editar Perfil",
                style = MaterialTheme.typography.headlineMedium,
                color = WhiteColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )

            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                Spacer(Modifier.height(16.dp))

                BeneficiarioTextField(nome, { nome = it }, "Nome")
                Spacer(Modifier.height(16.dp))
                BeneficiarioTextField(nif, { nif = it }, "NIF")
                Spacer(Modifier.height(16.dp))
                BeneficiarioTextField(email, { email = it }, "Email")
                Spacer(Modifier.height(16.dp))
                BeneficiarioTextField(telefone, { telefone = it }, "Telefone")

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 16.dp)) {
                    Switch(checked = estado, onCheckedChange = { estado = it }, colors = SwitchDefaults.colors(checkedThumbColor = IpcaButtonGreen))
                    Spacer(Modifier.width(8.dp))
                    Text(if (estado) "Ativo" else "Inativo", color = WhiteColor)
                }

                Button(
                    onClick = {
                        state.beneficiario?.let {
                            val b = it.copy(nome = nome, nif = nif, email = email, telefone = telefone, estado = estado)
                            viewModel.atualizar(b) { navController.popBackStack() }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = WhiteColor,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Atualizar Dados", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}