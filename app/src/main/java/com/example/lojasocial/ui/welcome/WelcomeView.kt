package com.example.lojasocial.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarWithMenu
import com.google.firebase.auth.FirebaseAuth

private val BgGreen = Color(0xFF0B3B2E)
private val CardGreen = Color(0xFF164A3D)
private val IpcaGreen = Color(0xFF1F6F43)
private val WhiteColor = Color.White
private val SecondaryText = Color(0xFFB7D7CC)

@Composable
fun WelcomeView(navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser
    val name = user?.displayName ?: (user?.email?.substringBefore("@") ?: "Utilizador")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        // ---  TOPBAR COM MENU ---
        TopBarWithMenu(navController = navController)

        Divider(color = Color(0xFF2C6B55))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- SAUDAÇÃO ---
            Text(text = "Olá,", color = SecondaryText, fontSize = 18.sp)
            Text(text = name, color = WhiteColor, fontSize = 28.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Gestão Principal",
                color = WhiteColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- GRID DE ACESSO RÁPIDO ---
            // Beneficiários
            MenuCard(
                title = "Beneficiários",
                subtitle = "Famílias e Utentes",
                icon = Icons.Default.People,
                onClick = { navController.navigate("beneficiarios") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Pedidos
            MenuCard(
                title = "Pedidos",
                subtitle = "Novas solicitações",
                icon = Icons.Default.Assignment,
                onClick = { navController.navigate("pedidos/novos") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Entregas
            MenuCard(
                title = "Entregas",
                subtitle = "Logística e Saídas",
                icon = Icons.Default.LocalShipping,
                onClick = { navController.navigate("entregas") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campanhas
            MenuCard(
                title = "Campanhas",
                subtitle = "Eventos de Recolha",
                icon = Icons.Default.DateRange,
                onClick = { navController.navigate("campanhas") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Loja Social • IPCA SAS",
                color = SecondaryText.copy(alpha = 0.3f),
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardGreen)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(IpcaGreen, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = WhiteColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    color = WhiteColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = SecondaryText,
                    fontSize = 13.sp
                )
            }
        }
    }
}