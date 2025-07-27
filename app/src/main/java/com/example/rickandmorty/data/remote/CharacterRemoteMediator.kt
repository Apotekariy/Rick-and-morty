package com.example.rickandmorty.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.rickandmorty.data.local.CharacterDatabase
import com.example.rickandmorty.data.local.CharacterEntity
import com.example.rickandmorty.data.mappers.toCharacterEntity
import com.example.rickandmorty.domain.CharacterFilterOptions
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val characterDb: CharacterDatabase,
    private val characterApi: ApiService,
    private val filterOptions: CharacterFilterOptions
) : RemoteMediator<Int, CharacterEntity>() {

    companion object {
        private const val TAG = "CharacterRemoteMediator"
        private const val STARTING_PAGE_INDEX = 1
    }

    // Храним состояние пагинации в памяти
    private var currentPage = STARTING_PAGE_INDEX
    private var isEndOfPagination = false

    override suspend fun initialize(): InitializeAction {
        Log.d(TAG, "initialize() called with filters: $filterOptions")

        return try {
            // При наличии фильтров всегда обновляем данные
            if (filterOptions.hasFilters()) {
                Log.d(TAG, "Has filters - forcing refresh")
                resetPaginationState()
                InitializeAction.LAUNCH_INITIAL_REFRESH
            } else {
                val hasOfflineData = characterDb.characterDao().getCharacterCount() > 0
                Log.d(TAG, "Has offline data: $hasOfflineData")

                if (hasOfflineData) {
                    Log.d(TAG, "Has offline data - skipping initial refresh")
                    InitializeAction.SKIP_INITIAL_REFRESH
                } else {
                    Log.d(TAG, "No offline data - launching initial refresh")
                    resetPaginationState()
                    InitializeAction.LAUNCH_INITIAL_REFRESH
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in initialize()", e)
            resetPaginationState()
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        Log.d(TAG, "load() called with loadType: $loadType")
        Log.d(TAG, "Current state - page: $currentPage, endOfPagination: $isEndOfPagination")

        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    Log.d(TAG, "REFRESH - resetting to page 1")
                    resetPaginationState()
                    STARTING_PAGE_INDEX
                }
                LoadType.PREPEND -> {
                    Log.d(TAG, "PREPEND - not supported, returning end of pagination")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    if (isEndOfPagination) {
                        Log.d(TAG, "APPEND - already at end of pagination")
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }

                    Log.d(TAG, "APPEND - loading page $currentPage")
                    currentPage
                }
            }

            // Применяем фильтры
            val filters = filterOptions.toQueryMap()
            Log.d(TAG, "Making API request - page: $page, filters: $filters")
            Log.d(TAG, "FilterOptions breakdown:")
            Log.d(TAG, "  - name: '${filterOptions.name}'")
            Log.d(TAG, "  - status: '${filterOptions.status?.apiValue}' (${filterOptions.status})")
            Log.d(TAG, "  - species: '${filterOptions.species}'")
            Log.d(TAG, "  - type: '${filterOptions.type}'")
            Log.d(TAG, "  - gender: '${filterOptions.gender?.apiValue}' (${filterOptions.gender})")

            val startTime = System.currentTimeMillis()
            val response = characterApi.getCharacters(
                page = page,
                filters = filters
            )
            val endTime = System.currentTimeMillis()

            Log.d(TAG, "API request completed in ${endTime - startTime}ms")
            Log.d(TAG, "Response: ${response.results.size} characters")
            Log.d(TAG, "Next URL: ${response.info.next}")
            Log.d(TAG, "Prev URL: ${response.info.prev}")

            // Обновляем состояние пагинации
            isEndOfPagination = response.info.next == null
            if (!isEndOfPagination && loadType == LoadType.APPEND) {
                currentPage++
            }

            Log.d(TAG, "Updated state - nextPage: $currentPage, endOfPagination: $isEndOfPagination")

            characterDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Log.d(TAG, "REFRESH - clearing database")
                    val deletedCount = characterDb.characterDao().getCharacterCount()
                    characterDb.characterDao().clearAll()
                    Log.d(TAG, "Cleared $deletedCount characters from database")
                }

                val characterEntities = response.results.map { dto ->
                    dto.toCharacterEntity()
                }

                Log.d(TAG, "Inserting ${characterEntities.size} characters to database")
                characterDb.characterDao().upsertAll(characterEntities)

                val totalInDb = characterDb.characterDao().getCharacterCount()
                Log.d(TAG, "Total characters in database: $totalInDb")
            }

            Log.d(TAG, "Returning success - endOfPagination: $isEndOfPagination")
            MediatorResult.Success(endOfPaginationReached = isEndOfPagination)

        } catch (e: HttpException) {
            Log.e(TAG, "HTTP Exception - code: ${e.code()}, message: ${e.message()}", e)
            when (e.code()) {
                404 -> {
                    Log.w(TAG, "404 Not Found - treating as end of pagination")
                    isEndOfPagination = true
                    MediatorResult.Success(endOfPaginationReached = true)
                }
                else -> {
                    Log.e(TAG, "HTTP error ${e.code()}: ${e.message()}")
                    MediatorResult.Error(e)
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Network IOException", e)
            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected exception in load()", e)
            MediatorResult.Error(e)
        }
    }

    private fun resetPaginationState() {
        currentPage = STARTING_PAGE_INDEX
        isEndOfPagination = false
        Log.d(TAG, "Pagination state reset - page: $currentPage, endOfPagination: ${false}")
    }
}