package com.example.aprxtiva

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aprxtiva.ui.screens.LoginScreen
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
                    composable("home") {
                        // HomeScreen próximamente
                    }
                    composable("registro") {
                        // RegisterScreen próximamente
                    }
                }
            }
        }
    }
}