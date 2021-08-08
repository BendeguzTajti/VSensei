package com.example.vsensei.data

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class WordGuess(
    val answer: String,
    val answerVariant: String?,
    val guess: String,
    val isCorrectGuess: Boolean
) : Parcelable