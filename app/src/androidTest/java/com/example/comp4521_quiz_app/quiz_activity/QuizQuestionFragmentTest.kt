package com.example.comp4521_quiz_app.quiz_activity

import android.os.SystemClock
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.comp4521_quiz_app.R
import junit.framework.TestCase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuizQuestionFragmentTest: TestCase(){
    private lateinit var scenario: FragmentScenario<QuizQuestionFragment>
    @Before
    fun init(){
        val bundle= bundleOf(
            "quizTypeStr" to "Geography",
            "quizType" to 0,
        )
        scenario= launchFragmentInContainer(themeResId = R.style.Theme_Comp4521_quiz_app, fragmentArgs = bundle)
        scenario.moveToState(Lifecycle.State.STARTED)
    }
    @Test
    fun testAnswerButton(){
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.quiz_navigation)
            navController.setCurrentDestination(R.id.quizQuestionFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        SystemClock.sleep(1000)
        assertEquals(navController.currentDestination?.id,R.id.quizResultFragment)
    }
    @Test
    fun testExceedTimeLimit(){
        Espresso.onView(ViewMatchers.withId(R.id.OptionAButton)).perform(ViewActions.click())
        SystemClock.sleep(30500)
        Espresso.onView(ViewMatchers.withId(com.google.android.material.R.id.snackbar_text))
            .check(ViewAssertions.matches(ViewMatchers.withText("Exceed time limit. Go to next Question.")))
    }
}
