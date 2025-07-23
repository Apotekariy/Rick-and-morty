package com.example.rickandmorty.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CharacterEntity::class],
    version = 1
)
abstract class CharacterDatabase: RoomDatabase() {
    abstract val dao: CharacterDao
}