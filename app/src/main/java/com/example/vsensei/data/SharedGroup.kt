package com.example.vsensei.data

data class SharedGroup(
    val groupName: String,
    val selectedLanguageIndex: Int,
    val localeLanguage: String,
    val sharedWords: List<SharedWord>
)