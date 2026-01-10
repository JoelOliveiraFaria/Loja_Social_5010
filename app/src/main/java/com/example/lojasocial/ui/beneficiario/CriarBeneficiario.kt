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
import com.example.lojasocial.ui.components.TopBarVoltar
import com.example.lojasocial.ui.components.TopBarWithMenu

// Cores do tema
private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val TextWhite = Color.White

// ---------------------------------------------------------
// 1. ENTRY POINT (Lógica + ViewModel)
// ---------------------------------------------------------
@Composable
fun CriarBeneficiarioView(
    navController: NavController,
    viewModel: BeneficiarioViewModel = hiltViewModel()
) {
    // Passamos a função de guardar para o conteúdo visual
    CriarBeneficiarioContent(
        navController = navController,
        onSave = { novoBeneficiario ->
            viewModel.criar(novoBeneficiario) {
                navController.popBackStack()
            }
        },
        onCancel = {
            navController.popBackStack()
        }
    )
}

// ---------------------------------------------------------
// 2. CONTEÚDO VISUAL (Stateless para suportar Preview)
// ---------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CriarBeneficiarioContent(
    navController: NavController,
    onSave: (Beneficiario) -> Unit,
    onCancel: () -> Unit
) {
    // Estados locais do formulário
    var nome by remember { mutableStateOf("") }
    var nif by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefone by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf(true) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        // TopBar
        TopBarVoltar(navController, "Adicionar Beneficiário")
        Divider(color = LineGreen)

        // Título
        Text(
            text = "Adicionar Beneficiário",
            color = TextWhite,
            fontSize = 30.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )

        // Formulário
        Column(modifier = Modifier.padding(16.dp)) {

            // Campo Nome
            OutlinedTextField(
                value = nome, onValueChange = { nome = it },
                label = { Text("Nome do beneficiário", color = TextWhite) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextWhite,
                    unfocusedBorderColor = TextWhite,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = TextWhite
                )
            )
            Spacer(Modifier.height(12.dp))

            // Campo NIF
            OutlinedTextField(
                value = nif, onValueChange = { nif = it },
                label = { Text("NIF", color = TextWhite) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextWhite,
                    unfocusedBorderColor = TextWhite,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = TextWhite
                )
            )
            Spacer(Modifier.height(12.dp))

            // Campo Email
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email", color = TextWhite) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextWhite,
                    unfocusedBorderColor = TextWhite,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = TextWhite
                )
            )

            Spacer(Modifier.height(12.dp))


            // Campo Telefone
            OutlinedTextField(
                value = telefone, onValueChange = { telefone = it },
                label = { Text("Telefone", color = TextWhite) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TextWhite,
                    unfocusedBorderColor = TextWhite,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = TextWhite
                )
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (estado) "Estado: Ativo" else "Estado: Inativo",
                    color = Color.White,
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
                onClick = {
                    onSave(
                        Beneficiario(
                            nome = nome,
                            nif = nif,
                            email = email,
                            telefone = telefone,
                            estado = estado
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                shape = RoundedCornerShape(50),
                modifier = Modifier.weight(1f).height(50.dp)
            ) { Text("Guardar", color = TextWhite) }
        }
    }
}

// ---------------------------------------------------------
// 3. PREVIEW
// ---------------------------------------------------------
@Preview(showBackground = true)
@Composable
fun CriarBeneficiarioPreview() {
    CriarBeneficiarioContent(
        navController = rememberNavController(),
        onSave = {},
        onCancel = {}
    )
}