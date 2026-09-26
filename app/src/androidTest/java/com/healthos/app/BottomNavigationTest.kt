package com.healthos.app

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class BottomNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun allFourDestinationsAreReachableFromBottomNav() {
        composeTestRule.onNodeWithTag("screen_today").assertExists()

        composeTestRule.onNodeWithTag("nav_workouts").performClick()
        composeTestRule.onNodeWithTag("screen_workouts").assertExists()

        composeTestRule.onNodeWithTag("nav_evolution").performClick()
        composeTestRule.onNodeWithTag("screen_evolution").assertExists()

        composeTestRule.onNodeWithTag("nav_profile").performClick()
        composeTestRule.onNodeWithTag("screen_profile").assertExists()

        composeTestRule.onNodeWithTag("nav_today").performClick()
        composeTestRule.onNodeWithTag("screen_today").assertExists()
    }
}
