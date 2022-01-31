package com.example.vsensei.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vsensei.data.PracticeSummary
import com.example.vsensei.repository.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class PracticeViewModel(private val repository: Repository) : ViewModel() {

    private val _currentCardPosition: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            it.value = 0
        }
    }
    private val _currentPracticeSummary: MutableLiveData<PracticeSummary> by lazy {
        MutableLiveData()
    }
    val allPracticeSummaries: LiveData<List<PracticeSummary>> by lazy {
        repository.allPracticeSummaries
    }
    private val _onWordGuess = MutableSharedFlow<Boolean>()
    val onWordGuess: SharedFlow<Boolean> = _onWordGuess
    val currentCardPosition: LiveData<Int> = _currentCardPosition

    fun onWordGuess(currentCardPosition: Int, isCorrect: Boolean) {
        viewModelScope.launch {
            _onWordGuess.emit(isCorrect)
            val delayInMillis = if (isCorrect) 1400L else 2000L
            delay(delayInMillis)
            _currentCardPosition.value = currentCardPosition + 1
        }
    }

    fun setCurrentPracticeSummary(practiceSummary: PracticeSummary) {
        _currentPracticeSummary.value = practiceSummary
    }

    fun savePracticeSummary(practiceSummary: PracticeSummary) {
        viewModelScope.launch {
            repository.addPracticeSummary(practiceSummary)
            repository.deleteOldPracticeSummaries()
        }
    }
}