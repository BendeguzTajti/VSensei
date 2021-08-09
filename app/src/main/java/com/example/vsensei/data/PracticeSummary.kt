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
    val practiceSummaryId: Long,
    val practiceType: PracticeType,
    val practicedGroupName: String,
    val wordGuesses: MutableList<WordGuess>,
    val hasVariants: Boolean,
    val timeCreated: Long
) : Parcelable {

    fun getPercent(): Int {
        return ((wordGuesses.count { it.isCorrectGuess } / wordGuesses.size.toFloat()) * 100).roundToInt()
    }
}