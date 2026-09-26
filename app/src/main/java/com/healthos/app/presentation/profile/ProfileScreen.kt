package com.healthos.app.presentation.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(24.dp).testTag("screen_profile"),
        contentAlignment = Alignment.TopStart,
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}
