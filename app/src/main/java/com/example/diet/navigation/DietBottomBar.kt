package com.example.diet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.diet.core.navigation.AnalysisRoute
import com.example.diet.core.navigation.DiaryRoute
import com.example.diet.core.navigation.HomeRoute
import com.example.diet.core.navigation.SettingsRoute

private data class TopLevelDestination(
    val route: Any,
    val icon: ImageVector,
    val label: String,
)

private val topLevelDestinations = listOf(
    TopLevelDestination(HomeRoute, Icons.Default.Home, "홈"),
    TopLevelDestination(DiaryRoute, Icons.Default.MenuBook, "식단"),
    TopLevelDestination(AnalysisRoute, Icons.Default.BarChart, "분석"),
    TopLevelDestination(SettingsRoute, Icons.Default.Settings, "설정"),
)

@Composable
fun DietBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        topLevelDestinations.forEach { destination ->
            val selected = currentDestination?.hierarchy?.any {
                it.hasRoute(destination.route::class)
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) },
            )
        }
    }
}
