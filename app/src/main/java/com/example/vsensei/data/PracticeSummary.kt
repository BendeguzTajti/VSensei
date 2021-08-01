package com.example.vsensei.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PracticeSummary(
    val practicedGroupName: String,
    val correctGuesses: MutableList<Word>,
    val wrongGuesses: MutableList<Word>,
    val timeCreated: Long
) : Parcelable