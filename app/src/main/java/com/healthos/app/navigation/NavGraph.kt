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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.healthos.app.presentation.account.AccountScreen
import com.healthos.app.presentation.dashboard.DashboardScreen
import com.healthos.app.presentation.evolution.EvolutionScreen
import com.healthos.app.presentation.goal.GoalsScreen
import com.healthos.app.presentation.habit.HabitsScreen
import com.healthos.app.presentation.profile.OnboardingScreen
import com.healthos.app.presentation.profile.ProfileScreen
import com.healthos.app.presentation.weight.MeasurementScreen
import com.healthos.app.presentation.weight.WeightScreen
import com.healthos.app.presentation.workout.ActiveWorkoutScreen
import com.healthos.app.presentation.workout.WorkoutScreen

private const val ROUTE_WEIGHT = "weight"
private const val ROUTE_MEASUREMENTS = "measurements"
private const val ROUTE_ACTIVE_WORKOUT = "workout"
private const val ARG_WORKOUT_ID = "workoutId"
private const val ROUTE_HABITS = "habits"
private const val ROUTE_GOALS = "goals"
private const val ROUTE_ACCOUNT = "account"

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
            composable(BottomNavItem.Today.route) {
                DashboardScreen(
                    onNavigateToWeight = { navController.navigate(ROUTE_WEIGHT) },
                    onNavigateToHabits = { navController.navigate(ROUTE_HABITS) },
                    onNavigateToGoals = { navController.navigate(ROUTE_GOALS) },
                )
            }
            composable(BottomNavItem.Workouts.route) {
                WorkoutScreen(onOpenWorkout = { id -> navController.navigate("$ROUTE_ACTIVE_WORKOUT/$id") })
            }
            composable(BottomNavItem.Evolution.route) { EvolutionScreen() }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(onNavigateToAccount = { navController.navigate(ROUTE_ACCOUNT) })
            }
            composable(ROUTE_ACCOUNT) { AccountScreen() }
            composable(ROUTE_WEIGHT) {
                WeightScreen(onNavigateToMeasurements = { navController.navigate(ROUTE_MEASUREMENTS) })
            }
            composable(ROUTE_MEASUREMENTS) { MeasurementScreen() }
            composable(
                route = "$ROUTE_ACTIVE_WORKOUT/{$ARG_WORKOUT_ID}",
                arguments = listOf(navArgument(ARG_WORKOUT_ID) { type = NavType.LongType }),
            ) {
                ActiveWorkoutScreen(onFinished = { navController.popBackStack() })
            }
            composable(ROUTE_HABITS) { HabitsScreen() }
            composable(ROUTE_GOALS) { GoalsScreen() }
        }
    }
}
