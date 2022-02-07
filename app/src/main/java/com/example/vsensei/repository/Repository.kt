package com.example.vsensei.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import com.example.vsensei.data.*
import com.example.vsensei.util.Constants
import com.example.vsensei.util.compress
import com.google.gson.Gson

class Repository(
    private val sharedPreferences: SharedPreferences,
    private val wordGroupDao: WordGroupDao
) {

    private val gson = Gson()

    val allWordGroups = wordGroupDao.getAllWordGroups()
    val allPracticeSummaries = wordGroupDao.getAllPracticeSummaries()

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

    suspend fun updateWord(word: Word) {
        wordGroupDao.updateWord(word)
    }

    suspend fun addPracticeSummary(practiceSummary: PracticeSummary) {
        wordGroupDao.addPracticeSummary(practiceSummary)
    }

    suspend fun deleteWords(words: List<Word>) {
        wordGroupDao.deleteWords(words)
    }

    suspend fun deleteWord(word: Word) {
        wordGroupDao.deleteWord(word)
    }

    suspend fun deleteOldPracticeSummaries() {
        wordGroupDao.deleteOldPracticeSummaries()
    }

    fun wordsByGroupId(groupId: Long): LiveData<List<Word>> {
        return wordGroupDao.getWordsByGroupId(groupId)
    }

    fun saveSelectedLanguageIndex(selectedLanguageIndex: Int) {
        sharedPreferences.edit {
            putInt(Constants.SELECTED_LANGUAGE_KEY, selectedLanguageIndex)
        }
    }

    fun getLatestSelectedLanguageIndex(): Int {
        return sharedPreferences.getInt(Constants.SELECTED_LANGUAGE_KEY, 0)
    }

    fun saveUiMode(currentNightMode: Int?) {
        currentNightMode?.let {
            sharedPreferences.edit {
                putInt(Constants.CURRENT_NIGHT_MODE_KEY, it)
            }
        }
    }

    fun compressWordGroup(wordGroup: WordGroupWithWords): String = gson.toJson(wordGroup).compress()
}