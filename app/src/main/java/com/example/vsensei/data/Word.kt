package com.example.vsensei.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "word_table")
data class Word(
    @PrimaryKey(autoGenerate = true)
    val wordId: Long = 0,
    val groupId: Long,
    val wordPrimary: String,
    val wordPrimaryVariant: String?,
    val wordMeanings: List<String>
) : Parcelable
