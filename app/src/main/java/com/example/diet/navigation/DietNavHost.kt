package com.example.diet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.diet.core.model.MealType
import com.example.diet.core.navigation.AnalysisRoute
import com.example.diet.core.navigation.DiaryRoute
import com.example.diet.core.navigation.FoodRegisterRoute
import com.example.diet.core.navigation.HomeRoute
import com.example.diet.core.navigation.SettingsRoute
import com.example.diet.feature.analysis.AnalysisScreen
import com.example.diet.feature.diary.DiaryScreen
import com.example.diet.feature.diary.FoodRegisterScreen
import com.example.diet.feature.home.HomeScreen
import com.example.diet.feature.settings.SettingsScreen
import java.time.LocalDate

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
            HomeScreen(onNavigateToDiary = { navController.navigate(DiaryRoute) })
        }
        composable<DiaryRoute> {
            DiaryScreen(
                onNavigateToFoodRegister = { mealType, date ->
                    navController.navigate(FoodRegisterRoute(mealType.name, date.toString()))
                }
            )
        }
        composable<AnalysisRoute> {
            AnalysisScreen()
        }
        composable<SettingsRoute> {
            SettingsScreen()
        }
        composable<FoodRegisterRoute> { backStack ->
            val route = backStack.toRoute<FoodRegisterRoute>()
            FoodRegisterScreen(
                mealType = MealType.valueOf(route.mealType),
                date = LocalDate.parse(route.date),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
