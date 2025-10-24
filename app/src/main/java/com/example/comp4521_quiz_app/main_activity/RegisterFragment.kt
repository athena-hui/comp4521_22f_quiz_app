package com.example.comp4521_quiz_app.main_activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentRegisterBinding
import com.example.comp4521_quiz_app.main_activity.dialog.InfoDialog
import com.example.comp4521_quiz_app.main_activity.viewModel.MainActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment() {
    private val viewModel: MainActivityViewModel by viewModels()
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var registerButtonPressed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore
        // default profile picture
        val bmp = AppCompatResources.getDrawable(requireContext(), R.drawable.ranking_user)!!.toBitmap()
        // Byte Array representation of the default profile picture
        val profilePictureByteArray = viewModel.bitmapToByteArray(bmp)
        val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)

        // register button
        binding.registerButton.setOnClickListener {
            if (registerButtonPressed) return@setOnClickListener

            registerButtonPressed = true
            viewLifecycleOwner.lifecycleScope.launch {
                val emailInput = binding.inputEmail.text.toString()
                val userNameInput = binding.inputUserName.text.toString()
                val passwordInput = binding.inputPassword.text.toString()

                //check if email is valid and not empty
                val emailValid = viewModel.isEmailValid(emailInput)
                if (!emailValid) {
                    val dialog = InfoDialog("Please input a valid email!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    registerButtonPressed = false
                    return@launch
                }

                //check if user name and password is empty
                val inputValid = viewModel.isUserNameAndPasswordValid(userNameInput, passwordInput)
                if (!inputValid) {
                    val dialog = InfoDialog("User name and password cannot be empty!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    registerButtonPressed = false
                    return@launch
                }

                //check if internet is available
                val internetAvailable = withContext(Dispatchers.Default) {
                    viewModel.isInternetAvailable()
                }
                if (!internetAvailable) {
                    val dialog = InfoDialog("Internet is not available! Please connect to the internet!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    registerButtonPressed = false
                    return@launch
                }

                //check if email has been used (email need to be unique)
                val duplicateEmail = withContext(Dispatchers.Default) {
                    viewModel.isEmailOccupied(db, getString(R.string.email), emailInput, null)
                }
                if (duplicateEmail) {
                    val dialog = InfoDialog("Email $emailInput has been used! Please try a different email!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    registerButtonPressed = false
                    return@launch
                }

                //check if user name has been used (user name need to be unique)
                val duplicateUserName = withContext(Dispatchers.Default) {
                    viewModel.isUserNameOccupied(db, getString(R.string.user_name), userNameInput, null)
                }
                if (duplicateUserName) {
                    val dialog = InfoDialog("User name $userNameInput has been used! Please try a different user name!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    registerButtonPressed = false
                    return@launch
                }

                //register account in cloud database and save account info on disk, and create profile picture
                val registerAccountSuccess = withContext(Dispatchers.Default) {
                    //register account
                    viewModel.registerAccount(
                        db,
                        sharedPref,
                        getString(R.string.email),
                        getString(R.string.user_name),
                        getString(R.string.password),
                        getString(R.string.user_id),
                        getString(R.string.firstSeen),
                        emailInput,
                        userNameInput,
                        passwordInput,
                    )
                }

                if (!registerAccountSuccess) {
                    Log.i("***", "registerAccountSuccess unsuccessful!")
                    val dialog = InfoDialog("Registration unsuccessful! Error occurs!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    registerButtonPressed = false
                    return@launch
                }

                val userId = sharedPref!!.getString(getString(R.string.user_id), "Error")!!
                val saveProfilePicCloudSuccess =
                    viewModel.saveProfilePicToCloud(userId ,profilePictureByteArray)
                if (!saveProfilePicCloudSuccess) {
                    Log.i("***", "saveProfilePicCloudSuccess unsuccessful!")
                    val dialog = InfoDialog("Registration unsuccessful! Error occurs!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    registerButtonPressed = false
                    return@launch
                }

                val saveProfilePicLocalSuccess =
                    viewModel.saveProfilePicToInternalStorage(requireActivity(), userId, profilePictureByteArray)
                if (!saveProfilePicLocalSuccess) {
                    Log.i("***", "saveProfilePicLocalSuccess unsuccessful!")
                    val dialog = InfoDialog("Registration unsuccessful! Error occurs!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    registerButtonPressed = false
                    return@launch
                }

                val action = RegisterFragmentDirections.actionRegisterFragmentToMainPageFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}