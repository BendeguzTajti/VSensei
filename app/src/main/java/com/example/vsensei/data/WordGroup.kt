package com.example.vsensei.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "word_group_table")
data class WordGroup(
    @PrimaryKey(autoGenerate = true)
    val groupId: Long = 0,
    val groupName : String,
    val selectedLanguageIndex: Int,
    val localeLanguage: String,
    val timeCreated: Long
) : Parcelable