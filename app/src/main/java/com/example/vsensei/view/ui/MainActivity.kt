package com.example.vsensei.view.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.vsensei.R
import com.example.vsensei.databinding.ActivityMainBinding
import com.example.vsensei.viewmodel.UserOptionsViewModel
import com.google.android.material.transition.platform.MaterialFadeThrough
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val userOptionsViewModel: UserOptionsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.exitTransition = MaterialFadeThrough()
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navInit()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                menu?.getItem(0)?.apply {
                    setIcon(R.drawable.ic_light_mode)
                    setTitle(R.string.light_mode)
                }
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                menu?.getItem(0)?.apply {
                    setIcon(R.drawable.ic_dark_mode)
                    setTitle(R.string.dark_mode)
                }
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.app_theme) {
            when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> {
                    userOptionsViewModel.saveUiMode(Configuration.UI_MODE_NIGHT_NO)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                Configuration.UI_MODE_NIGHT_NO -> {
                    userOptionsViewModel.saveUiMode(Configuration.UI_MODE_NIGHT_YES)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
            true
        } else {
            false
        }
    }

    private fun navInit() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
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
        binding.bottomNavigation.setOnItemReselectedListener { }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.newWordFragment,
                R.id.wordGroupFragment -> {
                    TransitionManager.beginDelayedTransition(
                        binding.root,
                        Slide(Gravity.BOTTOM).excludeTarget(R.id.nav_host_fragment, true)
                    )
                    binding.bottomNavContainer.visibility = View.GONE
                }
                else -> {
                    if (!binding.bottomNavContainer.isVisible) {
                        TransitionManager.beginDelayedTransition(
                            binding.root,
                            Slide(Gravity.BOTTOM).excludeTarget(R.id.nav_host_fragment, true)
                        )
                        binding.bottomNavContainer.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}