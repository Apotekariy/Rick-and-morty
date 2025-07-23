package com.example.rickandmorty.data.remote

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import coil3.network.HttpException
import com.example.rickandmorty.data.local.CharacterDatabase
import com.example.rickandmorty.data.local.CharacterEntity
import com.example.rickandmorty.data.mappers.toCharacterEntity
import okio.IOException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val characterDb : CharacterDatabase,
    private val characterApi : ApiService
): RemoteMediator<Int, CharacterEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>,
    ): MediatorResult {
        return try {
            val loadKey = when(loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(
                    endOfPaginationReached = true
                )
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    if (lastItem == null) {
                        1
                    } else {
                        (lastItem.id / state.config.pageSize) + 1
                    }
                }
            }

            val characters = characterApi.getCharacters(
                page = loadKey,
                pageCount = state.config.pageSize
            )

            characterDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    characterDb.dao.clearAll()
                }
                val characterEntities = characters.map{ it.toCharacterEntity() }
                characterDb.dao.upsertAll(characterEntities)
            }

            MediatorResult.Success(
                endOfPaginationReached = characters.isEmpty()
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}