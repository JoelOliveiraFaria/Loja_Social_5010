package com.example.lojasocial.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lojasocial.ui.components.TopBarWithMenu

private val BgGreen = Color(0xFF0B3B2E)
private val TextWhite = Color.White

@Composable
fun PedidosTerminadosView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGreen)
    ) {

        TopBarWithMenu(navController)

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pedidos Terminados",
                color = TextWhite,
                fontSize = 22.sp
            )
        }
    }
}
