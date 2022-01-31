package com.example.vsensei.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vsensei.data.PracticeSummary
import com.example.vsensei.repository.Repository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class PracticeViewModel(private val repository: Repository) : ViewModel() {

    private val practiceSummary: MutableStateFlow<PracticeSummary> = MutableStateFlow(
        PracticeSummary()
    )
    private val _nextCardPosition = MutableSharedFlow<Int>()
    val nextCardPosition: SharedFlow<Int> = _nextCardPosition
    val allPracticeSummaries: LiveData<List<PracticeSummary>> by lazy {
        repository.allPracticeSummaries
    }
    private val _onWordGuess = MutableSharedFlow<Pair<Int, Boolean>>()
    val onWordGuess: SharedFlow<Pair<Int, Boolean>> = _onWordGuess

    fun onWordGuess(currentCardPosition: Int, guess: String, answer: String, answerVariant: String?) {
        viewModelScope.launch {
            val isCorrect = guess.lowercase() == answer.lowercase() || guess.lowercase() == answerVariant?.lowercase()
            _onWordGuess.emit(Pair(currentCardPosition, isCorrect))
            val delayInMillis = if (isCorrect) 1400L else 2000L
            delay(delayInMillis)
            if (isCorrect) {
                practiceSummary.value = practiceSummary.value.copy(
                    correctGuesses = practiceSummary.value.correctGuesses + 1
                )
            } else {
                practiceSummary.value = practiceSummary.value.copy(
                    wrongGuesses = practiceSummary.value.wrongGuesses + 1
                )
            }
            _nextCardPosition.emit(currentCardPosition + 1)
        }
    }

    fun onWordGuess(currentCardPosition: Int, guess: String, answers: List<String>) {
        viewModelScope.launch {
            val guesses = guess.split(",").map { it.trim().lowercase() }
            val matches = answers.filter { guesses.contains(it.lowercase()) }
            val isCorrect = matches.isNotEmpty()
            _onWordGuess.emit(Pair(currentCardPosition, isCorrect))
            val delayInMillis = if (isCorrect) 1400L else 2000L
            delay(delayInMillis)
            if (isCorrect) {
                practiceSummary.value = practiceSummary.value.copy(
                    correctGuesses = practiceSummary.value.correctGuesses + 1
                )
            } else {
                practiceSummary.value = practiceSummary.value.copy(
                    wrongGuesses = practiceSummary.value.wrongGuesses + 1
                )
            }
            _nextCardPosition.emit(currentCardPosition + 1)
        }
    }

    fun savePracticeSummary() {
        viewModelScope.launch {
            repository.addPracticeSummary(practiceSummary.value)
            repository.deleteOldPracticeSummaries()
        }
    }

    fun getPracticePercent(): Int = practiceSummary.value.getPercent()
}