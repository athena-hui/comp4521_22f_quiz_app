package com.example.comp4521_quiz_app.quiz_activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentQuizQuestionBinding
import com.google.android.material.snackbar.Snackbar
import kotlin.properties.Delegates



class QuizQuestionFragment : Fragment() {
    private val args: QuizQuestionFragmentArgs by navArgs()
    private val viewModel:QuizQuestionViewModel by viewModels{QuizQuestionViewModelFactory(args.quizTypeStr)}
    private lateinit var binding:FragmentQuizQuestionBinding
    private var quizType by Delegates.notNull<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizType=args.quizType
    }

    override fun onDestroy() {
        viewModel.countDownTimer.cancel()
        super.onDestroy()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_quiz_question,container,false)
        // Inflate the layout for this fragment

        binding.OptionAButton.setOnClickListener {
            onSubmitAnswer("Option 1")
        }
        binding.OptionBButton.setOnClickListener {
            onSubmitAnswer("Option 2")
        }
        binding.OptionCButton.setOnClickListener {
            onSubmitAnswer("Option 3")
        }
        binding.OptionDButton.setOnClickListener {
            onSubmitAnswer("Option 4")
        }
        binding.OptionAButton.isEnabled=false
        binding.OptionBButton.isEnabled=false
        binding.OptionCButton.isEnabled=false
        binding.OptionDButton.isEnabled=false
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {

            }
        })
        viewModel.currentSecondObserver.observe(viewLifecycleOwner) {
            updateTimer()
            if (viewModel.getSecond() == 0) {
                if (viewModel.nextQuestion()) {
                    updateNextQuestion()
                    Snackbar.make(
                        requireView(),
                        "Exceed time limit. Go to next Question.",
                        Snackbar.LENGTH_LONG
                    ).show()
                } else {
                    val action =
                        QuizQuestionFragmentDirections.actionQuizQuestionFragmentToQuizResultFragment(
                            viewModel.quizScore,
                            viewModel.correctAnswer,
                            viewModel.totalTime,
                            quizType,
                            args.quizTypeStr
                        )
                    requireView().findNavController().navigate(action)
                }

            }
        }
        viewModel.initObserver.observe(viewLifecycleOwner) {
            if (it) {
                if (viewModel.questionSize<10){
                    requireView().findNavController().navigate(R.id.action_quizQuestionFragment_to_quizDashboardFragment)
                    Snackbar.make(
                        binding.root,
                        "Offline access database do not have enough questions. Internet is required to access new question.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                updateNextQuestion()
                binding.OptionAButton.isEnabled=true
                binding.OptionBButton.isEnabled=true
                binding.OptionCButton.isEnabled=true
                binding.OptionDButton.isEnabled=true
            }
        }
        //updateNextQuestion()
        return binding.root
    }

    private fun updateTimer(){
        binding.TimeProgressBar.progress=viewModel.currentSecondLeft
        binding.TimeTextView.text=viewModel.currentSecondLeft.toString()
    }
    private fun updateNextQuestion(){
        binding.OptionAButton.text=viewModel.currentQuizOptionA
        binding.OptionBButton.text=viewModel.currentQuizOptionB
        binding.OptionCButton.text=viewModel.currentQuizOptionC
        binding.OptionDButton.text=viewModel.currentQuizOptionD
        binding.QuestionNumTextView.text="Question "+(viewModel.currentQuizQuestionCount).toString()
        binding.QuestionProgressBar.progress=viewModel.currentQuizQuestionCount-1
        binding.TimeProgressBar.progress=viewModel.currentSecondLeft
        binding.QuestionTextView.text=viewModel.currentQuizQuestion
        if (viewModel.currentPhotoLink!=""){
            binding.QuestionImageView.visibility=View.VISIBLE
            Glide.with(requireContext()).load(viewModel.currentPhotoLink).into(binding.QuestionImageView)
        }
        else{
            binding.QuestionImageView.visibility=View.INVISIBLE
        }
    }

    private fun onSubmitAnswer(inputAnswer:String){
        viewModel.addScore(inputAnswer)
        if (viewModel.nextQuestion()){
            updateNextQuestion()
        }
        else{
            val action=QuizQuestionFragmentDirections.actionQuizQuestionFragmentToQuizResultFragment(viewModel.quizScore,viewModel.correctAnswer,viewModel.totalTime,quizType,args.quizTypeStr)
            requireView().findNavController().navigate(action)
        }
    }
}