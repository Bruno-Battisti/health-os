package com.healthos.app.presentation.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthos.app.domain.model.PersonalGoal

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("screen_profile"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Perfil", style = MaterialTheme.typography.headlineMedium)

        if (!uiState.isLoading) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nome") },
                modifier = Modifier.fillMaxWidth().testTag("input_name"),
            )
            OutlinedTextField(
                value = uiState.birthDate,
                onValueChange = viewModel::onBirthDateChange,
                label = { Text("Data de nascimento (AAAA-MM-DD)") },
                modifier = Modifier.fillMaxWidth().testTag("input_birthdate"),
            )
            OutlinedTextField(
                value = uiState.heightCm,
                onValueChange = viewModel::onHeightChange,
                label = { Text("Altura (cm)") },
                modifier = Modifier.fillMaxWidth().testTag("input_height"),
            )

            Text(text = "Objetivo", style = MaterialTheme.typography.titleMedium)
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PersonalGoal.entries.forEach { goal ->
                    FilterChip(
                        selected = uiState.goal == goal,
                        onClick = { viewModel.onGoalChange(goal) },
                        label = { Text(goal.displayLabel()) },
                        modifier = Modifier.testTag("goal_${goal.name}"),
                    )
                }
            }

            uiState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("text_error"))
            }
            if (uiState.saveSuccess) {
                Text(text = "Perfil atualizado", modifier = Modifier.testTag("text_save_success"))
            }

            Button(
                onClick = viewModel::save,
                enabled = !uiState.isSaving,
                modifier = Modifier.testTag("button_save"),
            ) {
                Text("Salvar")
            }
        }
    }
}
