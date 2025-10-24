package com.example.comp4521_quiz_app.main_activity.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.comp4521_quiz_app.R

class ForgetPasswordSuccessDialog(
    private val content: String,
    private val navDirection: NavDirections,
    private val popUpToId: Int
):
    DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(content)
                .setPositiveButton("Ok",
                    DialogInterface.OnClickListener { dialog, id ->
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(popUpToId, true)
                            .build()
                        findNavController().navigate(
                            navDirection,
                            navOptions
                        )
                    })
            isCancelable = false
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}