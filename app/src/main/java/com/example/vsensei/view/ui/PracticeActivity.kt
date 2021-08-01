package com.example.vsensei.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.vsensei.R
import com.example.vsensei.databinding.ActivityPracticeBinding
import com.google.android.material.transition.platform.MaterialFadeThrough

class PracticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.apply {
            enterTransition = MaterialFadeThrough()
            allowEnterTransitionOverlap = true
        }
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        val navController = navHostFragment.navController
        navController.setGraph(R.navigation.practice_nav_graph, intent.extras)
        setSupportActionBar(binding.toolbar)
        val appBarConfiguration = AppBarConfiguration.Builder().build()
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        finishAfterTransition()
        return true
    }
}