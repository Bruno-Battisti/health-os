package com.healthos.app.presentation.account

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    viewModel: AccountViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .testTag("screen_account"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(text = "Conta na nuvem", style = MaterialTheme.typography.headlineMedium)

        if (uiState.isAuthenticated) {
            Text(text = "Conectado", style = MaterialTheme.typography.titleMedium)
            Text(text = "Conta: ${uiState.accountId.orEmpty()}")
            Text(
                text = uiState.lastSyncedAt?.let { "Última sincronização: $it" } ?: "Ainda não sincronizado",
                modifier = Modifier.testTag("text_last_synced"),
            )

            uiState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("text_error"))
            }
            uiState.infoMessage?.let {
                Text(text = it, modifier = Modifier.testTag("text_info"))
            }

            Button(
                onClick = viewModel::syncNow,
                enabled = !uiState.isSyncing,
                modifier = Modifier.testTag("button_sync_now"),
            ) {
                Text(if (uiState.isSyncing) "Sincronizando..." else "Sincronizar agora")
            }

            OutlinedButton(
                onClick = viewModel::logout,
                modifier = Modifier.testTag("button_logout"),
            ) {
                Text("Sair")
            }
        } else {
            Text(
                text = if (uiState.isRegisterMode) "Criar conta" else "Entrar",
                style = MaterialTheme.typography.titleMedium,
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("E-mail") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().testTag("input_email"),
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().testTag("input_password"),
            )

            uiState.errorMessage?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.testTag("text_error"))
            }

            Button(
                onClick = viewModel::submit,
                enabled = !uiState.isSubmitting,
                modifier = Modifier.testTag("button_submit"),
            ) {
                Text(if (uiState.isRegisterMode) "Criar conta" else "Entrar")
            }

            TextButton(
                onClick = viewModel::onToggleMode,
                modifier = Modifier.testTag("button_toggle_mode"),
            ) {
                Text(if (uiState.isRegisterMode) "Já tenho conta" else "Criar uma conta")
            }
        }
    }
}
