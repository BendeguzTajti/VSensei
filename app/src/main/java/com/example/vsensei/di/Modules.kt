package com.example.vsensei.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.vsensei.data.WordGroupDao
import com.example.vsensei.data.WordGroupDatabase
import com.example.vsensei.repository.Repository
import com.example.vsensei.util.Constants
import com.example.vsensei.viewmodel.GroupShareViewModel
import com.example.vsensei.viewmodel.PracticeViewModel
import com.example.vsensei.viewmodel.UserOptionsViewModel
import com.example.vsensei.viewmodel.WordViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModules = module {

    // Shared preferences
    single { provideSharedPreferences(get()) }

    // Repository
    single { Repository(get(), get()) }

    // ViewModel
    viewModel { WordViewModel(get()) }
    viewModel { PracticeViewModel(get()) }
    viewModel { UserOptionsViewModel(get()) }
    viewModel { GroupShareViewModel(get()) }

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