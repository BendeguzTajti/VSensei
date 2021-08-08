package com.example.vsensei.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@Parcelize
@Entity
data class PracticeSummary(
    @PrimaryKey(autoGenerate = true)
    val practiceSummaryId: Long,
    val practiceType: PracticeType,
    val practicedGroupName: String,
    @Embedded
    val guesses: List<WordGuess>,
    val timeCreated: Long
) : Parcelable {

    fun getPercent(): Int {
        return ((guesses.count { it.isCorrectGuess } / guesses.size.toFloat()) * 100).roundToInt()
    }

//    fun addWordGuess(wordGuess: WordGuess) {
//        guesses.add
//    }
}