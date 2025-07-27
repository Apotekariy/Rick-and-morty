package com.example.rickandmorty.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int? = null,
        @QueryMap filters: Map<String, String> = emptyMap()
    ): CharacterResponse

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto
}