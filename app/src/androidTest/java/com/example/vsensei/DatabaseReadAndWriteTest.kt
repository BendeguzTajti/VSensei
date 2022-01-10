package com.example.vsensei

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.vsensei.data.WordGroup
import com.example.vsensei.data.WordGroupDao
import com.example.vsensei.data.WordGroupDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.rules.TestRule

import org.junit.Rule

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class DatabaseReadAndWriteTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    private lateinit var wordGroupDao: WordGroupDao
    private lateinit var database: WordGroupDatabase

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, WordGroupDatabase::class.java
        )
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        wordGroupDao = database.wordGroupDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun writeWordGroupAndReadInList() {
        val wordGroup = WordGroup(
            1,
            "Foods and Drinks",
            1,
            "en_US",
            0
        )
        testScope.runBlockingTest {
            wordGroupDao.addWordGroup(wordGroup)
        }
        val wordGroupsWithWords = LiveDataTestUtil.getValue(wordGroupDao.getAllWordGroups())
        assertThat(wordGroupsWithWords?.get(0)?.wordGroup, equalTo(wordGroup))
    }
}