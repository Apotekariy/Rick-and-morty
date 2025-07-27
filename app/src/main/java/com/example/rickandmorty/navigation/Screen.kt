package com.example.rickandmorty.navigation

sealed class Screen(val route: String) {
    object CharactersList : Screen("characters_list")
    object CharacterDetails : Screen("character_details/{characterId}") {
        fun createRoute(characterId: Int) = "character_details/$characterId"
    }
}