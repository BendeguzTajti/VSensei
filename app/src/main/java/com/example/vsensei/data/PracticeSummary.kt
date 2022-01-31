package com.example.vsensei.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

@Parcelize
@Entity(tableName = "practice_summary_table")
data class PracticeSummary(
    @PrimaryKey(autoGenerate = true)
    val practiceSummaryId: Long = 0,
    val correctGuesses: Int = 0,
    val wrongGuesses: Int = 0
) : Parcelable {

    fun getPercent(): Int {
        val allGuesses = correctGuesses + wrongGuesses
        return ((correctGuesses.toFloat() / allGuesses.toFloat()) * 100).roundToInt()
    }
}