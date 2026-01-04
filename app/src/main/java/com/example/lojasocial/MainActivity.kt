package com.example.lojasocial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lojasocial.ui.campanhas.CampanhasView
import com.example.lojasocial.ui.campanhas.CriarCampanhaView
import com.example.lojasocial.ui.campanhas.DetalhesCampanhaView
import com.example.lojasocial.ui.campanhas.EditarCampanhaView
import com.example.lojasocial.ui.login.LoginView
import com.example.lojasocial.ui.profile.ProfileView
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.example.lojasocial.ui.welcome.WelcomeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LojaSocialTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("login") { LoginView(navController) }
                        composable("welcome") { WelcomeView(navController) }
                        composable("profile") { ProfileView(navController) }
                        composable("campanhas") { CampanhasView(navController) }
                        composable("campanhas/add") { CriarCampanhaView(navController) }
                        composable("campanhas/{id}") { backStack ->
                            val id = backStack.arguments?.getString("id") ?: ""
                            DetalhesCampanhaView(navController, id)
                        }
                        composable("campanhas/{id}/edit") { backStack ->
                            val id = backStack.arguments?.getString("id") ?: ""
                            EditarCampanhaView(navController, id)
                        }
                    }
                }
            }
        }
    }
}
