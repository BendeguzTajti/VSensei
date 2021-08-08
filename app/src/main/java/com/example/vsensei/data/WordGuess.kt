package com.example.vsensei.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WordGuess(
    val answer: String,
    val answerVariant: String?,
    val guess: String,
    val isCorrectGuess: Boolean
) : Parcelable