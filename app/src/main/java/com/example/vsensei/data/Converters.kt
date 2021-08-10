package com.example.vsensei.data

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromWordGuess(value: MutableList<WordGuess>): String = Gson().toJson(value)

    @TypeConverter
    fun toWordGuess(value: String) = Gson().fromJson(value, Array<WordGuess>::class.java).toList()

    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").map { it }
    }

    @TypeConverter
    fun toString(value: List<String>): String {
        return value.joinToString(separator = ",")
    }
}