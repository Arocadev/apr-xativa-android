package com.example.aprxtiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aprxtiva.ui.screens.*
import com.example.aprxtiva.ui.theme.APRXativaTheme
import com.example.aprxtiva.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APRXativaTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("registro")
                            },
                            viewModel = authViewModel
                        )
                    }
                    composable("registro") {
                        RegisterScreen(
                            onRegistroSuccess = {
                                navController.navigate("login") {
                                    popUpTo("registro") { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.popBackStack()
                            },
                            viewModel = authViewModel
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onNavigateToVehiculos = { navController.navigate("vehiculos") },
                            onNavigateToDerechos = { navController.navigate("derechos") },
                            onNavigateToPerfil = { navController.navigate("perfil") },
                            onNavigateToAjustes = { navController.navigate("opciones") },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            viewModel = authViewModel
                        )
                    }
                    composable("vehiculos") {
                        VehiculosScreen(
                            onVolver = { navController.popBackStack() }
                        )
                    }
                    composable("derechos") {
                        DerechosScreen(
                            onVolver = { navController.popBackStack() }
                        )
                    }
                    composable("perfil") {
                        PerfilScreen(
                            onVolver = { navController.popBackStack() },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            viewModel = authViewModel
                        )
                    }
                    composable("opciones") {
                        OpcionesScreen(
                            onVolver = { navController.popBackStack() },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            viewModel = authViewModel
                        )
                    }
                }
            }
        }
    }
}