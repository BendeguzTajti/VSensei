package com.example.vsensei.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vsensei.data.PracticeSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PracticeViewModel : ViewModel() {

    private val currentCardPosition: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            it.value = 0
        }
    }
    private val currentPracticeSummary: MutableLiveData<PracticeSummary> by lazy {
        MutableLiveData()
    }

    fun currentCardPosition(): LiveData<Int> = currentCardPosition

    fun setCurrentCardPosition(currentPosition: Int, replaceDelay: Long) {
        viewModelScope.launch {
            delay(replaceDelay)
            currentCardPosition.value = currentPosition
        }
    }

    fun currentPracticeSummary(): LiveData<PracticeSummary> = currentPracticeSummary

    fun setCurrentPracticeSummary(practiceSummary: PracticeSummary) {
        currentPracticeSummary.value = practiceSummary
    }

    fun savePracticeSummary(practiceSummary: PracticeSummary) {
        // TODO SAVE SUMMARY
    }
}