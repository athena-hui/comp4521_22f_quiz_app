package com.example.comp4521_quiz_app.quiz_activity

import android.os.SystemClock
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.comp4521_quiz_app.R
import com.google.android.material.datepicker.CompositeDateValidator.allOf
import junit.framework.TestCase
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class QuizDashboardFragmentTest: TestCase() {
    private lateinit var scenario: FragmentScenario<QuizDashboardFragment>
    @Before
    fun init(){
        scenario= launchFragmentInContainer(themeResId = R.style.Theme_Comp4521_quiz_app)
        scenario.moveToState(Lifecycle.State.STARTED)
    }
    @Test
    fun testAddQuestionButton(){
        val mockNavController= Mockito.mock(NavController::class.java)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        onView(withId(R.id.AddQuestionButton)).perform(ViewActions.click())
        Mockito.verify(mockNavController).navigate(R.id.action_quizDashboardFragment_to_addQuestionFragment)
    }
}