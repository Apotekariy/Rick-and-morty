package com.example.rickandmorty.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class CharacterResponse(
    val info: PageInfo,
    val results: List<CharacterDto>
)
@Serializable
data class PageInfo(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)