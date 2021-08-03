package com.example.vsensei.view.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vsensei.R
import com.example.vsensei.databinding.ActivityMainBinding
import com.google.android.material.transition.platform.MaterialFadeThrough

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        window.exitTransition = MaterialFadeThrough()
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
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.splashScreenFragment) {
                binding.appBarLayout.visibility = View.INVISIBLE
                binding.bottomNavigation.visibility = View.INVISIBLE
            } else {
                binding.appBarLayout.visibility = View.VISIBLE
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.setOnItemReselectedListener {  }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}