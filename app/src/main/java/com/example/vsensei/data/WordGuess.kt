package com.example.vsensei.data

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class WordGuess(
    val hint: String,
    val hintVariant: String? = null,
    val answer: String,
    val isCorrectGuess: Boolean
) : Parcelable