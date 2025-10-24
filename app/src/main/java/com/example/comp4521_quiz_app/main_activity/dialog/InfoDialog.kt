package com.example.comp4521_quiz_app.main_activity.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class InfoDialog(
    private val content: String
    ): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(content)
                .setPositiveButton("Ok",null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}