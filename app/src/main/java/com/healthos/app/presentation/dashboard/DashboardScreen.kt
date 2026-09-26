package com.healthos.app.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("screen_today"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = "Hoje", style = MaterialTheme.typography.headlineMedium)

        if (!uiState.isLoading) {
            uiState.userName?.let { name ->
                val ageText = uiState.userAge?.let { ", $it anos" }.orEmpty()
                Text(text = "$name$ageText", modifier = Modifier.testTag("text_user_summary"))
            }
            uiState.currentWeight?.let { weight ->
                Text(text = "Peso atual: $weight kg", modifier = Modifier.testTag("text_current_weight"))
            }
        }
    }
}
