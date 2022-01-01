package com.example.vsensei.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vsensei.data.PracticeSummary
import com.example.vsensei.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
    val currentCardPosition: LiveData<Int> = _currentCardPosition

    fun setCurrentCardPosition(currentPosition: Int, replaceDelay: Long) {
        viewModelScope.launch {
            delay(replaceDelay)
            _currentCardPosition.value = currentPosition
        }
    }

    fun setCurrentPracticeSummary(practiceSummary: PracticeSummary) {
        _currentPracticeSummary.value = practiceSummary
    }

    fun savePracticeSummary(practiceSummary: PracticeSummary) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPracticeSummary(practiceSummary)
            repository.deleteOldPracticeSummaries()
        }
    }
}