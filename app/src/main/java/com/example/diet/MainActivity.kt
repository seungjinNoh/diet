package com.example.diet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.diet.core.designsystem.theme.DietTheme
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
    Scaffold(
        bottomBar = { DietBottomBar(navController) }
    ) { innerPadding ->
        DietNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
