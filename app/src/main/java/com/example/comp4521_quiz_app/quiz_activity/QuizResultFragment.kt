package com.example.comp4521_quiz_app.quiz_activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentQuizResultBinding

class QuizResultFragment : Fragment() {
    private val args: QuizResultFragmentArgs by navArgs()
    private lateinit var binding: FragmentQuizResultBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_quiz_result, container, false
        )
        binding.scoreTextView.text = args.totalScore.toString()
        binding.correctAnswerTextView.text = args.correctAnswer.toString() + "/10"
        val min: Int = args.spentSecond / 60
        val sec: Int = args.spentSecond % 60
        binding.timeSpentTextView.text = min.toString() + "m " + sec.toString()+"s"
        binding.tryAgainButton.setOnClickListener {
            val action = QuizResultFragmentDirections.actionQuizResultFragmentToStartQuizFragment(
                args.quizTypeString, args.quizType
            )
            requireView().findNavController().navigate(action)
        }
        binding.backButton.setOnClickListener {
            requireView().findNavController()
                .navigate(R.id.action_quizResultFragment_to_quizDashboardFragment)
        }
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val recentList2 = mutableListOf<Int>()
        for (i in 0..3) {
            if (sharedPref != null) {
                if (sharedPref.getInt("recent$i", -1) != -1) {
                    recentList2.add(sharedPref.getInt("recent$i", -1))
                }
            }
        }
        val sharedPref2 = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)
        val userId = sharedPref2?.getString(getString(R.string.user_id), "Error")!!

        FirestoreService.updateRanking(
            hashMapOf(
                "numOfCorrectAns" to args.correctAnswer.toString(),
                "questionCategory" to args.quizTypeString,
                "score" to args.totalScore.toString(),
                "timeSpent" to args.spentSecond.toString(),
                "userID" to userId)
            )


        if (!recentList2.contains(args.quizType)) {
            if (recentList2.size == 4) {
                if (sharedPref != null) {
                    with(sharedPref.edit()) {
                        putInt("recent0", args.quizType)
                        for (i in 0..recentList2.size - 2) {
                            putInt("recent" + (i + 1).toString(), recentList2[i])
                        }
                        apply()
                    }
                }
            } else {
                recentList2.add(0,args.quizType)
                if (sharedPref != null) {
                    with(sharedPref.edit()) {
                        for (i in 0 until recentList2.size) {
                            putInt("recent$i", recentList2[i])
                        }
                        apply()
                    }
                }

            }
        }
        return binding.root
    }

}