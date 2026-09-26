package com.healthos.app.presentation.weight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.healthos.app.domain.model.Measurement
import com.healthos.app.domain.model.MeasurementType

@Composable
fun MeasurementScreen(
    modifier: Modifier = Modifier,
    viewModel: MeasurementViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(24.dp).testTag("screen_measurements"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(text = "Medidas corporais", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MeasurementType.entries.forEach { type ->
                    FilterChip(
                        selected = uiState.selectedType == type,
                        onClick = { viewModel.onTypeChange(type) },
                        label = { Text(type.displayLabel()) },
                        modifier = Modifier.testTag("measurement_type_${type.name}"),
                    )
                }
            }
        }
        item {
            OutlinedTextField(
                value = uiState.valueInput,
                onValueChange = viewModel::onValueChange,
                label = { Text("Valor (cm)") },
                modifier = Modifier.fillMaxWidth().testTag("input_measurement_value"),
            )
        }
        uiState.errorMessage?.let { error ->
            item {
                Text(text = error, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("text_error"))
            }
        }
        item {
            Button(onClick = viewModel::submit, modifier = Modifier.testTag("button_add_measurement")) {
                Text("Registrar medida")
            }
        }
        item {
            HorizontalDivider()
            Text(text = "Histórico", style = MaterialTheme.typography.titleMedium)
        }
        items(uiState.history, key = { it.id }) { measurement ->
            MeasurementHistoryRow(measurement = measurement, onDelete = { viewModel.delete(measurement.id) })
        }
    }
}

@Composable
private fun MeasurementHistoryRow(measurement: Measurement, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().testTag("measurement_item_${measurement.id}"),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = "${measurement.value} cm — ${measurement.date}")
        }
        IconButton(onClick = onDelete, modifier = Modifier.testTag("delete_measurement_${measurement.id}")) {
            Icon(imageVector = Icons.Filled.Delete, contentDescription = "Excluir medida")
        }
    }
}
