package com.example.vsensei.data

data class SharedWord(
    val wordPrimary: String,
    val wordPrimaryVariant: String?,
    val wordMeanings: List<String>
)