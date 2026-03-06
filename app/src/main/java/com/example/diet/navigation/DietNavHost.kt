package com.example.diet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.diet.core.navigation.AnalysisRoute
import com.example.diet.core.navigation.DiaryRoute
import com.example.diet.core.navigation.HomeRoute
import com.example.diet.core.navigation.SettingsRoute
import com.example.diet.feature.analysis.AnalysisScreen
import com.example.diet.feature.diary.DiaryScreen
import com.example.diet.feature.home.HomeScreen
import com.example.diet.feature.settings.SettingsScreen

@Composable
fun DietNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        composable<HomeRoute> {
            HomeScreen()
        }
        composable<DiaryRoute> {
            DiaryScreen()
        }
        composable<AnalysisRoute> {
            AnalysisScreen()
        }
        composable<SettingsRoute> {
            SettingsScreen()
        }
    }
}
