package com.example.vsensei.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WordGroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWordGroup(wordGroup: WordGroup)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWords(words: List<Word>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addWord(word: Word)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPracticeSummary(practiceSummary: PracticeSummary)

    @Query("SELECT * FROM word_group_table ORDER BY timeCreated DESC")
    fun getAllWordGroups(): LiveData<List<WordGroupWithWords>>

    @Query("SELECT * FROM word_table WHERE groupId = :groupId ORDER BY wordPrimary")
    fun getWordsByGroupId(groupId: Long): LiveData<List<Word>>

    @Query("SELECT * FROM practice_summary_table ORDER BY timeCreated DESC")
    fun getAllPracticeSummaries(): LiveData<List<PracticeSummary>>

    @Delete
    suspend fun deleteWordGroup(wordGroup: WordGroup)

    @Delete
    suspend fun deleteWords(words: List<Word>)

    @Delete
    suspend fun deleteWord(word: Word)
}