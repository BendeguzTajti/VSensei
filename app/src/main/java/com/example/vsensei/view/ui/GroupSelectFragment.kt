package com.example.vsensei.view.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.example.vsensei.viewmodel.WordViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFadeThrough
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GroupSelectFragment : DialogFragment() {

    private val args by navArgs<GroupSelectFragmentArgs>()
    private val wordViewModel: WordViewModel by sharedViewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val groups = wordViewModel.wordGroupsWithEnoughWords.value ?: emptyList()
        return MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(R.string.select_group)
            .setItems(groups.map { it.wordGroup.groupName }.toTypedArray()) { _, which ->
                val selectedGroup = groups[which]
                selectedGroup.words.shuffle()
                val action = GroupSelectFragmentDirections.actionGroupSelectFragmentToPracticeFragment(
                    args.practiceType,
                    selectedGroup,
                    getString(args.practiceType.labelResId)
                )
                exitTransition = MaterialFadeThrough()
                reenterTransition = MaterialFadeThrough()
                findNavController().navigate(action)
            }
            .show()
    }
}