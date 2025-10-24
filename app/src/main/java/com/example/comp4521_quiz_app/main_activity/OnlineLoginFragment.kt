package com.example.comp4521_quiz_app.main_activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentOnlineLoginBinding
import com.example.comp4521_quiz_app.main_activity.dialog.InfoDialog
import com.example.comp4521_quiz_app.main_activity.viewModel.MainActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OnlineLoginFragment : Fragment() {
    private val viewModel: MainActivityViewModel by viewModels()
    private var _binding: FragmentOnlineLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnlineLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val db = Firebase.firestore
                val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)
                val userNameInput = binding.inputUserName.text.toString()
                val passwordInput = binding.inputPassword.text.toString()

                //check if user name and password is empty
                val inputValid = viewModel.isUserNameAndPasswordValid(userNameInput, passwordInput)
                if (!inputValid) {
                    val dialog = InfoDialog("User name and password cannot be empty!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                //check if internet is available
                val internetAvailable = withContext(Dispatchers.Default) {
                    viewModel.isInternetAvailable()
                }
                if (!internetAvailable) {
                    val dialog = InfoDialog("Internet is not available! Please connect to the internet!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                val onlineLoginInSuccess = withContext(Dispatchers.Default) {
                    viewModel.onlineLogin(
                        requireActivity(),
                        db,
                        sharedPref,
                        getString(R.string.email),
                        getString(R.string.user_name),
                        getString(R.string.password),
                        getString(R.string.user_id),
                        getString(R.string.firstSeen),
                        userNameInput,
                        passwordInput,
                    )
                }
                if (onlineLoginInSuccess) {
                    val action = OnlineLoginFragmentDirections.actionOnlineLoginFragmentToMainPageFragment()
                    findNavController().navigate(action)
                } else {
                    val dialog = InfoDialog("User name or password incorrect, please retry!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }
            }
        }

        binding.button7.setOnClickListener {
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
                    val action = OnlineLoginFragmentDirections.actionOnlineLoginFragmentToForgetPasswordFragment(
                        false
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