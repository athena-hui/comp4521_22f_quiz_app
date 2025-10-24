package com.example.comp4521_quiz_app.main_activity.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.main_activity.viewModel.MainActivityViewModel

class SignOutConfirmDialog(
    private val navDirection: NavDirections
    ): DialogFragment() {
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Confirm sign out?")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener { dialog, id ->
                        val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)
                        viewModel.signOut(
                            requireActivity(),
                            sharedPref,
                            getString(R.string.email),
                            getString(R.string.user_name),
                            getString(R.string.password),
                            getString(R.string.user_id),
                            getString(R.string.firstSeen)
                        )
                        findNavController().navigate(navDirection)
                    })
                .setNegativeButton("No", null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}