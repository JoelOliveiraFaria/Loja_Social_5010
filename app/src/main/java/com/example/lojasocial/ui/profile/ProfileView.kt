package com.example.lojasocial.ui.profile

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lojasocial.R
import com.example.lojasocial.ui.components.TopBarWithMenu
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)

@Composable
fun ProfileView(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "—"
    val fullName = user?.displayName ?: ""

    val firstName = fullName.split(" ").firstOrNull().orEmpty()
    val lastName = fullName.split(" ").drop(1).joinToString(" ")

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

        Text(
            text = "Perfil",
            color = Color.White,
            fontSize = 34.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp),
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 18.dp)) {
            LabelValue("Primeiro Nome", if (firstName.isBlank()) "—" else firstName)
            Spacer(Modifier.height(18.dp))
            LabelValue("Último Nome", if (lastName.isBlank()) "—" else lastName)
            Spacer(Modifier.height(18.dp))
            LabelValue("Email", email)
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Text(text = label, color = Color(0xFFB7D7CC), fontSize = 14.sp)
    Spacer(Modifier.height(6.dp))
    Text(text = value, color = Color.White, fontSize = 18.sp)
}

@Composable
private fun TopBarMock() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_sas),
            contentDescription = "Serviços de Ação Social",
            modifier = Modifier.height(28.dp)
        )

        Spacer(Modifier.width(12.dp))

        Image(
            painter = painterResource(id = R.drawable.logo_sas),
            contentDescription = "Loja Social",
            modifier = Modifier.height(28.dp)
        )

        Spacer(Modifier.weight(1f))

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
