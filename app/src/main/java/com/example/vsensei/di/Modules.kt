package com.example.vsensei.di

import android.content.Context
import android.content.SharedPreferences
import android.speech.tts.TextToSpeech
import androidx.room.Room
import com.example.vsensei.data.WordGroupDao
import com.example.vsensei.data.WordGroupDatabase
import com.example.vsensei.repository.Repository
import com.example.vsensei.viewmodel.PracticeViewModel
import com.example.vsensei.viewmodel.UserOptionsViewModel
import com.example.vsensei.viewmodel.WordViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {

    // SHARED PREF
    single { provideSharedPreferences(get()) }

    // TEXT TO SPEECH
    single { provideTextToSpeech(get()) }

    // REPOSITORY
    single { Repository(get(), get()) }

    // VIEW-MODEL
    viewModel { WordViewModel(get()) }
    viewModel { PracticeViewModel() }
    viewModel { UserOptionsViewModel(get()) }

    // DataBase
    single { provideDataBase(get()) }

    // DAO
    single { provideWordGroupDao(get()) }
}

private fun provideSharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences("VSensei", Context.MODE_PRIVATE)

private fun provideTextToSpeech(context: Context): TextToSpeech? {
    var textToSpeech: TextToSpeech?
    textToSpeech = TextToSpeech(context) { status ->
        if (status != TextToSpeech.SUCCESS) {
            textToSpeech = null
        }
    }
    return textToSpeech
}

private fun provideDataBase(context: Context): WordGroupDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        WordGroupDatabase::class.java,
        "word_group_database"
    ).build()
}

fun provideWordGroupDao(database: WordGroupDatabase): WordGroupDao {
    return database.wordGroupDao()
}