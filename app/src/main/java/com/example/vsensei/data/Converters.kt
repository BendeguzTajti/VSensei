package com.example.vsensei.data

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromString(value: String): List<String> {
        return value.split(",").map { it }
    }

    @TypeConverter
    fun toString(value: List<String>): String {
        return value.joinToString(separator = ",")
    }
}