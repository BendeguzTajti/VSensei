package com.example.vsensei.view.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vsensei.R
import com.example.vsensei.data.WordGroup
import com.example.vsensei.databinding.ActivityMainBinding
import com.example.vsensei.util.Constants
import com.example.vsensei.viewmodel.UserOptionsViewModel
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

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
                        .setAnchorView(binding.fab)
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
                        .setAnchorView(binding.fab)
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
            when (destination.id) {
                R.id.practiceFragment,
                R.id.practiceResultFragment -> {
                    binding.fab.setOnClickListener(null)
                    hideBottomAppBar()
                }
                R.id.newWordFragment,
                R.id.wordGroupFragment -> {
                    binding.fab.contentDescription = getString(R.string.add_word)
                    binding.fab.setImageResource(R.drawable.ic_add_word)
                    binding.bottomNavigation.isVisible = false
                    binding.fab.setOnClickListener {
                        val wordGroup: WordGroup? =
                            arguments?.getParcelable(Constants.WORD_GROUP_ARGS_KEY)
                        if (wordGroup != null) {
                            val bundle = bundleOf(Constants.WORD_GROUP_ARGS_KEY to wordGroup)
                            controller.navigate(R.id.newWordFragment, bundle)
                        }
                    }
                    showBottomAppBar()
                }
                else -> {
                    binding.fab.contentDescription = getString(R.string.create_group)
                    binding.fab.setImageResource(R.drawable.ic_add_group)
                    binding.bottomNavigation.isVisible = true
                    binding.fab.setOnClickListener {
                        controller.navigate(R.id.newGroupFragment)
                    }
                    showBottomAppBar()
                }
            }
        }
    }

    private fun showBottomAppBar() {
        binding.run {
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.performShow()
            fab.show()
        }
    }

    private fun hideBottomAppBar() {
        binding.run {
            bottomAppBar.performHide()
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return
                    bottomAppBar.visibility = View.GONE
                    fab.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
            fab.hide()
        }
    }
}