package com.example.vsensei.di

import android.content.Context
import android.speech.tts.TextToSpeech
import org.koin.dsl.module

val appModules = module {

    // TEXT TO SPEECH
    single { provideTextToSpeech(get()) }
}

private fun provideTextToSpeech(context: Context): TextToSpeech? {
    var textToSpeech: TextToSpeech?
    textToSpeech = TextToSpeech(context) { status ->
        if (status != TextToSpeech.SUCCESS) {
            textToSpeech = null
        }
    }
    return textToSpeech
}