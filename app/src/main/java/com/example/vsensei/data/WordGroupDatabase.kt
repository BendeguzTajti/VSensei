package com.example.vsensei.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        WordGroup::class,
        Word::class
       ],
    version = 1,
    exportSchema = false
)
abstract class WordGroupDatabase: RoomDatabase() {

    abstract fun wordGroupDao(): WordGroupDao
}