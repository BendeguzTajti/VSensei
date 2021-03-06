package com.example.vsensei.viewmodel

import androidx.lifecycle.*
import com.example.vsensei.data.Word
import com.example.vsensei.data.WordGroup
import com.example.vsensei.data.WordGroupWithWords
import com.example.vsensei.repository.Repository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class WordViewModel(private val repository: Repository) : ViewModel() {

    val allWordGroups: LiveData<List<WordGroupWithWords>> = repository.allWordGroups
    val wordGroupsWithEnoughWords: LiveData<List<WordGroupWithWords>> =
        Transformations.map(allWordGroups) { wordGroups -> wordGroups.filter { it.words.size >= 5 } }

    private val _recentlyDeletedGroup = MutableSharedFlow<WordGroupWithWords>()
    val recentlyDeletedGroup: SharedFlow<WordGroupWithWords> = _recentlyDeletedGroup
    private val _recentlyDeletedWord = MutableSharedFlow<Word>()
    val recentlyDeletedWord: SharedFlow<Word> = _recentlyDeletedWord

    fun addWordGroup(wordGroup: WordGroup) {
        viewModelScope.launch {
            repository.addWordGroup(wordGroup)
        }
    }

    fun deleteWordGroup(wordGroupWithWords: WordGroupWithWords) {
        viewModelScope.launch {
            repository.deleteWordGroup(wordGroupWithWords.wordGroup)
            repository.deleteWords(wordGroupWithWords.words)
            _recentlyDeletedGroup.emit(wordGroupWithWords)
        }
    }

    fun restoreWordGroup(wordGroupWithWords: WordGroupWithWords) {
        viewModelScope.launch {
            repository.addWords(wordGroupWithWords.words)
            repository.addWordGroup(wordGroupWithWords.wordGroup)
        }
    }

    fun addWord(word: Word) {
        viewModelScope.launch {
            repository.addWord(word)
        }
    }

    fun updateWord(word: Word) {
        viewModelScope.launch {
            repository.updateWord(word)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch {
            repository.deleteWord(word)
            _recentlyDeletedWord.emit(word)
        }
    }

    fun wordsByGroupId(groupId: Long) : LiveData<List<Word>> {
        return repository.wordsByGroupId(groupId)
    }

    fun getLatestSelectedLanguageIndex(): Int = repository.getLatestSelectedLanguageIndex()

    fun saveSelectedLanguageIndex(selectedLanguageIndex: Int) = repository.saveSelectedLanguageIndex(selectedLanguageIndex)
}