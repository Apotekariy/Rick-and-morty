package com.example.rickandmorty.di

import android.content.Context
import androidx.room.Room
import com.example.rickandmorty.data.local.CharacterDao
import com.example.rickandmorty.data.local.CharacterDatabase
import com.example.rickandmorty.data.local.RemoteKeysDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCharacterDatabase(@ApplicationContext context: Context): CharacterDatabase {
        return Room.databaseBuilder(
            context,
            CharacterDatabase::class.java,
            "characters.db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideCharacterDao(database: CharacterDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    fun provideRemoteKeysDao(database: CharacterDatabase): RemoteKeysDao {
        return database.remoteKeysDao()
    }
}