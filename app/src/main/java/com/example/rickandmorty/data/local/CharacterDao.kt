package com.example.rickandmorty.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface CharacterDao {
    @Upsert
    suspend fun upsertAll(characters: List<CharacterEntity>)

    @Query("SELECT * FROM characters")
    fun pagingSource(): PagingSource<Int, CharacterEntity>

    @Query("""
        SELECT * FROM characters 
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:status IS NULL OR status = :status)
        AND (:species IS NULL OR species LIKE '%' || :species || '%')
        AND (:type IS NULL OR (type = :type OR (type = '' AND :type = '')))
        AND (:gender IS NULL OR gender = :gender)
        ORDER BY id ASC
    """)
    fun pagingSourceWithFilters(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        type: String? = null,
        gender: String? = null
    ): PagingSource<Int, CharacterEntity>

    @Query("DELETE FROM characters")
    suspend fun clearAll()

    @Query("DELETE FROM characters WHERE id NOT IN (:keepIds)")
    suspend fun clearAllExcept(keepIds: List<Int>)

    @Query("SELECT * FROM characters WHERE id = :characterId")
    suspend fun getCharacterById(characterId: Int): CharacterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(character: CharacterEntity)

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharacterCount(): Int

    @Query("""
        SELECT COUNT(*) FROM characters 
        WHERE (:name IS NULL OR name LIKE '%' || :name || '%')
        AND (:status IS NULL OR status = :status)
        AND (:species IS NULL OR species LIKE '%' || :species || '%')
        AND (:type IS NULL OR (type = :type OR (type = '' AND :type = '')))
        AND (:gender IS NULL OR gender = :gender)
    """)
    suspend fun getFilteredCharacterCount(
        name: String? = null,
        status: String? = null,
        species: String? = null,
        type: String? = null,
        gender: String? = null
    ): Int
}