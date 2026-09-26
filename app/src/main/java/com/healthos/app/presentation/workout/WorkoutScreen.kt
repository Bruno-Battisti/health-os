package com.healthos.app.presentation.workout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthos.app.domain.model.Workout

@Composable
fun WorkoutScreen(
    onOpenWorkout: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkoutListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.createdWorkoutId.collect { id -> onOpenWorkout(id) }
    }

    Scaffold(
        modifier = modifier.testTag("screen_workouts"),
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::openCreateDialog, modifier = Modifier.testTag("button_new_workout")) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Novo treino")
            }
        },
    ) { innerPadding ->
        if (uiState.history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(text = "Nenhum treino registrado ainda")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(uiState.history, key = { it.id }) { workout ->
                    WorkoutHistoryRow(
                        workout = workout,
                        onOpen = { onOpenWorkout(workout.id) },
                        onDelete = { viewModel.deleteWorkout(workout.id) },
                    )
                }
            }
        }
    }

    if (uiState.isCreateDialogOpen) {
        AlertDialog(
            onDismissRequest = viewModel::dismissCreateDialog,
            title = { Text("Novo treino") },
            text = {
                OutlinedTextField(
                    value = uiState.newWorkoutName,
                    onValueChange = viewModel::onNewWorkoutNameChange,
                    label = { Text("Nome do treino") },
                    modifier = Modifier.testTag("input_workout_name"),
                )
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmCreateWorkout, modifier = Modifier.testTag("button_confirm_new_workout")) {
                    Text("Criar")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissCreateDialog) { Text("Cancelar") }
            },
        )
    }
}

@Composable
private fun WorkoutHistoryRow(workout: Workout, onOpen: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("workout_item_${workout.id}"),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.clickable(onClick = onOpen)) {
            Text(text = workout.name, style = MaterialTheme.typography.titleMedium)
            val durationText = workout.durationMinutes?.let { " • $it min" }.orEmpty()
            Text(text = "${workout.date}$durationText", style = MaterialTheme.typography.bodySmall)
        }
        IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_workout_${workout.id}")) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Excluir treino")
        }
    }
}
