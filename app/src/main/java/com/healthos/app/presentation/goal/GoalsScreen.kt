package com.healthos.app.presentation.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthos.app.domain.model.GoalWithProgress

@Composable
fun GoalsScreen(
    modifier: Modifier = Modifier,
    viewModel: GoalsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.testTag("screen_goals"),
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::openAddDialog, modifier = Modifier.testTag("button_new_goal")) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Nova meta")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(uiState.goals, key = { it.goal.id }) { goalWithProgress ->
                GoalRow(
                    goalWithProgress = goalWithProgress,
                    onCheckIn = { viewModel.checkIn(goalWithProgress.goal.id) },
                    onDelete = { viewModel.deleteGoal(goalWithProgress.goal.id) },
                )
            }
        }
    }

    if (uiState.isAddDialogOpen) {
        AlertDialog(
            onDismissRequest = viewModel::dismissAddDialog,
            title = { Text("Nova meta") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.newGoalDescription,
                        onValueChange = viewModel::onDescriptionChange,
                        label = { Text("Descrição") },
                        modifier = Modifier.testTag("input_goal_description"),
                    )
                    OutlinedTextField(
                        value = uiState.newGoalTargetCount,
                        onValueChange = viewModel::onTargetCountChange,
                        label = { Text("Vezes a cumprir") },
                        modifier = Modifier.testTag("input_goal_target"),
                    )
                    OutlinedTextField(
                        value = uiState.newGoalDeadline,
                        onValueChange = viewModel::onDeadlineChange,
                        label = { Text("Prazo (AAAA-MM-DD)") },
                        modifier = Modifier.testTag("input_goal_deadline"),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmAddGoal, modifier = Modifier.testTag("button_confirm_new_goal")) {
                    Text("Criar")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissAddDialog) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun GoalRow(goalWithProgress: GoalWithProgress, onCheckIn: () -> Unit, onDelete: () -> Unit) {
    val goal = goalWithProgress.goal
    Column(modifier = Modifier.fillMaxWidth().testTag("goal_${goal.id}")) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(text = goal.description, style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_goal_${goal.id}")) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Excluir meta")
            }
        }
        Text(text = "${goalWithProgress.checkInCount}/${goal.targetCount} até ${goal.deadline}")
        LinearProgressIndicator(
            progress = { (goalWithProgress.checkInCount.toFloat() / goal.targetCount).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth(),
        )
        Button(onClick = onCheckIn, modifier = Modifier.testTag("button_checkin_${goal.id}")) {
            Text("Concluí hoje")
        }
    }
}
