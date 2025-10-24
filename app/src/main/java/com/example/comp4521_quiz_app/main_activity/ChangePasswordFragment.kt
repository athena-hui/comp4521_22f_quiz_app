package com.example.comp4521_quiz_app.main_activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentChangePasswordBinding
import com.example.comp4521_quiz_app.main_activity.dialog.InfoDialog
import com.example.comp4521_quiz_app.main_activity.dialog.InfoDialogWithNavDir
import com.example.comp4521_quiz_app.main_activity.viewModel.MainActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChangePasswordFragment : Fragment() {
    private val viewModel: MainActivityViewModel by viewModels()
    private var _binding: FragmentChangePasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.confirmButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val db = Firebase.firestore
                val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)
                val userId = sharedPref?.getString(getString(R.string.user_id), "Error")!!
                val oldPassword = binding.oldPassword.text.toString()
                val newPassword = binding.newPassword.text.toString()
                val newPassword2 = binding.confirmNewPassword.text.toString()

                // check is all input is not empty
                var inputValid = viewModel.isUserNameAndPasswordValid(oldPassword, newPassword)
                if (!inputValid) {
                    val dialog = InfoDialog("Password cannot be empty!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }
                inputValid = viewModel.isUserNameAndPasswordValid(newPassword, newPassword2)
                if (!inputValid) {
                    val dialog = InfoDialog("Password cannot be empty!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                // check if 2 new password input is the same
                inputValid = newPassword == newPassword2
                if (!inputValid) {
                    val dialog = InfoDialog("Two new passwords are different! Please retry!")
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

                // check if old password is correct
                val passwordCorrect = viewModel.offlineLogin(
                    sharedPref,
                    getString(R.string.password),
                    oldPassword
                )
                if (!passwordCorrect) {
                    val dialog = InfoDialog("Old password is incorrect! Please retry!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                // change the password both in cloud and local
                withContext(Dispatchers.Default) {
                    val success = viewModel.changePasswordInCloud(
                        db,
                        getString(R.string.password),
                        newPassword,
                        userId
                    )
                    viewModel.changePasswordInLocal(
                        sharedPref,
                        getString(R.string.password),
                        newPassword,
                    )
                }

                val action = ChangePasswordFragmentDirections.actionChangePasswordFragmentToAccountFragment()
                val popUpDialog = InfoDialogWithNavDir(
                    "Password change successfully!",
                    action,
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