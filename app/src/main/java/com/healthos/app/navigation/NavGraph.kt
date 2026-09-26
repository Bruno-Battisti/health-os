package com.healthos.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.healthos.app.presentation.dashboard.DashboardScreen
import com.healthos.app.presentation.evolution.EvolutionScreen
import com.healthos.app.presentation.profile.OnboardingScreen
import com.healthos.app.presentation.profile.ProfileScreen
import com.healthos.app.presentation.workout.WorkoutScreen

@Composable
fun HealthOsNavGraph(rootViewModel: RootViewModel = hiltViewModel()) {
    val onboardingCompleted by rootViewModel.onboardingCompleted.collectAsState()

    when (onboardingCompleted) {
        null -> Box(modifier = Modifier.fillMaxSize().testTag("screen_loading"))
        false -> OnboardingScreen()
        true -> MainNavGraph()
    }
}

@Composable
private fun MainNavGraph() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                BottomNavItem.entries.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        modifier = Modifier.testTag("nav_${item.route}"),
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(imageVector = item.icon, contentDescription = stringResource(item.labelRes)) },
                        label = { Text(text = stringResource(item.labelRes)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Today.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(BottomNavItem.Today.route) { DashboardScreen() }
            composable(BottomNavItem.Workouts.route) { WorkoutScreen() }
            composable(BottomNavItem.Evolution.route) { EvolutionScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }
    }
}
