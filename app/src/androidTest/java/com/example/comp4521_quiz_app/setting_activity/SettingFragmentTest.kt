package com.example.comp4521_quiz_app.setting_activity

import android.content.Context
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.comp4521_quiz_app.R
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingFragmentTest{
    private lateinit var scenario: FragmentScenario<SettingFragment>

    @Before
    fun init(){
        scenario= launchFragmentInContainer(themeResId = R.style.Theme_Comp4521_quiz_app)
        scenario.moveToState((Lifecycle.State.STARTED))
    }

    @Test
    fun bgm_setting_switch_status_match_stored_status() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.setting_sharedPreferences_name),
            Context.MODE_PRIVATE
        )

        val savedStatus =
            sharedPref.getBoolean(context.getString(R.string.BGM_sharedPreferences_name), true)

        if (savedStatus){
            onView(withId(R.id.backgroundMusicSwitch)).check(matches(isChecked()))
        }else{
            onView(withId(R.id.backgroundMusicSwitch)).check(matches(isNotChecked()))
        }

    }

    @Test
    fun theme_setting_switch_status_match_stored_status() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.setting_sharedPreferences_name),
            Context.MODE_PRIVATE
        )

        val savedStatus =
            sharedPref.getBoolean(context.getString(R.string.darkMode_sharedPreferences_name), true)

        if (savedStatus){
            onView(withId(R.id.backgroundThemeSwitch)).check(matches(isChecked()))
        }else{
            onView(withId(R.id.backgroundThemeSwitch)).check(matches(isNotChecked()))
        }

    }

    @Test
    fun test_navigate_to_aboutPage(){
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext())
        scenario.onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph_setting)
            navController.setCurrentDestination(R.id.settingFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }

        onView(withId(R.id.about_layout)).perform(ViewActions.click())
        TestCase.assertEquals(navController.currentDestination?.id, R.id.aboutFragment)
    }
}