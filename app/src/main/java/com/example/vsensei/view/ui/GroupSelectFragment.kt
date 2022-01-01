package com.example.vsensei.view.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialFadeThrough

class GroupSelectFragment : DialogFragment() {

    private val args: GroupSelectFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(R.string.select_group)
            .setItems(args.wordGroupsWithWords.map { it.wordGroup.groupName }.toTypedArray()) { _, which ->
                val selectedGroup = args.wordGroupsWithWords[which]
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