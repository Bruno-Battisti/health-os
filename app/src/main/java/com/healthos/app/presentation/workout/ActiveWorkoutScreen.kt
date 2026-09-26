package com.healthos.app.presentation.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthos.app.domain.model.Exercise
import com.healthos.app.domain.model.ExerciseSet

@Composable
fun ActiveWorkoutScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ActiveWorkoutViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) onFinished()
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(24.dp).testTag("screen_active_workout"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(text = uiState.workout?.name.orEmpty(), style = MaterialTheme.typography.headlineMedium)
        }

        item {
            RestTimerRow(
                secondsRemaining = uiState.restSecondsRemaining,
                isRunning = uiState.isTimerRunning,
                onToggle = viewModel::toggleTimer,
                onReset = viewModel::resetTimer,
            )
        }

        item {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.newExerciseName,
                    onValueChange = viewModel::onNewExerciseNameChange,
                    label = { Text("Novo exercício") },
                    modifier = Modifier.testTag("input_exercise_name"),
                )
                Button(onClick = viewModel::addExercise, modifier = Modifier.testTag("button_add_exercise")) {
                    Text("Adicionar")
                }
            }
        }

        uiState.workout?.exercises?.forEach { exercise ->
            item {
                ExerciseCard(
                    exercise = exercise,
                    setInput = uiState.setInputs[exercise.id] ?: SetInput(),
                    onWeightChange = { viewModel.onSetWeightChange(exercise.id, it) },
                    onRepsChange = { viewModel.onSetRepsChange(exercise.id, it) },
                    onAddSet = { viewModel.addSet(exercise.id) },
                    onToggleSet = viewModel::toggleSetCompleted,
                )
            }
        }

        item {
            HorizontalDivider()
            Button(onClick = viewModel::finishWorkout, modifier = Modifier.testTag("button_finish_workout")) {
                Text("Finalizar treino")
            }
        }
    }
}

@Composable
private fun RestTimerRow(secondsRemaining: Int, isRunning: Boolean, onToggle: () -> Unit, onReset: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Text(text = "Descanso: ${secondsRemaining}s", modifier = Modifier.testTag("text_rest_timer"))
        OutlinedButton(onClick = onToggle, modifier = Modifier.testTag("button_toggle_timer")) {
            Text(if (isRunning) "Pausar" else "Iniciar")
        }
        OutlinedButton(onClick = onReset, modifier = Modifier.testTag("button_reset_timer")) {
            Text("Resetar")
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: Exercise,
    setInput: SetInput,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onAddSet: () -> Unit,
    onToggleSet: (ExerciseSet) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth().testTag("exercise_${exercise.id}")) {
        Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)

        exercise.sets.forEach { set ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = set.completed,
                    onCheckedChange = { onToggleSet(set) },
                    modifier = Modifier.testTag("checkbox_set_${set.id}"),
                )
                Text(text = "${set.weight}kg x ${set.repetitions}")
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            OutlinedTextField(
                value = setInput.weight,
                onValueChange = onWeightChange,
                label = { Text("Peso") },
                modifier = Modifier.width(100.dp).testTag("input_set_weight_${exercise.id}"),
            )
            OutlinedTextField(
                value = setInput.repetitions,
                onValueChange = onRepsChange,
                label = { Text("Reps") },
                modifier = Modifier.width(100.dp).testTag("input_set_reps_${exercise.id}"),
            )
            Button(onClick = onAddSet, modifier = Modifier.testTag("button_add_set_${exercise.id}")) {
                Text("+")
            }
        }
    }
}
