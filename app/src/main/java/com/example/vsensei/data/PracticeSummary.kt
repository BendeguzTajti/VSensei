package com.example.vsensei.data

import android.os.Parcelable
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
    val correctGuesses: MutableList<Word>,
    val wrongGuesses: MutableList<Word>,
    val timeCreated: Long
) : Parcelable {

    fun getPercent(): Int {
        val allWords = (correctGuesses.size + wrongGuesses.size).toFloat()
        return ((correctGuesses.size / allWords) * 100).roundToInt()
    }
}