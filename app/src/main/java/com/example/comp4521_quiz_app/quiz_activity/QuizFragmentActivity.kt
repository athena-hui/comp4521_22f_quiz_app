package com.example.comp4521_quiz_app.quiz_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.ActivityQuizFragmentBinding

class QuizFragmentActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState:Bundle?){

        super.onCreate(savedInstanceState)

        DataBindingUtil.setContentView<ActivityQuizFragmentBinding>(this, R.layout.activity_quiz_fragment)
        supportActionBar?.title = getString(R.string.quiz)
    }
}