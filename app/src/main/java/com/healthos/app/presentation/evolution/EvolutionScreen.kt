package com.healthos.app.presentation.evolution

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.healthos.app.R
import com.healthos.app.domain.model.WeightEntry

@Composable
fun EvolutionScreen(
    modifier: Modifier = Modifier,
    viewModel: EvolutionViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp).testTag("screen_evolution"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (uiState.isLoading) return@Column

        if (uiState.history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.coming_soon))
            }
            return@Column
        }

        Text(text = "Evolução do peso", style = MaterialTheme.typography.headlineMedium)

        uiState.trend30?.deltaKg?.let { delta ->
            Text(text = "Últimos 30 dias: %+.1f kg".format(delta), modifier = Modifier.testTag("text_trend_30"))
        }

        WeightChart(
            history = uiState.history,
            modifier = Modifier.fillMaxWidth().height(200.dp).testTag("chart_weight"),
        )
    }
}

@Composable
private fun WeightChart(history: List<WeightEntry>, modifier: Modifier = Modifier) {
    val lineColor = MaterialTheme.colorScheme.primary
    val pointColor = MaterialTheme.colorScheme.secondary

    Canvas(modifier = modifier) {
        if (history.size < 2) {
            if (history.size == 1) {
                drawCircle(color = pointColor, radius = 6f, center = Offset(size.width / 2f, size.height / 2f))
            }
            return@Canvas
        }

        val minWeight = history.minOf { it.weight }
        val maxWeight = history.maxOf { it.weight }
        val weightRange = (maxWeight - minWeight).coerceAtLeast(1f)
        val stepX = size.width / (history.size - 1)

        val points = history.mapIndexed { index, entry ->
            val x = stepX * index
            val y = size.height - ((entry.weight - minWeight) / weightRange) * size.height
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = lineColor,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4f,
                cap = StrokeCap.Round,
            )
        }
        points.forEach { point ->
            drawCircle(color = pointColor, radius = 5f, center = point)
        }
    }
}
