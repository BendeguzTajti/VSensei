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
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.example.vsensei.R
import com.example.vsensei.databinding.ActivityMainBinding
import com.example.vsensei.view.contract.BottomNavActivity
import com.example.vsensei.viewmodel.UserOptionsViewModel
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), BottomNavActivity {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val wordViewModel: WordViewModel by viewModel()
    private val userOptionsViewModel: UserOptionsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navInit()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.recentlyDeletedGroup.collect { group ->
                    Snackbar.make(
                        binding.root,
                        getString(R.string.item_removed, group.wordGroup.groupName),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.undo)) {
                            wordViewModel.restoreWordGroup(group)
                        }
                        .show()
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                wordViewModel.recentlyDeletedWord.collect { word ->
                    val wordName =
                        if (word.wordPrimaryVariant.isNullOrBlank()) word.wordPrimary else word.wordPrimaryVariant
                    Snackbar.make(
                        binding.root,
                        getString(R.string.item_removed, wordName),
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(getString(R.string.undo)) {
                            wordViewModel.addWord(word)
                        }
                        .show()
                }
            }
        }
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
                R.id.practiceFragment,
                R.id.practiceResultFragment
            )
        )
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.setOnItemReselectedListener { }
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.wordGroupFragment || destination.id == R.id.newWordFragment) {
                binding.bottomNavigation.menu[2].setIcon(R.drawable.ic_add_word)
                binding.bottomNavigation.menu[2].setOnMenuItemClickListener {
                    controller.navigate(R.id.newWordFragment, arguments)
                    true
                }
            } else {
                binding.bottomNavigation.menu[2].setIcon(R.drawable.ic_add_group)
                binding.bottomNavigation.menu[2].setOnMenuItemClickListener {
                    controller.navigate(R.id.newGroupFragment)
                    true
                }
            }
        }
    }

    override fun showBottomNav() {
        if (this::binding.isInitialized) {
            TransitionManager.beginDelayedTransition(
                binding.root,
                Slide(Gravity.BOTTOM).addTarget(R.id.bottom_nav_container)
            )
            binding.bottomNavContainer.visibility = View.VISIBLE
        }
    }

    override fun hideBottomNav() {
        if (this::binding.isInitialized) {
            TransitionManager.beginDelayedTransition(
                binding.root,
                Slide(Gravity.BOTTOM).addTarget(R.id.bottom_nav_container)
            )
            binding.bottomNavContainer.visibility = View.GONE
        }
    }
}