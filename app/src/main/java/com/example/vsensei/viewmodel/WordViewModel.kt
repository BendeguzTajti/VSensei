package com.example.vsensei.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.vsensei.data.Word
import com.example.vsensei.data.WordGroup
import com.example.vsensei.data.WordGroupDatabase
import com.example.vsensei.data.WordGroupWithWords
import com.example.vsensei.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository
    val allWordGroups: LiveData<List<WordGroupWithWords>>
    val wordGroupsWithWords: LiveData<List<WordGroupWithWords>>
    private var wordsByGroupId: LiveData<List<Word>>? = null

    init {
        val sharedPreferences = application.getSharedPreferences("VSensei", Context.MODE_PRIVATE)
        val wordGroupDao = WordGroupDatabase.getDatabase(application).wordGroupDao()
        repository = Repository(sharedPreferences, wordGroupDao)
        allWordGroups = repository.allWordGroups
        wordGroupsWithWords = Transformations.map(allWordGroups) { wordGroups -> wordGroups.filter { it.words.isNotEmpty() } }
    }

    fun addWordGroup(wordGroup: WordGroup) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWordGroup(wordGroup)
        }
    }

    fun deleteWordGroup(wordGroupWithWords: WordGroupWithWords) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWordGroup(wordGroupWithWords.wordGroup)
            repository.deleteWords(wordGroupWithWords.words)
        }
    }

    fun restoreWordGroup(wordGroupWithWords: WordGroupWithWords) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWords(wordGroupWithWords.words)
            repository.addWordGroup(wordGroupWithWords.wordGroup)
        }
    }

    fun addWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addWord(word)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWord(word)
        }
    }

    fun wordsByGroupId(groupId: Long) : LiveData<List<Word>> {
        if (wordsByGroupId == null) {
            wordsByGroupId = repository.wordsByGroupId(groupId)
        }
        return wordsByGroupId!!
    }

    fun getLatestSelectedLanguageIndex(): Int = repository.getLatestSelectedLanguageIndex()

    fun saveSelectedLanguageIndex(selectedLanguageIndex: Int) = repository.saveSelectedLanguageIndex(selectedLanguageIndex)
}