package com.example.comp4521_quiz_app.main_activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.comp4521_quiz_app.R

class PlaceholderFragment : Fragment(R.layout.fragment_placeholder) {
    private var firstSeen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // check for if first time open app (no account)
        val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE) ?: return
        firstSeen = sharedPref.getBoolean(getString(R.string.firstSeen), true)
        Log.i("***","firstSeen in PlaceholderFragment = $firstSeen")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // first time open app, go to register screen
        if (firstSeen) {
            val action = PlaceholderFragmentDirections.actionPlaceholderFragmentToRegORLogFragment()
            findNavController().navigate(action)
        } else {
            //not first time open app, go to login screen
            val action = PlaceholderFragmentDirections.actionPlaceholderFragmentToOfflineLoginFragment()
            findNavController().navigate(action)
        }
    }
}