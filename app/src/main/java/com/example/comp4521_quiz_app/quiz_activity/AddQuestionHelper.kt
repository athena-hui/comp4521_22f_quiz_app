package com.example.comp4521_quiz_app.quiz_activity

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.opencsv.CSVReader
import java.io.IOException
import java.io.InputStreamReader
//Used for initialize firebase data by csv file
class AddQuestionHelper {
    companion object{
        fun addQuestion(context: Context,fileName: String){
            val db = Firebase.firestore
            try{
                val reader = CSVReader(InputStreamReader(context.assets.open(fileName)))
                val data=reader.readAll()
                for (i in 1 until data.size){
                    val question = hashMapOf(
                        "question" to data[i][0],
                        "option1" to data[i][1],
                        "option2" to data[i][2],
                        "option3" to data[i][3],
                        "option4" to data[i][4],
                        "answer" to data[i][5],
                        "category" to data[i][6],
                        "photoLink" to data[i][7]
                    )

                    // Add a new document with a generated ID
                    db.collection("QuestionSet")
                        .add(question)
                        .addOnSuccessListener { documentReference ->
                            Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(ContentValues.TAG, "Error adding document", e)
                        }
                }
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
    }

}