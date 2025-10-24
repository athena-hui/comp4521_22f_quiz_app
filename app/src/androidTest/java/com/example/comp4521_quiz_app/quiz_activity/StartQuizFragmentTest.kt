package com.example.comp4521_quiz_app.quiz_activity

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
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
import org.mockito.Mockito


@RunWith(AndroidJUnit4::class)
class StartQuizFragmentTest:TestCase(){
    private lateinit var scenario: FragmentScenario<StartQuizFragment>
    @Before
    fun init(){
        val bundle= bundleOf(
            "quizType" to "Geography",
            "quizTypeInt" to 0
        )
        scenario= launchFragmentInContainer(themeResId = R.style.Theme_Comp4521_quiz_app, fragmentArgs = bundle)
        scenario.moveToState(Lifecycle.State.STARTED)
    }
    @Test
    fun testStartButton(){
//        val mockNavController= Mockito.mock(NavController::class.java)
//        scenario.onFragment { fragment ->
//            Navigation.setViewNavController(fragment.requireView(), mockNavController)
//        }
//        Espresso.onView(ViewMatchers.withId(R.id.startButton)).perform(ViewActions.click())
//        Mockito.verify(mockNavController).navigate(R.id.action_startQuizFragment_to_quizQuestionFragment)
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.quiz_navigation)
            navController.setCurrentDestination(R.id.startQuizFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
       }
        onView(ViewMatchers.withId(R.id.startButton)).perform(ViewActions.click())

        assertEquals(navController.currentDestination?.id,R.id.quizQuestionFragment)
    }
    @Test
    fun testCategory(){
        Espresso.onView(ViewMatchers.withId(R.id.TypeTextview)).check(
            ViewAssertions.matches(
                ViewMatchers.withText("Geography")
            )
        )
    }
}