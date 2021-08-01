package com.example.vsensei.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.vsensei.R
import com.example.vsensei.databinding.ActivityPracticeBinding
import com.google.android.material.transition.MaterialFadeThrough

class PracticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val enter = com.google.android.material.transition.platform.MaterialFadeThrough()
        window.enterTransition = enter
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.practice_nav_graph, intent.extras)
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController)
    }
}