package com.example.rickandmorty.navigation

import com.example.rickandmorty.presentation.CharactersScreen
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rickandmorty.presentation.CharacterDetailsScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.CharactersList.route
    ) {
        composable(Screen.CharactersList.route) {
            CharactersScreen(navController = navController)
        }

        composable(
            route = Screen.CharacterDetails.route,
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: -1
            CharacterDetailsScreen(
                characterId = characterId,
                navController = navController
            )
        }
    }
}