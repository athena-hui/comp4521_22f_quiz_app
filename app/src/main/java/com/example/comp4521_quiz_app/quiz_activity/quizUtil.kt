package com.example.comp4521_quiz_app.quiz_activity

import android.util.Log
import java.net.InetAddress
import java.net.UnknownHostException

class quizUtil {
    companion object{
        fun isInternetAvailable(): Boolean {
            return try {
                val address: InetAddress = InetAddress.getByName("www.google.com")
                //Log.d("Internet Check","1")
                !address.equals("")
            } catch (e: UnknownHostException) {
                //Log.d("Internet Check","0")
                false
            }
        }
    }

}