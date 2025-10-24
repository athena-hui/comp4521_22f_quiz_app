package com.example.comp4521_quiz_app.quiz_activity

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
class QuizResultFragmentTest: TestCase(){
    private lateinit var scenario: FragmentScenario<QuizResultFragment>
    @Before
    fun init(){
        val bundle= bundleOf(
            "quizTypeString" to "Geography",
            "quizType" to 0,
            "totalScore" to 600,
            "spentSecond" to 90,
            "correctAnswer" to 7
        )
        scenario= launchFragmentInContainer(themeResId = R.style.Theme_Comp4521_quiz_app, fragmentArgs = bundle)
        scenario.moveToState(Lifecycle.State.STARTED)
    }
    @Test
    fun testTryAgainButton(){
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.quiz_navigation)
            navController.setCurrentDestination(R.id.quizResultFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        Espresso.onView(ViewMatchers.withId(R.id.tryAgainButton)).perform(ViewActions.click())

        assertEquals(navController.currentDestination?.id,R.id.startQuizFragment)
    }
    @Test
    fun testBackButton(){
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.quiz_navigation)
            navController.setCurrentDestination(R.id.quizResultFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        Espresso.onView(ViewMatchers.withId(R.id.backButton)).perform(ViewActions.click())

        assertEquals(navController.currentDestination?.id,R.id.quizDashboardFragment)
    }
    @Test
    fun testOutputText(){
        Espresso.onView(ViewMatchers.withId(R.id.scoreTextView)).check(
            ViewAssertions.matches(
            ViewMatchers.withText("600")
        ))
        Espresso.onView(ViewMatchers.withId(R.id.correctAnswerTextView)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("7/10")
            ))
        Espresso.onView(ViewMatchers.withId(R.id.timeSpentTextView)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("1m 30s")
            ))
    }
}