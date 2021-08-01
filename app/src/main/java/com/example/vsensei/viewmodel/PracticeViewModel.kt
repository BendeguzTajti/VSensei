package com.example.vsensei.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PracticeViewModel : ViewModel() {

    private val currentCardPosition: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also {
            it.value = 0
        }
    }

    fun currentCardPosition(): LiveData<Int> = currentCardPosition

    fun setCurrentCardPosition(currentPosition: Int, replaceDelay: Long) {
        viewModelScope.launch {
            delay(replaceDelay)
            currentCardPosition.value = currentPosition
        }
    }
}