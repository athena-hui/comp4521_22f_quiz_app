package com.example.comp4521_quiz_app.setting_activity

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.comp4521_quiz_app.BackgroundMusicService
import com.example.comp4521_quiz_app.MainActivity
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentSettingBinding

@Suppress("DEPRECATION")
class SettingFragment: Fragment() {
    private var _binding: FragmentSettingBinding ?= null
    private val binding get() = _binding!!
    private lateinit var bgmSharedPref: SharedPreferences
    private lateinit var bgmEditor: SharedPreferences.Editor
    private lateinit var darkModeSharedPref: SharedPreferences
    private lateinit var darkModeEditor: SharedPreferences.Editor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //get BGM service is running or not and set the status to switch
        binding.backgroundMusicSwitch.isChecked = serviceRunning(BackgroundMusicService::class.java)

        binding.backgroundMusicSwitch.setOnCheckedChangeListener { _, onSwitch ->
            bgmSharedPref = activity?.getSharedPreferences(getString(R.string.setting_sharedPreferences_name), Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            bgmEditor = bgmSharedPref.edit()
            if(onSwitch){
                activity?.startService(Intent(activity, BackgroundMusicService::class.java))
                bgmEditor.putBoolean(getString(R.string.BGM_sharedPreferences_name), true)
                bgmEditor.apply()
            }else{
                activity?.stopService(Intent(activity, BackgroundMusicService::class.java))
                bgmEditor.putBoolean(getString(R.string.BGM_sharedPreferences_name), false)
                bgmEditor.apply()
            }
        }

        //get original status of dark mode
        var darkModeOn = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        binding.backgroundThemeSwitch.isChecked = darkModeOn
        binding.backgroundThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            darkModeSharedPref = activity?.getSharedPreferences(getString(R.string.setting_sharedPreferences_name), Context.MODE_PRIVATE) ?: return@setOnCheckedChangeListener
            darkModeEditor = darkModeSharedPref.edit()
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                darkModeEditor.putBoolean(getString(R.string.darkMode_sharedPreferences_name), true)
                darkModeEditor.apply()
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                darkModeEditor.putBoolean(getString(R.string.darkMode_sharedPreferences_name), false)
                darkModeEditor.apply()
            }
        }

        binding.aboutLayout.setOnClickListener{
            val action = SettingFragmentDirections.actionSettingFragmentToAboutFragment()
            findNavController().navigate(action)
        }
    }

    //check if the background music service is running
    private fun serviceRunning(serviceClass: Class<BackgroundMusicService>): Boolean {
        var manager: ActivityManager =
            activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;
        val services = manager.getRunningServices(Integer.MAX_VALUE)
        for (service in services) {
            if (serviceClass.name.equals(service.service.className)) {
                return true;
            }
        }
        return false;
    }
}