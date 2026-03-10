package com.example.diet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.diet.core.designsystem.theme.DietTheme
import com.example.diet.core.navigation.FoodRegisterRoute
import com.example.diet.navigation.DietBottomBar
import com.example.diet.navigation.DietNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DietTheme {
                DietApp()
            }
        }
    }
}

@Composable
private fun DietApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val showBottomBar = navBackStackEntry?.destination?.hasRoute<FoodRegisterRoute>() == false
    Scaffold(
        bottomBar = { if (showBottomBar) DietBottomBar(navController) }
    ) { innerPadding ->
        DietNavHost(navController = navController, modifier = Modifier.padding(innerPadding))
    }
}
