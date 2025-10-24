package com.example.comp4521_quiz_app.quiz_activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentStartQuizBinding


class StartQuizFragment : Fragment() {
    private val args: StartQuizFragmentArgs by navArgs()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding=DataBindingUtil.inflate<FragmentStartQuizBinding>(inflater,R.layout.fragment_start_quiz,container,false)
        val strtext = args.quizType
        val quizInt=args.quizTypeInt
        binding.TypeTextview.text=strtext
        binding.startButton.setOnClickListener {
            val action=StartQuizFragmentDirections.actionStartQuizFragmentToQuizQuestionFragment(
                quizInt,strtext)
            requireView().findNavController().navigate(action)
        }
        return binding.root
    }
}