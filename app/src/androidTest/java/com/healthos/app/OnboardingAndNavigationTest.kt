package com.healthos.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class OnboardingAndNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun onboardingRequiresFieldsThenUnlocksBottomNavigation() {
        composeTestRule.onNodeWithTag("screen_onboarding").assertExists()

        composeTestRule.onNodeWithTag("button_submit").performClick()
        composeTestRule.onNodeWithTag("text_error").assertExists()

        composeTestRule.onNodeWithTag("input_name").performTextInput("Ana")
        composeTestRule.onNodeWithTag("input_birthdate").performTextInput("1995-04-10")
        composeTestRule.onNodeWithTag("input_height").performTextInput("165")
        composeTestRule.onNodeWithTag("input_weight").performTextInput("60")
        composeTestRule.onNodeWithTag("goal_MAINTAIN_WEIGHT").performClick()
        composeTestRule.onNodeWithTag("button_submit").performClick()

        composeTestRule.onNodeWithTag("screen_today").assertExists()

        composeTestRule.onNodeWithTag("nav_workouts").performClick()
        composeTestRule.onNodeWithTag("screen_workouts").assertExists()

        composeTestRule.onNodeWithTag("nav_evolution").performClick()
        composeTestRule.onNodeWithTag("screen_evolution").assertExists()

        composeTestRule.onNodeWithTag("nav_profile").performClick()
        composeTestRule.onNodeWithTag("screen_profile").assertExists()

        composeTestRule.onNodeWithTag("nav_today").performClick()
        composeTestRule.onNodeWithTag("screen_today").assertExists()
        composeTestRule.onNodeWithTag("text_user_summary").assertExists()
    }
}
