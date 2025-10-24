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
import com.example.comp4521_quiz_app.databinding.FragmentAccountBinding
import com.example.comp4521_quiz_app.main_activity.dialog.InfoDialog
import com.example.comp4521_quiz_app.main_activity.dialog.SignOutConfirmDialog
import com.example.comp4521_quiz_app.main_activity.viewModel.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountFragment : Fragment() {
    private val viewModel: MainActivityViewModel by viewModels()
    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)
        val email = sharedPref?.getString(getString(R.string.email), "Error!")!!
        val userName = sharedPref.getString(getString(R.string.user_name), "Error!")!!
        val userId = sharedPref.getString(getString(R.string.user_id), "Error!")!!

        binding.emailString.text = email
        binding.userNameString.text = userName
        binding.profilePic.visibility = View.INVISIBLE
        viewLifecycleOwner.lifecycleScope.launch {
            binding.profilePic.setImageBitmap(viewModel.loadProfilePicFromInternalStorage(
                requireActivity(),
                userId,
            ))
            binding.profilePic.visibility = View.VISIBLE
        }

        // edit account information button
        binding.editInfoButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                //check if internet is available
                val internetAvailable = withContext(Dispatchers.Default) {
                    viewModel.isInternetAvailable()
                }
                if (!internetAvailable) {
                    val dialog = InfoDialog("Internet is not available! Please connect to the internet!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }
                val action = AccountFragmentDirections.actionAccountFragmentToEditAccountInfoFragment()
                findNavController().navigate(action)
            }
        }

        //change password button
        binding.changePwButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                //check if internet is available
                val internetAvailable = withContext(Dispatchers.Default) {
                    viewModel.isInternetAvailable()
                }
                if (!internetAvailable) {
                    val dialog = InfoDialog("Internet is not available! Please connect to the internet!")
                    fragmentManager?.let { it1 -> dialog.show(it1, "Dialog") }
                    return@launch
                }
                val action = AccountFragmentDirections.actionAccountFragmentToChangePasswordFragment()
                findNavController().navigate(action)
            }
        }

        // sign out button
        binding.signOutButton.setOnClickListener {
            val action = AccountFragmentDirections.actionAccountFragmentToRegORLogFragment()
            val popUpDialog = SignOutConfirmDialog(action)
            fragmentManager?.let { it1 -> popUpDialog.show(it1,"Sigh out confirm dialog") }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}