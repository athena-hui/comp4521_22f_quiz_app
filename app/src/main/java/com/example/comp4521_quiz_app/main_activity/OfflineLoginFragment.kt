package com.example.comp4521_quiz_app.main_activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentOfflineLoginBinding
import com.example.comp4521_quiz_app.main_activity.dialog.InfoDialog
import com.example.comp4521_quiz_app.main_activity.dialog.SignOutConfirmDialog
import com.example.comp4521_quiz_app.main_activity.viewModel.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OfflineLoginFragment : Fragment() {
    private val viewModel: MainActivityViewModel by viewModels()
    private var _binding: FragmentOfflineLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOfflineLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)
        if (sharedPref != null) {
            Log.i("***", "user name: ${sharedPref.getString(getString(R.string.user_name),"Error")}")
            Log.i("***", "password: ${sharedPref.getString(getString(R.string.password),"Error")}")
        }

        binding.userNameStored.text = sharedPref?.getString(getString(R.string.user_name), "User Name")

        // login button
        binding.loginButton.setOnClickListener {
            val passwordInput = binding.inputPassword.text.toString()

            val loginInSuccess = viewModel.offlineLogin(sharedPref, getString(R.string.password), passwordInput)
            if (loginInSuccess) {
                val action = OfflineLoginFragmentDirections.actionOfflineLoginFragmentToMainPageFragment()
                findNavController().navigate(action)

            } else {
                val dialog = InfoDialog("Password incorrect, please retry!")
                fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                return@setOnClickListener
            }
        }

        // sign out button
        binding.signOutButton.setOnClickListener {
            val action = OfflineLoginFragmentDirections.actionOfflineLoginFragmentToRegORLogFragment()
            val popUpDialog = SignOutConfirmDialog(action)
            fragmentManager?.let { it1 -> popUpDialog.show(it1,"Sigh out confirm dialog") }
        }

        // forget password button
        binding.forgetPwButton.setOnClickListener {

            viewLifecycleOwner.lifecycleScope.launch {
                val internetAvailable = withContext(Dispatchers.Default) {
                    viewModel.isInternetAvailable()
                }
                if (!internetAvailable) {
                    val dialog =
                        InfoDialog("Internet is not available! Please connect to the internet!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                } else {
                    val action =
                        OfflineLoginFragmentDirections.actionOfflineLoginFragmentToForgetPasswordFragment(
                            true
                        )
                    findNavController().navigate(action)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}