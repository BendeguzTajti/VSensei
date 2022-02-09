package com.example.vsensei.util

import com.example.vsensei.data.*
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

fun WordGroupWithWords.compress(gson: Gson): String {
    val sharedGroup = this.toSharedGroup()
    val json = gson.toJson(sharedGroup)
    val bos = ByteArrayOutputStream()
    val gzip = GZIPOutputStream(bos)
    gzip.apply {
        write(json.toByteArray(StandardCharsets.ISO_8859_1))
        close()
    }
    return bos.toString(StandardCharsets.ISO_8859_1.name())
}

fun ByteArray.decompressSharedGroup(gson: Gson): SharedGroup {
    val gis = GZIPInputStream(ByteArrayInputStream(this))
    val bf = BufferedReader(InputStreamReader(gis, StandardCharsets.ISO_8859_1))
    var outStr: String? = ""
    var line: String?
    while (bf.readLine().also { line = it } != null) {
        outStr += line
    }
    return gson.fromJson(outStr, SharedGroup::class.java)
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