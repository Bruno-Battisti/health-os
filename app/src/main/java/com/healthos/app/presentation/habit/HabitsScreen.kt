package com.healthos.app.presentation.habit

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HabitsScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier.testTag("screen_habits"),
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::openAddDialog, modifier = Modifier.testTag("button_new_habit")) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Novo hábito")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(uiState.habits, key = { it.habit.id }) { display ->
                HabitRow(
                    display = display,
                    logInput = uiState.logInputs[display.habit.id].orEmpty(),
                    onLogInputChange = { viewModel.onLogInputChange(display.habit.id, it) },
                    onLog = { viewModel.logEntry(display.habit.id) },
                    onDelete = { viewModel.deleteHabit(display.habit.id) },
                )
            }
        }
    }

    if (uiState.isAddDialogOpen) {
        AlertDialog(
            onDismissRequest = viewModel::dismissAddDialog,
            title = { Text("Novo hábito") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uiState.newHabitName,
                        onValueChange = viewModel::onNewHabitNameChange,
                        label = { Text("Nome") },
                        modifier = Modifier.testTag("input_habit_name"),
                    )
                    OutlinedTextField(
                        value = uiState.newHabitTarget,
                        onValueChange = viewModel::onNewHabitTargetChange,
                        label = { Text("Meta diária") },
                        modifier = Modifier.testTag("input_habit_target"),
                    )
                    OutlinedTextField(
                        value = uiState.newHabitUnit,
                        onValueChange = viewModel::onNewHabitUnitChange,
                        label = { Text("Unidade (ex: L, h, passos)") },
                        modifier = Modifier.testTag("input_habit_unit"),
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = viewModel::confirmAddHabit, modifier = Modifier.testTag("button_confirm_new_habit")) {
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
private fun HabitRow(
    display: HabitDisplay,
    logInput: String,
    onLogInputChange: (String) -> Unit,
    onLog: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().testTag("habit_${display.habit.id}")) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "${display.habit.name}: ${display.todayValue}/${display.habit.targetValue} ${display.habit.unit}",
                style = MaterialTheme.typography.titleMedium,
            )
            IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_habit_${display.habit.id}")) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Excluir hábito")
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = logInput,
                onValueChange = onLogInputChange,
                label = { Text("Registrar") },
                modifier = Modifier.testTag("input_log_${display.habit.id}"),
            )
            Button(onClick = onLog, modifier = Modifier.testTag("button_log_${display.habit.id}")) {
                Text("+")
            }
        }
    }
}
