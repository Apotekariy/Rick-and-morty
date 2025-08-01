package com.example.rickandmorty.di

import androidx.paging.PagingConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PagingModule {

    @Provides
    @Singleton
    fun providePagingConfig(): PagingConfig {
        return PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            prefetchDistance = 5,
            initialLoadSize = 20
        )
    }
}