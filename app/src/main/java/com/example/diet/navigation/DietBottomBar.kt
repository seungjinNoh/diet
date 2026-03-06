package com.example.diet.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.diet.core.designsystem.icon.DietIcons
import com.example.diet.core.navigation.AnalysisRoute
import com.example.diet.core.navigation.DiaryRoute
import com.example.diet.core.navigation.HomeRoute
import com.example.diet.core.navigation.SettingsRoute

private val ActiveColor = Color(0xFF4CAF50)
private val InactiveColor = Color(0xFFBBBBBB)

private data class TopLevelDestination(
    val route: Any,
    val icon: ImageVector,
    val label: String,
)

private val topLevelDestinations = listOf(
    TopLevelDestination(HomeRoute, DietIcons.Home, "홈"),
    TopLevelDestination(DiaryRoute, DietIcons.Diary, "식단"),
    TopLevelDestination(AnalysisRoute, DietIcons.Analysis, "분석"),
    TopLevelDestination(SettingsRoute, DietIcons.Person, "설정"),
)

@Composable
fun DietBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding(),
    ) {
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFF0F0F0))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp),
        ) {
            topLevelDestinations.forEach { destination ->
                val selected = currentDestination?.hierarchy?.any {
                    it.hasRoute(destination.route::class)
                } == true

                DietNavItem(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    icon = destination.icon,
                    label = destination.label,
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
                )
            }
        }
    }
}

@Composable
private fun DietNavItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = if (selected) ActiveColor else InactiveColor

    Column(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick,
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            color = color,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
