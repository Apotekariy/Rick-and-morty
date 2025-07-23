package com.example.rickandmorty.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CharacterDao {
    @Upsert
    suspend fun upsertAll(beers: List<CharacterEntity>)

    @Query("SELECT * FROM beerentity")
    fun pagingSource(): PagingSource<Int, CharacterEntity>

    @Query("DELETE FROM beerentity")
    suspend fun clearAll()
}