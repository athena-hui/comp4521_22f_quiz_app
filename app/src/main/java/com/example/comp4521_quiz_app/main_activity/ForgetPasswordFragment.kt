package com.example.comp4521_quiz_app.main_activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentForgetPasswordBinding
import com.example.comp4521_quiz_app.main_activity.dialog.ForgetPasswordSuccessDialog
import com.example.comp4521_quiz_app.main_activity.dialog.InfoDialog
import com.example.comp4521_quiz_app.main_activity.viewModel.MainActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgetPasswordFragment : Fragment() {
    private val viewModel: MainActivityViewModel by viewModels()
    private var _binding: FragmentForgetPasswordBinding? = null
    private val binding get() = _binding!!
    private val args:  ForgetPasswordFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userNameProvided = args.userNameProvided
        if (userNameProvided) {
            binding.userNameLayout.visibility = View.GONE
            val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)!!
            binding.userNameStored.text = sharedPref.getString(getString(R.string.user_name), "Error!")
        } else {
            binding.userNameStored.visibility = View.GONE
        }

        binding.pwRecoveryButton.setOnClickListener {
            val db = Firebase.firestore
            val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)
            val userNameInput: String
            if (userNameProvided) {
                userNameInput = binding.userNameStored.text.toString()
            } else {
                userNameInput = binding.userName.text.toString()
            }
            val emailInput = binding.email.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                //check if email is valid and not empty
                val emailValid = viewModel.isEmailValid(emailInput)
                if (!emailValid) {
                    val dialog = InfoDialog("Please input a valid email!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                //check if user name is empty
                val inputValid = viewModel.isUserNameAndPasswordValid(userNameInput, emailInput)
                if (!inputValid) {
                    val dialog = InfoDialog("User name cannot be empty!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                // check if internet is available
                val internetAvailable = withContext(Dispatchers.Default) {
                    viewModel.isInternetAvailable()
                }
                if (!internetAvailable) {
                    val dialog = InfoDialog("Internet is not available! Please connect to the internet!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                // check cloud to see if user name matches email
                val accountInfoCorrect = withContext(Dispatchers.Default) {
                    viewModel.isAccountInfoCorrect(
                        db,
                        getString(R.string.email),
                        getString(R.string.user_name),
                        getString(R.string.user_id),
                        emailInput,
                        userNameInput
                    )
                }
                if (!(accountInfoCorrect["isAccountInfoCorrect"] as Boolean)) {
                    val dialog = InfoDialog("Account information incorrect! Please retry!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                val userId = accountInfoCorrect[getString(R.string.user_id)] as String
                val newPassword = "12345678"
                withContext(Dispatchers.Default) {
                    viewModel.changePasswordInCloud(
                        db,
                        getString(R.string.password),
                        newPassword,
                        userId
                    )
                    // if have account detail, sign out first
                    if (sharedPref != null) {
                        if (!sharedPref.getBoolean(getString(R.string.firstSeen), true)) {
                            viewModel.signOut(
                                requireActivity(),
                                sharedPref,
                                getString(R.string.email),
                                getString(R.string.user_name),
                                getString(R.string.password),
                                getString(R.string.user_id),
                                getString(R.string.firstSeen),
                            )
                        }
                    }

                    // sign in using the new password
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
                        newPassword,
                    )
                }

                val action = ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToMainPageFragment()
                val popUpDialog = ForgetPasswordSuccessDialog(
                    "Your password has been reset to $newPassword",
                    action,
                    if (userNameProvided) R.id.offlineLoginFragment else R.id.regORLogFragment
                )
                fragmentManager?.let { it1 -> popUpDialog.show(it1,"Dialog") }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}