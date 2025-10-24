package com.example.comp4521_quiz_app.main_activity

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.drawToBitmap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentEditAccountInfoBinding
import com.example.comp4521_quiz_app.main_activity.dialog.InfoDialog
import com.example.comp4521_quiz_app.main_activity.dialog.InfoDialogWithNavDir
import com.example.comp4521_quiz_app.main_activity.viewModel.MainActivityViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditAccountInfoFragment : Fragment() {
    private val viewModel: MainActivityViewModel by viewModels()
    private var _binding: FragmentEditAccountInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditAccountInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            binding.profilePic.setImageURI(selectedImage)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = Firebase.firestore
        val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)
        val userId = sharedPref?.getString(getString(R.string.user_id), "Error")!!
        binding.profilePic.visibility = View.INVISIBLE

        viewLifecycleOwner.lifecycleScope.launch {
            binding.profilePic.setImageBitmap(viewModel.loadProfilePicFromInternalStorage(
                requireActivity(),
                userId,
            ))
            binding.profilePic.visibility = View.VISIBLE
        }

        // edit profile picture button
        binding.editProfilePicButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 3)
        }

        // confirm button
        binding.confirmButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val emailInput = binding.newEmail.text.toString()
                val userNameInput = binding.newUserName.text.toString()

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

                //check if internet is available
                val internetAvailable = withContext(Dispatchers.Default) {
                    viewModel.isInternetAvailable()
                }
                if (!internetAvailable) {
                    val dialog = InfoDialog("Internet is not available! Please connect to the internet!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                //check if email has been occupied (email need to be unique)
                val duplicateEmail = withContext(Dispatchers.Default) {
                    viewModel.isEmailOccupied(db, getString(R.string.email), emailInput, userId)
                }
                if (duplicateEmail) {
                    val dialog = InfoDialog("Email $emailInput has been used! Please try a different email!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                //check if user name has been occupied (user name need to be unique)
                val duplicateUserName = withContext(Dispatchers.Default) {
                    viewModel.isUserNameOccupied(db, getString(R.string.user_name), userNameInput, userId)
                }
                if (duplicateUserName) {
                    val dialog = InfoDialog("User name $userNameInput has been used! Please try a different user name!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                //update account info in cloud database and on disk
                val updateAccountSuccess = withContext(Dispatchers.Default) {
                    viewModel.updateAccountInfo(
                        db,
                        sharedPref,
                        getString(R.string.email),
                        getString(R.string.user_name),
                        getString(R.string.user_id),
                        emailInput,
                        userNameInput,
                    )
                }
                if (!updateAccountSuccess) {
                    Log.i("***", "updateAccountSuccess unsuccessful!")
                    val dialog = InfoDialog("Edit account information unsuccessful! Error occurs!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                val profilePic = binding.profilePic.drawable.toBitmap()
                val profilePicByteArray = viewModel.bitmapToByteArray(profilePic)
                val saveProfilePicToInternalStorageSuccess = withContext(Dispatchers.Default) {
                    viewModel.saveProfilePicToInternalStorage(
                        requireActivity(),
                        userId,
                        profilePicByteArray)
                }
                if (!saveProfilePicToInternalStorageSuccess) {
                    Log.i("***", "saveProfilePicToInternalStorageSuccess unsuccessful!")
                    val dialog = InfoDialog("Edit account information unsuccessful! Error occurs!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                val saveProfilePicToCloudSuccess = withContext(Dispatchers.Default) {
                    viewModel.saveProfilePicToCloud(userId, profilePicByteArray)
                }
                if (!saveProfilePicToCloudSuccess) {
                    Log.i("***", "saveProfilePicToCloudSuccess unsuccessful!")
                    val dialog = InfoDialog("Edit account information unsuccessful! Error occurs!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }

                val action = EditAccountInfoFragmentDirections.actionEditAccountInfoFragmentToAccountFragment()
                val popUpDialog = InfoDialogWithNavDir(
                    "Account information update successfully!",
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