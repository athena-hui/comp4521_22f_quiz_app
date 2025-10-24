package com.example.comp4521_quiz_app.quiz_activity


import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.comp4521_quiz_app.R


import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class AddQuestionFragmentTest: TestCase(){
    private lateinit var scenario: FragmentScenario<AddQuestionFragment>
    @Before
    fun init(){
        scenario= launchFragmentInContainer(themeResId = R.style.Theme_Comp4521_quiz_app)
        scenario.moveToState(Lifecycle.State.STARTED)
    }
    @Test
    fun testBackbutton(){
        val mockNavController=mock(NavController::class.java)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        onView(withId(R.id.BackButton)).perform(click())
        verify(mockNavController).navigate(R.id.action_addQuestionFragment_to_quizDashboardFragment)
    }
    @Test
    fun testAddQuestion(){
        val mockNavController=mock(NavController::class.java)
        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)
        }
        onView(withId(R.id.Option1Field)).perform(click())
        onView(withId(R.id.Option1Field)).perform(typeText("Option 1 Testing"))
        onView(withId(R.id.Option2Field)).perform(click())
        onView(withId(R.id.Option2Field)).perform(typeText("Option 2 Testing"))
        onView(withId(R.id.Option3Field)).perform(click())
        onView(withId(R.id.Option3Field)).perform(typeText("Option 3 Testing"))
        onView(withId(R.id.Option4Field)).perform(click())
        onView(withId(R.id.Option4Field)).perform(typeText("Option 4 Testing"))
        onView(withId(R.id.QuestionTextField)).perform(click())
        onView(withId(R.id.QuestionTextField)).perform(typeText("Question Adding instrumental test"))
        onView(withId(R.id.SubmitButton)).perform(click())
        verify(mockNavController).navigate(R.id.action_addQuestionFragment_to_quizDashboardFragment)

    }
    @Test
    fun emptyFieldDetection(){
        onView(withId(R.id.Option1Field)).perform(click())
        onView(withId(R.id.Option1Field)).perform(typeText("Option 1 Testing"))
        onView(withId(R.id.Option2Field)).perform(click())
        onView(withId(R.id.Option2Field)).perform(typeText("Option 2 Testing"))
        onView(withId(R.id.Option3Field)).perform(click())
        onView(withId(R.id.Option3Field)).perform(typeText("Option 3 Testing"))
        //onView(withId(R.id.Option4Field)).perform(click())
        //onView(withId(R.id.Option4Field)).perform(typeText("Option 4 Testing"))
        onView(withId(R.id.QuestionTextField)).perform(click())
        onView(withId(R.id.QuestionTextField)).perform(typeText("Question Adding instrumental test"))
        onView(withId(R.id.SubmitButton)).perform(click())
        onView(withId(com.google.android.material.R.id.snackbar_text))
            .check(matches(withText("All field must be inputted")))
    }

}