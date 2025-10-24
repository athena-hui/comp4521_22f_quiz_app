@file:Suppress("IllegalIdentifier")
package com.example.comp4521_quiz_app.main_activity.viewModel

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import androidx.test.core.app.ApplicationProvider
import com.example.comp4521_quiz_app.R
import com.google.common.truth.Truth.assertThat
import org.junit.Assume
import org.junit.Before
import org.junit.Test


class MainActivityViewModelTest {
    private lateinit var viewModel: MainActivityViewModel

    @Before
    fun setup() {
        viewModel = MainActivityViewModel()
    }

    @Test
    fun `empty_email_return_false`() {
        val result = viewModel.isEmailValid(
            ""
        )
        assertThat(result).isFalse()
    }

    @Test
    fun `incorrect_format_email_return_false`() {
        val resultList = mutableListOf<Boolean>()
        resultList.add(viewModel.isEmailValid(
            "a"
        ))
        resultList.add(viewModel.isEmailValid(
            "a@"
        ))
        resultList.add(viewModel.isEmailValid(
            "a."
        ))
        resultList.add(viewModel.isEmailValid(
            "a@a"
        ))
        resultList.add(viewModel.isEmailValid(
            "a.a"
        ))
        resultList.add(viewModel.isEmailValid(
            "a@a."
        ))
       assertThat(resultList).doesNotContain(true)
    }

    @Test
    fun `correct_email_return_true`() {
        val result = viewModel.isEmailValid(
            "a@a.a"
        )
        assertThat(result).isTrue()
    }

    @Test
    fun `empty_userName_return_false`() {
        val resultList = mutableListOf<Boolean>()
        resultList.add(viewModel.isUserNameAndPasswordValid(
            "",
            "",
        ))
        resultList.add(viewModel.isUserNameAndPasswordValid(
            "",
            "a",
        ))
        assertThat(resultList).doesNotContain(true)
    }

    @Test
    fun `empty_password_return_false`() {
        val resultList = mutableListOf<Boolean>()
        resultList.add(viewModel.isUserNameAndPasswordValid(
            "",
            "",
        ))
        resultList.add(viewModel.isUserNameAndPasswordValid(
            "a",
            "",
        ))
        assertThat(resultList).doesNotContain(true)
    }

    @Test
    fun `non-empty_userName_and_password_return_true`() {
        val result = viewModel.isUserNameAndPasswordValid(
            "a",
            "a",
        )
        assertThat(result).isTrue()
    }

    @Test
    fun `return_correct_boolean_depending_on_internet_connection`() {
        val result = viewModel.isInternetAvailable()

        val context = ApplicationProvider.getApplicationContext<Context>()
        val connectivityManager = context.getSystemService(
            CONNECTIVITY_SERVICE
        ) as ConnectivityManager?
        val result_2 = connectivityManager!= null &&
                connectivityManager.activeNetworkInfo != null &&
                connectivityManager.activeNetworkInfo!!.isConnected &&
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) != null

        assertThat(result).isEqualTo(result_2)
    }


    private fun getRandomString(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    @Test
    fun `offline_login_incorrect_password_return_false`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.General),
            Context.MODE_PRIVATE
        )
        val correctPassword = sharedPref.getString(context.getString(R.string.password), "")
        Assume.assumeTrue(correctPassword != "")

        val resultList = mutableListOf<Boolean>()
        resultList.add(viewModel.offlineLogin(
            sharedPref,
            context.getString(R.string.password),
            ""
        ))
        var incorrectPassword = getRandomString(10)
        while (incorrectPassword == correctPassword) {
            incorrectPassword = getRandomString(10)
        }
        resultList.add(viewModel.offlineLogin(
            sharedPref,
            context.getString(R.string.password),
            incorrectPassword
        ))
        assertThat(resultList).doesNotContain(true)
    }

    @Test
    fun `offline_login_correct_password_return_true`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.General),
            Context.MODE_PRIVATE
        )
        val correctPassword = sharedPref.getString(context.getString(R.string.password), "")!!
        Assume.assumeTrue(correctPassword != "")

        val result = viewModel.offlineLogin(
            sharedPref,
            context.getString(R.string.password),
            correctPassword,
        )
        assertThat(result).isTrue()
    }
}