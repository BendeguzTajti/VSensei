package com.example.vsensei.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        WordGroup::class,
        Word::class,
        PracticeSummary::class
       ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WordGroupDatabase: RoomDatabase() {

    abstract fun wordGroupDao(): WordGroupDao
}