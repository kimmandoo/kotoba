package com.kotoba.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object HomeGraph: Screen()
    @Serializable
    data object Profile: Screen()
    @Serializable
    data object Settings: Screen()
    @Serializable
    data object Learn: Screen()
}