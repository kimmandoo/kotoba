package com.kotoba.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeGraphScreen(
    navigateToProfile: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToLearn: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Text(text = "hello")
    }
}
