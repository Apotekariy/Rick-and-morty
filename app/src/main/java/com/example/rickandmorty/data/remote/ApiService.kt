package com.example.rickandmorty.data.remote

import com.example.rickandmorty.data.remote.models.CharacterDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("characters")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("per_page") pageCount: Int
    ): List<CharacterDto>
}