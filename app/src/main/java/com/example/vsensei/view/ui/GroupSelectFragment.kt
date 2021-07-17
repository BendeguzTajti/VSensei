package com.example.vsensei.view.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.vsensei.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GroupSelectFragment : DialogFragment() {

    private val args: GroupSelectFragmentArgs by navArgs()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.select_group)
            .setItems(args.wordGroupsWithWords.map { it.wordGroup.groupName }.toTypedArray()) { dialog, which ->
                Log.d("TEST", "onCreateDialog: " + args.wordGroupsWithWords[which])
                val action = GroupSelectFragmentDirections.actionGroupSelectFragmentToScoresFragment()
                findNavController().navigate(action)
            }
            .show()
    }
}