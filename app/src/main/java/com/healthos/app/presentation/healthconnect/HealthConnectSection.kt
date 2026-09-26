package com.healthos.app.presentation.healthconnect

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HealthConnectSection(
    modifier: Modifier = Modifier,
    viewModel: HealthConnectViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = PermissionController.createRequestPermissionResultContract(),
    ) { viewModel.onPermissionsResult() }

    Column(modifier = modifier.fillMaxWidth().testTag("section_health_connect"), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Health Connect", style = MaterialTheme.typography.titleMedium)

        when {
            uiState.isLoading -> Unit
            !uiState.isAvailable -> Text(
                text = "Health Connect não está disponível neste dispositivo",
                modifier = Modifier.testTag("text_health_connect_unavailable"),
            )
            !uiState.hasPermissions -> Button(
                onClick = { permissionLauncher.launch(uiState.requiredPermissions) },
                modifier = Modifier.testTag("button_connect_health_connect"),
            ) {
                Text("Conectar Health Connect")
            }
            else -> {
                Text(text = "Passos hoje: ${uiState.steps ?: 0}", modifier = Modifier.testTag("text_health_connect_steps"))
                uiState.sleepMinutes?.let {
                    Text(text = "Sono na última noite: ${it / 60}h ${it % 60}min", modifier = Modifier.testTag("text_health_connect_sleep"))
                }
                Text(
                    text = "Sessões de exercício (7 dias): ${uiState.exerciseSessionsThisWeek ?: 0}",
                    modifier = Modifier.testTag("text_health_connect_exercise"),
                )
            }
        }
    }
}
