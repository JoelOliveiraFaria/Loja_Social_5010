package com.example.lojasocial.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.lojasocial.R
import com.example.lojasocial.ui.theme.IpcaButtonGreen
import com.example.lojasocial.ui.theme.IpcaDarkGreen
import com.example.lojasocial.ui.theme.WhiteColor

// 1. ESTE É O COMPONENTE LÓGICO (STATEFUL)
// Ele liga-se ao ViewModel e passa os dados para baixo
@Composable
fun LoginView(
    modifier: Modifier = Modifier,
    onLoginSuccess: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state = viewModel.uiState.value

    LoginContent(
        state = state,
        onEmailChange = { viewModel.setEmail(it) },
        onPasswordChange = { viewModel.setPassword(it) },
        onLoginClick = { viewModel.login(onLoginSuccess) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(IpcaDarkGreen),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.logo_sas), // Confirma se tens esta imagem
                contentDescription = "Logo IPCA",
                modifier = Modifier
                    .width(250.dp)
                    .height(100.dp),
                contentScale = ContentScale.Fit
            )

            // --- CENTRO: FORMULÁRIO ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Campo Email
                OutlinedTextField(
                    value = state.email ?: "",
                    onValueChange = onEmailChange, // Usa a função passada por parâmetro
                    label = { Text("Email", color = WhiteColor) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhiteColor,
                        unfocusedBorderColor = WhiteColor,
                        focusedTextColor = WhiteColor,
                        unfocusedTextColor = WhiteColor,
                        cursorColor = WhiteColor,
                        focusedLabelColor = WhiteColor,
                        unfocusedLabelColor = WhiteColor
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Password
                OutlinedTextField(
                    value = state.password ?: "",
                    onValueChange = onPasswordChange, // Usa a função passada por parâmetro
                    label = { Text("Password", color = WhiteColor) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else
                            Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = WhiteColor)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhiteColor,
                        unfocusedBorderColor = WhiteColor,
                        focusedTextColor = WhiteColor,
                        unfocusedTextColor = WhiteColor,
                        cursorColor = WhiteColor,
                        focusedLabelColor = WhiteColor,
                        unfocusedLabelColor = WhiteColor
                    )
                )

                // Mensagem de Erro
                if (state.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error!!,
                        color = Color(0xFFEF5350),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botão Login
                Button(
                    onClick = onLoginClick, // Usa a função passada por parâmetro
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IpcaButtonGreen),
                    shape = RoundedCornerShape(50),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = WhiteColor,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "Login",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhiteColor
                        )
                    }
                }
            }

            // --- FUNDO: LOGO LOJA SOCIAL ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Logo Loja Social",
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "LOJA SOCIAL",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

// 3. AGORA O PREVIEW JÁ FUNCIONA
// Porque chamamos o LoginContent com dados falsos e sem ViewModel
@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    LoginContent(
        state = LoginState(email = "exemplo@ipca.pt", isLoading = false),
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {}
    )
}