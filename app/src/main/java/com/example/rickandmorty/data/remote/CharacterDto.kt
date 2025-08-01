package com.example.rickandmorty.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: Origin,
    val location: Location,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String,
) {
    @Serializable
    data class Location(
        val name: String,
        val url: String
    )
    @Serializable
    data class Origin(
        val name: String,
        val url: String
    )
}