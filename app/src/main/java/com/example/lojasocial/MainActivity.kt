package com.example.lojasocial

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
// Imports Produtos/Inventário
import com.example.lojasocial.ui.produtos.CriarProdutoView
import com.example.lojasocial.ui.produtos.ProdutosView
import com.example.lojasocial.ui.produtos.DetalhesProdutoView
//Imports Pedidos
import com.example.lojasocial.ui.pedidos.NovosPedidosListView
import com.example.lojasocial.ui.pedidos.PedidoDetalhesView
//Import Entregas
import com.example.lojasocial.ui.entrega.CriarEntregaView
import com.example.lojasocial.ui.entrega.EntregaDetalhesView
import com.example.lojasocial.ui.entrega.EntregasListView
// Outros Imports
import com.example.lojasocial.ui.login.LoginView
import com.example.lojasocial.ui.profile.ProfileView
import com.example.lojasocial.ui.theme.LojaSocialTheme
import com.example.lojasocial.ui.welcome.WelcomeView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
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

                        // --- Beneficiários ---
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

                        // --- Inventário ---
                        composable("inventario") {
                            ProdutosView(navController)
                        }

                        composable("inventario/add") {
                            CriarProdutoView(navController)
                        }

                        composable(
                            route = "inventario/{produtoId}",
                            arguments = listOf(navArgument("produtoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val produtoId = backStackEntry.arguments?.getString("produtoId") ?: ""
                            DetalhesProdutoView(
                                produtoId = produtoId,
                                navController = navController
                            )
                        }
                        // --- Pedidos ---
                        composable("pedidos/novos") {
                            NovosPedidosListView(navController)
                        }

                        composable(
                            route = "pedidos/{pedidoId}",
                            arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: return@composable
                            PedidoDetalhesView(navController, pedidoId)
                        }

                        // --- Entregas ---
                        composable("entregas") {
                            EntregasListView(navController)
                        }

                        composable("entrega/novo") {
                            CriarEntregaView(
                                navController = navController,
                                beneficiarioId = null,
                                pedidoId = null
                            )
                        }

                        composable(
                            route = "entrega/{beneficiarioId}/{pedidoId}",
                            arguments = listOf(
                                navArgument("beneficiarioId") { type = NavType.StringType },
                                navArgument("pedidoId") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val beneficiarioId = backStackEntry.arguments?.getString("beneficiarioId")
                            val pedidoId = backStackEntry.arguments?.getString("pedidoId")

                            CriarEntregaView(
                                navController = navController,
                                beneficiarioId = beneficiarioId,
                                pedidoId = pedidoId
                            )
                        }

                        composable(
                            route = "entrega/detalhes/{entregaId}",
                            arguments = listOf(navArgument("entregaId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val entregaId = backStackEntry.arguments?.getString("entregaId") ?: return@composable
                            EntregaDetalhesView(navController, entregaId)
                        }
                    }
                }
            }
        }
    }
}