package com.example.lojasocial.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarWithMenu


private val BgGreen = Color(0xFF0B3B2E)
private val LineGreen = Color(0xFF2C6B55)
private val ButtonGreen = Color(0xFF1F6F43)
private val TextWhite = Color.White

@Composable
fun PedidosView(navController: NavController) {
    PedidosContent(navController)
}

@Composable
fun PedidosContent(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {
        // TopBar
        TopBarWithMenu(navController)

        Divider(color = LineGreen)

        // Título fixo em cima
        Text(
            text = "Pedidos/Entregas",
            color = TextWhite,
            fontSize = 34.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp)
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PedidoButton(
                text = "Novos Pedidos",
                onClick = { navController.navigate("pedidos/novos") }
            )
            Spacer(modifier = Modifier.height(20.dp))
            PedidoButton(
                text = "Entregas",
                onClick = { navController.navigate("entregas/andamento") }
            )
            Spacer(modifier = Modifier.height(20.dp))
            PedidoButton(
                text = "Entregas Terminadas",
                onClick = { navController.navigate("entregas/terminados") }
            )
            Spacer(modifier = Modifier.height(20.dp))
            PedidoButton(
                text = "Criar Entrega",
                onClick = { navController.navigate("entrega/novo") }
            )
        }
    }
}


@Composable
private fun PedidoButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
        shape = RoundedCornerShape(50),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(50.dp)
    ) {
        Text(
            text = text,
            color = TextWhite,
            fontSize = 16.sp
        )
    }
}
