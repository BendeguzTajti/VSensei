package com.example.vsensei.viewmodel

import androidx.lifecycle.*
import com.example.vsensei.data.Word
import com.example.vsensei.data.WordGroup
import com.example.vsensei.data.WordGroupWithWords
import com.example.vsensei.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WordViewModel(private val repository: Repository) : ViewModel() {

    val allWordGroups: LiveData<List<WordGroupWithWords>> = repository.allWordGroups
    val wordGroupsWithEnoughWords: LiveData<List<WordGroupWithWords>> =
        Transformations.map(allWordGroups) { wordGroups -> wordGroups.filter { it.words.size >= 5 } }
    private var wordsByGroupId: LiveData<List<Word>>? = null

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