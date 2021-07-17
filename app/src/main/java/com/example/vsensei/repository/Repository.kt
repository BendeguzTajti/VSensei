package com.example.vsensei.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import com.example.vsensei.data.Word
import com.example.vsensei.data.WordGroup
import com.example.vsensei.data.WordGroupDao

class Repository(private val sharedPreferences: SharedPreferences, private val wordGroupDao: WordGroupDao) {

    val allWordGroups = wordGroupDao.getAllWordGroups()

    suspend fun addWordGroup(wordGroup: WordGroup) {
        wordGroupDao.addWordGroup(wordGroup)
    }

    suspend fun deleteWordGroup(wordGroup: WordGroup) {
        wordGroupDao.deleteWordGroup(wordGroup)
    }

    suspend fun addWords(words: List<Word>) {
        wordGroupDao.addWords(words)
    }

    suspend fun addWord(word: Word) {
        wordGroupDao.addWord(word)
    }

    suspend fun deleteWords(words: List<Word>) {
        wordGroupDao.deleteWords(words)
    }

    suspend fun deleteWord(word: Word) {
        wordGroupDao.deleteWord(word)
    }

    fun wordsByGroupId(groupId: Long) : LiveData<List<Word>> {
        return wordGroupDao.getWordsByGroupId(groupId)
    }

    fun saveSelectedLanguageIndex(selectedLanguageIndex: Int) {
        sharedPreferences.edit {
            putInt("SELECTED_LANGUAGE", selectedLanguageIndex)
        }
    }

    fun getLatestSelectedLanguageIndex(): Int {
        return sharedPreferences.getInt("SELECTED_LANGUAGE", 0)
    }
}