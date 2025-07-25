package com.kotoba.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotoba.home.HomeGraphScreen
import com.kotoba.learn.LearnScreen
import com.kotoba.shared.navigation.Screen

@Composable
fun AppNavGraph(startDestination: Screen = Screen.HomeGraph) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable<Screen.HomeGraph> {
            HomeGraphScreen(
                navigateToProfile = {
//                    navController.navigate(Screen.Profile)
                },
                navigateToSettings = {
//                    navController.navigate(Screen.Settings)
                },
                navigateToLearn = {
                    navController.navigate(Screen.Learn)
                },
            )
        }

        composable<Screen.Learn> {
            LearnScreen()
        }
    }
}
