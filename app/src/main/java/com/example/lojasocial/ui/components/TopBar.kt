package com.example.lojasocial.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.lojasocial.R
import com.google.firebase.auth.FirebaseAuth

private val MenuCircle = Color(0xFF0F4A3A)
private val MenuBackgroundGreen = Color(0xFF0B3B2E)
private val MenuTextColor = Color.White

@Composable
fun TopBarVoltar(
    navController: NavController,
    title: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {navController.popBackStack()}) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White
            )
        }
        Spacer(Modifier.width(8.dp))

        if (title != null) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.logo_sas),
                contentDescription = "Logo",
                modifier = Modifier.height(42.dp)
            )
        }
    }
}

@Composable
fun TopBarWithMenu(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_sas),
            contentDescription = "Serviços de Ação Social",
            modifier = Modifier.height(42.dp)
        )

        Spacer(Modifier.width(12.dp))

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(44.dp)
                .background(MenuCircle, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(MenuBackgroundGreen)
            ) {
                DropdownMenuItem(
                    text = { Text("Inventário", color = MenuTextColor) },
                    onClick = {
                        expanded = false
                        navController.navigate("inventario")
                    }
                )

                Divider(color = Color(0xFF2C6B55))

                DropdownMenuItem(
                    text = { Text("Beneficiários", color = MenuTextColor) },
                    onClick = {
                        expanded = false
                        navController.navigate("beneficiarios")
                    }
                )

                Divider(color = Color(0xFF2C6B55))

                DropdownMenuItem(
                    text = { Text("Campanhas", color = MenuTextColor) },
                    onClick = {
                        expanded = false
                        navController.navigate("campanhas")
                    }
                )

                Divider(color = Color(0xFF2C6B55))

                DropdownMenuItem(
                    text = { Text("Pedidos/Entregas", color = MenuTextColor) },
                    onClick = {
                        expanded = false
                        navController.navigate("pedidos")
                    }
                )

                Divider(color = Color(0xFF2C6B55))

                DropdownMenuItem(
                    text = { Text("Perfil", color = MenuTextColor) },
                    onClick = {
                        expanded = false
                        navController.navigate("profile")
                    }
                )

                Divider(color = Color(0xFF2C6B55))

                DropdownMenuItem(
                    text = { Text("Logout", color = MenuTextColor) },
                    onClick = {
                        expanded = false
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
