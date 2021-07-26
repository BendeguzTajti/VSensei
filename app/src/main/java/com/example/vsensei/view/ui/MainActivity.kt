package com.example.vsensei.view.ui

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vsensei.R
import com.example.vsensei.databinding.ActivityMainBinding
import com.example.vsensei.view.adapter.PracticeCardAdapter

class MainActivity : AppCompatActivity(), PracticeCardAdapter.WordGuessCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var correctAnswerSoundPlayer: MediaPlayer
    private lateinit var wrongAnswerSoundPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.practiceHomeFragment,
                R.id.dictionaryFragment,
                R.id.scoresFragment,
            )
        )
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.setOnItemReselectedListener {  }
        correctAnswerSoundPlayer = MediaPlayer.create(this, R.raw.correct_answer)
        wrongAnswerSoundPlayer = MediaPlayer.create(this, R.raw.wrong_answer)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        correctAnswerSoundPlayer.release()
        wrongAnswerSoundPlayer.release()
    }

    override fun onCorrectAnswer() {
        correctAnswerSoundPlayer.start()
    }

    override fun onWrongAnswer() {
        wrongAnswerSoundPlayer.start()
    }
}