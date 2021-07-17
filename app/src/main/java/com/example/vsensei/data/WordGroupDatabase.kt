package com.example.vsensei.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
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

    companion object {
        @Volatile
        private var INSTANCE: WordGroupDatabase? = null

        fun getDatabase(context: Context): WordGroupDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WordGroupDatabase::class.java,
                    "word_group_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}