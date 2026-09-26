package com.healthos.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.healthos.app.navigation.HealthOsNavGraph
import com.healthos.app.presentation.theme.HealthOsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthOsTheme {
                HealthOsNavGraph()
            }
        }
    }
}
