package com.example.lojasocial

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.lojasocial.ui.login.LoginView
import com.example.lojasocial.ui.theme.LojaSocialTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // <--- 1. ISTO É OBRIGATÓRIO PARA O HILT FUNCIONAR
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LojaSocialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    // 2. AQUI CHAMAMOS A TUA VIEW
                    LoginView(
                        modifier = Modifier.padding(innerPadding),
                        onLoginSuccess = {
                            Toast.makeText(
                                this,
                                "Login efetuado com sucesso!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            }
        }
    }
}