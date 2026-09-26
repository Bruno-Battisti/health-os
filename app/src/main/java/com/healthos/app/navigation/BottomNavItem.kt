package com.healthos.app.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.healthos.app.R

enum class BottomNavItem(
    val route: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Today(route = "today", labelRes = R.string.nav_today, icon = Icons.Filled.DateRange),
    Workouts(route = "workouts", labelRes = R.string.nav_workouts, icon = Icons.Filled.FitnessCenter),
    Evolution(route = "evolution", labelRes = R.string.nav_evolution, icon = Icons.AutoMirrored.Filled.ShowChart),
    Profile(route = "profile", labelRes = R.string.nav_profile, icon = Icons.Filled.Person),
}
