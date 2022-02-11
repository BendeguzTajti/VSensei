package com.example.vsensei.util

import com.example.vsensei.data.*
import com.example.vsensei.data.SharedGroup
import com.example.vsensei.data.SharedWord
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun WordGroupWithWords.compress(gson: Gson): String {
    val sharedGroup = this.toSharedGroup()
    val json = gson.toJson(sharedGroup)
    val bos = ByteArrayOutputStream()
    GZIPOutputStream(bos).bufferedWriter(StandardCharsets.UTF_8).use { it.write(json) }
    return bos.toByteArray().toString(StandardCharsets.ISO_8859_1)
}

fun ByteArray.decompressSharedGroup(gson: Gson): SharedGroup {
    val json = GZIPInputStream(this.inputStream()).bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
    return gson.fromJson(json, SharedGroup::class.java)
}

private fun WordGroupWithWords.toSharedGroup(): SharedGroup {
    return SharedGroup(
        groupName = this.wordGroup.groupName,
        selectedLanguageIndex = this.wordGroup.selectedLanguageIndex,
        localeLanguage = this.wordGroup.localeLanguage,
        sharedWords = this.words.toSharedWords()
    )
}

private fun List<Word>.toSharedWords(): List<SharedWord> {
    return this.map {
        SharedWord(
            wordPrimary = it.wordPrimary,
            wordPrimaryVariant = it.wordPrimaryVariant,
            wordMeanings = it.wordMeanings
        )
    }
}

fun SharedGroup.toWordGroup(): WordGroup {
    return WordGroup(
        groupName = this.groupName,
        selectedLanguageIndex = this.selectedLanguageIndex,
        localeLanguage = this.localeLanguage,
        timeCreated = System.currentTimeMillis()
    )
}

fun List<SharedWord>.toWords(groupId: Long): List<Word> {
    return this.map {
        Word(
            groupId = groupId,
            wordPrimary = it.wordPrimary,
            wordPrimaryVariant = it.wordPrimaryVariant,
            wordMeanings = it.wordMeanings
        )
    }
}