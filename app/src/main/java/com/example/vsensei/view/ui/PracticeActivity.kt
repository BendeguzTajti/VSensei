package com.example.vsensei.view.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.vsensei.R
import com.example.vsensei.databinding.ActivityPracticeBinding
import com.example.vsensei.viewmodel.UserOptionsViewModel
import com.google.android.material.transition.platform.MaterialFadeThrough
import org.koin.androidx.viewmodel.ext.android.viewModel

class PracticeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPracticeBinding

    private val userOptionsViewModel: UserOptionsViewModel by viewModel()

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        when (resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                menu?.getItem(0)?.apply {
                    setIcon(R.drawable.ic_dark_mode)
                    setTitle(R.string.dark_mode)
                }
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                menu?.getItem(0)?.apply {
                    setIcon(R.drawable.ic_light_mode)
                    setTitle(R.string.light_mode)
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
}