package com.example.vsensei.data

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {

    @TypeConverter
    fun fromWordGuess(value: MutableList<WordGuess>): String = Gson().toJson(value)

    @TypeConverter
    fun toWordGuess(value: String) = Gson().fromJson(value, Array<WordGuess>::class.java).toList()
}