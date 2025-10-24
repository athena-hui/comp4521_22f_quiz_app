package com.example.comp4521_quiz_app.main_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.comp4521_quiz_app.databinding.FragmentRegOrLogBinding

class RegORLogFragment : Fragment() {
    private var _binding: FragmentRegOrLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegOrLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // register button
        binding.registerButton.setOnClickListener {
            val action = RegORLogFragmentDirections.actionRegORLogFragmentToRegisterFragment()
            findNavController().navigate(action)
        }

        //login in button
        binding.loginButton.setOnClickListener {
            val action = RegORLogFragmentDirections.actionRegORLogFragmentToOnlineLoginFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}