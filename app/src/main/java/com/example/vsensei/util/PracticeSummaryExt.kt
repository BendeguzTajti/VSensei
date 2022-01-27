package com.example.vsensei.util

import com.example.vsensei.data.PracticeSummary
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry

fun List<PracticeSummary>.toEntries(): List<Entry> = this.mapIndexed { index, practiceSummary ->
    Entry(index.toFloat(), practiceSummary.getPercent().toFloat())
}

fun List<PracticeSummary>.floatOfCorrectGuesses(): Float = this.sumOf { it.correctGuesses }.toFloat()

fun List<PracticeSummary>.floatOfWrongGuesses(): Float = this.sumOf { it.wrongGuesses }.toFloat()

fun List<PracticeSummary>.toMinMaxPercentageBarEntries(): List<BarEntry> {
    val correctGuessesPercentage = this.sumOf { it.getPercent() } / this.size
    val wrongGuessesPercentage = 100 - correctGuessesPercentage
    return listOf(
        BarEntry(1f, correctGuessesPercentage.toFloat()),
        BarEntry(2f, wrongGuessesPercentage.toFloat())
    )
}