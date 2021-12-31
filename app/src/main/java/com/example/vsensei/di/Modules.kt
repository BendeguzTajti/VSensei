package com.example.vsensei.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.vsensei.data.WordGroupDao
import com.example.vsensei.data.WordGroupDatabase
import com.example.vsensei.repository.Repository
import com.example.vsensei.util.Constants
import com.example.vsensei.viewmodel.PracticeViewModel
import com.example.vsensei.viewmodel.UserOptionsViewModel
import com.example.vsensei.viewmodel.WordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {

    // SHARED PREF
    single { provideSharedPreferences(get()) }

    // REPOSITORY
    single { Repository(get(), get()) }

    // VIEW-MODEL
    viewModel { WordViewModel(get()) }
    viewModel { PracticeViewModel(get()) }
    viewModel { UserOptionsViewModel(get()) }

    // DataBase
    single { provideDataBase(get()) }

    // DAO
    single { provideWordGroupDao(get()) }
}

private fun provideSharedPreferences(context: Context): SharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

private fun provideDataBase(context: Context): WordGroupDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        WordGroupDatabase::class.java,
        Constants.DATABASE_NAME
    ).build()
}

fun provideWordGroupDao(database: WordGroupDatabase): WordGroupDao {
    return database.wordGroupDao()
}