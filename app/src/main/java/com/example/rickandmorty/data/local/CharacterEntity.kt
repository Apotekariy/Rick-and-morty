package com.example.rickandmorty.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    @Embedded(prefix = "origin_") val origin: Origin,
    @Embedded(prefix = "location_") val location: Location,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String,
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