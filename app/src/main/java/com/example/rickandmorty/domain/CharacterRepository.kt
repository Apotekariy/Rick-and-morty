package com.example.rickandmorty.domain

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.rickandmorty.data.local.CharacterDatabase
import com.example.rickandmorty.data.mappers.toCharacter
import com.example.rickandmorty.data.mappers.toCharacterEntity
import com.example.rickandmorty.data.remote.ApiService
import com.example.rickandmorty.data.remote.CharacterRemoteMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CharacterRepository {
    suspend fun getCharacterById(characterId: Int): CharacterFromShow
    fun getCharacters(filterOptions: CharacterFilterOptions): Flow<PagingData<CharacterFromShow>>
    suspend fun hasOfflineData(): Boolean
}

@OptIn(ExperimentalPagingApi::class)
class CharacterRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val database: CharacterDatabase,
    private val pagingConfig: PagingConfig
) : CharacterRepository {

    companion object {
        private const val TAG = "CharacterRepository"
    }

    override suspend fun getCharacterById(characterId: Int): CharacterFromShow {
        Log.d(TAG, "getCharacterById called with id: $characterId")

        try {
            // Сначала пробуем получить из базы данных
            val local = database.characterDao().getCharacterById(characterId)?.toCharacter()
            if (local != null) {
                Log.d(TAG, "Character found in database: ${local.name}")
                return local
            }

            Log.d(TAG, "Character not found in database, fetching from API")
            // Если нет в базе, загружаем с API
            val remote = apiService.getCharacterById(characterId).toCharacter()
            Log.d(TAG, "Character fetched from API: ${remote.name}")

            // Сохраняем в базу для будущего использования
            database.characterDao().insert(remote.toCharacterEntity())
            Log.d(TAG, "Character saved to database")

            return remote
        } catch (e: Exception) {
            Log.e(TAG, "Error getting character by id $characterId", e)
            throw e
        }
    }

    override fun getCharacters(filterOptions: CharacterFilterOptions): Flow<PagingData<CharacterFromShow>> {
        Log.d(TAG, "getCharacters called with filters: $filterOptions")
        Log.d(TAG, "PagingConfig: pageSize=${pagingConfig.pageSize}, prefetchDistance=${pagingConfig.prefetchDistance}")

        try {
            val remoteMediator = CharacterRemoteMediator(
                characterDb = database,
                characterApi = apiService,
                filterOptions = filterOptions
            )
            Log.d(TAG, "RemoteMediator created successfully")

            val pagingSourceFactory = {
                if (filterOptions.hasFilters()) {
                    Log.d(TAG, "Using filtered paging source")
                    Log.d(TAG, "Database filter values:")
                    Log.d(TAG, "  - name: '${filterOptions.name?.takeIf { it.isNotBlank() }}'")
                    Log.d(TAG, "  - status: '${filterOptions.status?.apiValue}'")
                    Log.d(TAG, "  - species: '${filterOptions.species?.takeIf { it.isNotBlank() }}'")
                    Log.d(TAG, "  - type: '${filterOptions.type?.takeIf { it.isNotBlank() }}'")
                    Log.d(TAG, "  - gender: '${filterOptions.gender?.apiValue}'")

                    database.characterDao().pagingSourceWithFilters(
                        name = filterOptions.name?.takeIf { it.isNotBlank() },
                        status = filterOptions.status?.apiValue,
                        species = filterOptions.species?.takeIf { it.isNotBlank() },
                        type = filterOptions.type?.takeIf { it.isNotBlank() },
                        gender = filterOptions.gender?.apiValue
                    )
                } else {
                    Log.d(TAG, "Using unfiltered paging source")
                    database.characterDao().pagingSource()
                }
            }

            val pager = Pager(
                config = pagingConfig,
                remoteMediator = remoteMediator,
                pagingSourceFactory = pagingSourceFactory
            )
            Log.d(TAG, "Pager created successfully")

            return pager.flow.map { pagingData ->
                Log.v(TAG, "Mapping PagingData to domain models")
                pagingData.map { entity ->
                    entity.toCharacter()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating pager", e)
            throw e
        }
    }

    override suspend fun hasOfflineData(): Boolean {
        return try {
            val count = database.characterDao().getCharacterCount()
            Log.d(TAG, "Offline data count: $count")
            count > 0
        } catch (e: Exception) {
            Log.e(TAG, "Error checking offline data", e)
            false
        }
    }
}