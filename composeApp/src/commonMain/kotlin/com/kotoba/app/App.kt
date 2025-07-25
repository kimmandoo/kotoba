package com.kotoba.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.kotoba.navigation.AppNavGraph
import com.kotoba.shared.navigation.Screen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val startDestination = Screen.HomeGraph

    MaterialTheme {
        AppNavGraph(startDestination = startDestination)
    }
}
