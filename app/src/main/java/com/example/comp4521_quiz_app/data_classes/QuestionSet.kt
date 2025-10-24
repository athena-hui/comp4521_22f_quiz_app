package com.example.comp4521_quiz_app.data_classes

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot


data class QuestionSet(val documentID:String,val question:String,val option1:String,val option2:String,val option3:String,val option4:String,val answer:String,val photoLink:String) {
    companion object{
        fun DocumentSnapshot.toQuestionSet(): QuestionSet?{
            return try {
                val documentId=id
                val answer=getString("answer")!!
                val option1=getString("option1")!!
                val option2=getString("option2")!!
                val option3=getString("option3")!!
                val option4=getString("option4")!!
                val question=getString("question")!!
                val photoLink=getString("photoLink")?:""
                QuestionSet(documentId,question,option1,option2,option3,option4,answer,photoLink)
            }catch (e:Exception){
                Log.e("ConvertQuestionSetError","Error in QuestionSet Conversion")
                null
            }
        }
    }
}