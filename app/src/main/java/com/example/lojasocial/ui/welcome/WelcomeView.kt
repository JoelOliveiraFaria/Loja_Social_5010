package com.example.lojasocial.ui.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lojasocial.R
import com.example.lojasocial.ui.components.TopBarWithMenu
import com.google.firebase.auth.FirebaseAuth

private val BgGreen = Color(0xFF0B3B2E)   // verde escuro (mockup feel)
private val LineGreen = Color(0xFF2C6B55)

@Composable
fun WelcomeView(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val name = user?.displayName ?: (user?.email?.substringBefore("@") ?: "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        TopBarWithMenu(navController)

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(LineGreen)
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Bem Vindo",
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Normal
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "\"$name\"",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun TopBarMock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo IPCA / SAS
        Image(
            painter = painterResource(id = R.drawable.logo_sas),
            contentDescription = "Serviços de Ação Social",
            modifier = Modifier.height(48.dp)
        )

        Spacer(Modifier.width(12.dp))

        Spacer(Modifier.weight(1f))

        // Botão menu
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color(0xFF0F4A3A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { /* por agora só visual */ }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        }
    }
}
