package com.example.comp4521_quiz_app.main_activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.comp4521_quiz_app.databinding.FragmentMainBinding
import com.example.comp4521_quiz_app.quiz_activity.QuizFragmentActivity
import com.example.comp4521_quiz_app.ranking_activity.RankingActivity
import com.example.comp4521_quiz_app.setting_activity.SettingActivity

class MainPageFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Start Account activity
        binding.accountButton.setOnClickListener {
            val action = MainPageFragmentDirections.actionMainPageFragmentToAccountFragment()
            findNavController().navigate(action)
        }

        //Start Quiz activity
        binding.quizButton.setOnClickListener {
            startActivity(Intent(activity, QuizFragmentActivity::class.java))
        }

        //Start Ranking activity
        binding.rankingButton.setOnClickListener {
            startActivity(Intent(activity, RankingActivity::class.java))
        }

        //Start Setting activity
        binding.settingButton.setOnClickListener {
            startActivity(Intent(activity, SettingActivity::class.java))
        }
    }

}