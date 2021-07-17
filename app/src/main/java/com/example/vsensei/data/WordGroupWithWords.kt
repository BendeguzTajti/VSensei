package com.example.vsensei.data

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
data class WordGroupWithWords(
    @Embedded
    val wordGroup: WordGroup,
    @Relation(
        parentColumn = "groupId",
        entity = Word::class,
        entityColumn = "groupId"
    )
    val words: List<Word>
) : Parcelable