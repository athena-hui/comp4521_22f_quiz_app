package com.example.comp4521_quiz_app.quiz_activity

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkObject
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.net.InetAddress
import java.net.UnknownHostException

class quizUtilTest{
    @Before
    fun mockInet() {
        mockkStatic(InetAddress::class)
        every { InetAddress.getByName("www.google.com") } throws UnknownHostException()
    }
    @Test
    fun isInternetAvailable(){
        assertFalse(quizUtil.isInternetAvailable())
        unmockkStatic(InetAddress::class)
        assertTrue(quizUtil.isInternetAvailable())
    }
}