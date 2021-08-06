package com.example.vsensei.viewmodel

import androidx.lifecycle.ViewModel
import com.example.vsensei.repository.Repository

class UserOptionsViewModel(private val repository: Repository) : ViewModel() {

    fun saveUiMode(currentNightMode: Int?) = repository.saveUiMode(currentNightMode)
}