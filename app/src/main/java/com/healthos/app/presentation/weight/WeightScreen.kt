package com.healthos.app.presentation.weight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthos.app.domain.model.WeightEntry
import com.healthos.app.domain.model.WeightTrend

@Composable
fun WeightScreen(
    onNavigateToMeasurements: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WeightViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(24.dp).testTag("screen_weight"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(text = "Peso", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            OutlinedTextField(
                value = uiState.weightInput,
                onValueChange = viewModel::onWeightChange,
                label = { Text("Peso (kg)") },
                modifier = Modifier.fillMaxWidth().testTag("input_weight_value"),
            )
        }
        item {
            OutlinedTextField(
                value = uiState.noteInput,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Observação (opcional)") },
                modifier = Modifier.fillMaxWidth().testTag("input_weight_note"),
            )
        }
        uiState.errorMessage?.let { error ->
            item {
                Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("text_error"))
            }
        }
        item {
            Button(
                onClick = viewModel::submit,
                enabled = !uiState.isSaving,
                modifier = Modifier.testTag("button_add_weight"),
            ) {
                Text("Registrar peso")
            }
        }
        item {
            OutlinedButton(onClick = onNavigateToMeasurements, modifier = Modifier.testTag("button_open_measurements")) {
                Text("Medidas corporais")
            }
        }
        item {
            TrendRow(label = "7 dias", trend = uiState.trend7, tag = "text_trend_7")
            TrendRow(label = "30 dias", trend = uiState.trend30, tag = "text_trend_30")
            TrendRow(label = "90 dias", trend = uiState.trend90, tag = "text_trend_90")
        }
        item {
            HorizontalDivider()
            Text(text = "Histórico", style = MaterialTheme.typography.titleMedium)
        }
        items(uiState.history, key = { it.id }) { entry ->
            WeightHistoryRow(entry = entry, onDelete = { viewModel.delete(entry.id) })
        }
    }
}

@Composable
private fun TrendRow(label: String, trend: WeightTrend?, tag: String) {
    val text = when {
        trend?.deltaKg == null -> "$label: sem dados suficientes"
        else -> "$label: %+.1f kg".format(trend.deltaKg)
    }
    Text(text = text, modifier = Modifier.testTag(tag))
}

@Composable
private fun WeightHistoryRow(entry: WeightEntry, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().testTag("weight_item_${entry.id}"),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = "${entry.weight} kg — ${entry.date}")
            entry.note?.let { Text(text = it, style = MaterialTheme.typography.bodySmall) }
        }
        IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_weight_${entry.id}")) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Excluir registro")
        }
    }
}
