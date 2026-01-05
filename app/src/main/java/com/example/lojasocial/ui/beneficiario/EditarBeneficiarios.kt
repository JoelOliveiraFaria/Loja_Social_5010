package com.example.lojasocial.ui.beneficiario

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.models.Beneficiario
import com.example.lojasocial.ui.components.TopBarWithMenu

// Cores do tema
private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val TextWhite = Color.White
private val ErrorRed = Color(0xFFEF5350)

// ---------------------------------------------------------
// 1. ENTRY POINT (Lógica + ViewModel)
// ---------------------------------------------------------
@Composable
fun EditarBeneficiarioView(
    navController: NavController,
    id: String,
    viewModel: BeneficiarioViewModel = hiltViewModel()
) {
    val detalhe by viewModel.detalheState.collectAsState()

    // Carregar dados ao entrar
    LaunchedEffect(id) {
        viewModel.carregarBeneficiario(id)
    }

    EditarBeneficiarioContent(
        navController = navController,
        state = detalhe,
        onSave = { nome, nif, email, telefone, estado -> // <-- Recebe estado
            viewModel.atualizar(
                Beneficiario(
                    id = id,
                    nome = nome,
                    nif = nif,
                    email = email,
                    telefone = telefone,
                    estado = estado // <-- Atualiza o modelo
                )
            ) {
                navController.popBackStack()
            }
        },
        onCancel = { navController.popBackStack() }
    )
}

// ---------------------------------------------------------
// 2. CONTEÚDO VISUAL (Stateless para Preview)
// ---------------------------------------------------------
@Composable
fun EditarBeneficiarioContent(
    navController: NavController,
    state: BeneficiarioDetalheState,
    onSave: (String, String, String, String, Boolean) -> Unit, // <-- Assinatura atualizada
    onCancel: () -> Unit
) {
    // Estados locais do formulário
    var nome by remember { mutableStateOf("") }
    var nif by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(true) }

    // Quando os dados chegarem da API (state.beneficiario), preenchemos os campos 1 vez
    LaunchedEffect(state.beneficiario) {
        val b = state.beneficiario ?: return@LaunchedEffect
        nome = b.nome ?: ""
        nif = b.nif ?: ""
        email = b.email ?: ""
        telefone = b.telefone ?: ""
        estado = b.estado ?: true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarWithMenu(navController)
        Divider(color = LineGreen)

        Text(
            text = "Editar Beneficiário",
            color = TextWhite,
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        // Loading
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // Erro
        state.error?.let {
            Text(text = it, color = ErrorRed, modifier = Modifier.padding(16.dp))
        }

        // Formulário
        Column(modifier = Modifier.padding(16.dp)) {

            FieldWhite(label = "Nome do beneficiário", value = nome, onChange = { nome = it })
            Spacer(Modifier.height(12.dp))

            FieldWhite(label = "NIF", value = nif, onChange = { nif = it })
            Spacer(Modifier.height(12.dp))

            FieldWhite(label = "Email", value = email, onChange = { email = it })
            Spacer(Modifier.height(12.dp))

            FieldWhite(label = "Telefone", value = telefone, onChange = { telefone = it })

            Spacer(Modifier.height(12.dp))

            // Switch de Estado (Ativo/Inativo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (estado) "Estado: Ativo" else "Estado: Inativo",
                    color = TextWhite,
                    fontSize = 16.sp
                )
                Switch(
                    checked = estado,
                    onCheckedChange = { estado = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ButtonGreen,
                        uncheckedThumbColor = Color.LightGray,
                        uncheckedTrackColor = Color.Gray
                    )
                )
            }
        }

        Spacer(Modifier.weight(1f))

        // Botões
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f).height(50.dp)
            ) { Text("Cancelar", color = TextWhite) }

            Spacer(Modifier.width(12.dp))

            Button(
                onClick = { onSave(nome, nif, email, telefone, estado) }, // <-- Envia estado
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f).height(50.dp)
            ) { Text("Guardar", color = TextWhite) }
        }
    }
}

// ---------------------------------------------------------
// 3. COMPONENTE AUXILIAR (Input Branco)
// ---------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FieldWhite(
    label: String,
    value: String,
    onChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, color = TextWhite) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TextWhite,
            unfocusedBorderColor = TextWhite,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            cursorColor = TextWhite
        )
    )
}

// ---------------------------------------------------------
// 4. PREVIEW
// ---------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun EditarBeneficiarioPreview() {
    val bMock = Beneficiario(
        id = "1",
        nome = "João Silva",
        nif = "123456789",
        email = "joao@teste.com",
        telefone = "912345678",
        estado = true
    )

    val stateMock = BeneficiarioDetalheState(
        isLoading = false,
        error = null,
        beneficiario = bMock
    )

    EditarBeneficiarioContent(
        navController = rememberNavController(),
        state = stateMock,
        onSave = { _, _, _, _, _ -> },
        onCancel = {}
    )
}