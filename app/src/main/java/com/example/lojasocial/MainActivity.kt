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
// Imports Campanhas
import com.example.lojasocial.ui.campanhas.CampanhasView
import com.example.lojasocial.ui.campanhas.CriarCampanhaView
import com.example.lojasocial.ui.campanhas.DetalhesCampanhaView
import com.example.lojasocial.ui.campanhas.EditarCampanhaView
// Imports Beneficiarios
import com.example.lojasocial.ui.beneficiario.BeneficiarioView
import com.example.lojasocial.ui.beneficiario.CriarBeneficiarioView
import com.example.lojasocial.ui.beneficiario.DetalhesBeneficiarioView
import com.example.lojasocial.ui.beneficiario.EditarBeneficiarioView
// Outros Imports
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
                        // --- Auth / Welcome ---
                        composable("login") { LoginView(navController) }
                        composable("welcome") { WelcomeView(navController) }
                        composable("profile") { ProfileView(navController) }

                        // --- Campanhas ---
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

                        composable("beneficiarios") { BeneficiarioView(navController) }
                        composable("beneficiarios/add") { CriarBeneficiarioView(navController) }
                        composable("beneficiarios/{id}") { backStack ->
                            val id = backStack.arguments?.getString("id") ?: ""
                            DetalhesBeneficiarioView(navController, id)
                        }
                        composable("beneficiarios/{id}/edit") { backStack ->
                            val id = backStack.arguments?.getString("id") ?: ""
                            EditarBeneficiarioView(navController, id)
                        }
                    }
                }
            }
        }
    }
}