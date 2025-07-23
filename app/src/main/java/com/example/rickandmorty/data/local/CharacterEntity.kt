package com.example.rickandmorty.data.local

import androidx.room.PrimaryKey
import com.example.rickandmorty.domain.models.CharacterGender
import com.example.rickandmorty.domain.models.CharacterStatus

data class CharacterEntity(
    val created: String,
    val episodeIds: List<Int>,
    val gender: CharacterGender,
    @PrimaryKey
    val id: Int,
    val imageUrl: String,
    val location: Location,
    val name: String,
    val origin: Origin,
    val species: String,
    val status: CharacterStatus,
    val type: String
) {
    data class Location(
        val name: String,
        val url: String
    )

    data class Origin(
        val name: String,
        val url: String
    )
}
