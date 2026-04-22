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
import com.example.aprxtiva.viewmodel.SolicitudViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            APRXativaTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val solicitudViewModel: SolicitudViewModel = viewModel()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { activo ->
                                if (activo) {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("espera") {
                                        popUpTo("login") { inclusive = true }
                                    }
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
                                navController.navigate("espera") {
                                    popUpTo("registro") { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.popBackStack()
                            },
                            viewModel = authViewModel
                        )
                    }
                    composable("espera") {
                        EsperaScreen(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("espera") { inclusive = true }
                                }
                            },
                            onAprobado = {
                                navController.navigate("home") {
                                    popUpTo("espera") { inclusive = true }
                                }
                            },
                            authViewModel = authViewModel,
                            solicitudViewModel = solicitudViewModel
                        )
                    }
                    composable("home") {
                        HomeScreen(
                            onNavigateToVehiculos = { navController.navigate("vehiculos") },
                            onNavigateToDerechos = { navController.navigate("derechos") },
                            onNavigateToPerfil = { navController.navigate("perfil") },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("home") { inclusive = true }
                                }
                            },
                            viewModel = authViewModel
                        )
                    }
                    composable("vehiculos") { }
                    composable("derechos") { }
                    composable("perfil") { }
                }
            }
        }
    }
}