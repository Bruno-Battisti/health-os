package com.healthos.app.presentation.evolution

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.healthos.app.R

@Composable
fun EvolutionScreen(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize().testTag("screen_evolution"), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.coming_soon),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}
