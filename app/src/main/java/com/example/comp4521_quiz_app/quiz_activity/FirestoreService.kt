package com.example.comp4521_quiz_app.quiz_activity

import ResultRanking
import ResultRanking.Companion.toResultRanking
import android.content.ContentValues
import android.util.Log
import com.example.comp4521_quiz_app.data_classes.QuestionSet
import com.example.comp4521_quiz_app.data_classes.QuestionSet.Companion.toQuestionSet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

//store function related to firestore
object FirestoreService {
    suspend fun getQuestionSet(quizType:String, db:FirebaseFirestore):List<QuestionSet>{
        //val db=FirebaseFirestore.getInstance()
        var list:MutableList<QuestionSet> = mutableListOf()
        db.collection("QuestionSet").whereEqualTo("category",quizType).get().addOnSuccessListener {
            result->for (document in result){
                val question=document.toQuestionSet()
                if (question!=null){
                    list.add(question)
                }
            }
            list.shuffle()
            if (list.size>=10){
                list=list.subList(0,10)
            }
        }.addOnFailureListener{
            Log.e("Get Firebase question","Cannot get Questions from firebase")
        }.await()
        return list
    }
    suspend fun getOfflineQuestionSet(quizType:String):List<QuestionSet>{
        val db=FirebaseFirestore.getInstance()
        var list:MutableList<QuestionSet> = mutableListOf()
        db.collection("QuestionSet").whereEqualTo("category",quizType).whereEqualTo("photoLink","").get().addOnSuccessListener {
                result->for (document in result){
            val question=document.toQuestionSet()
            if (question!=null){
                list.add(question)
            }
        }
            list.shuffle()
            if (list.size>=10){
                list=list.subList(0,10)
            }
        }.addOnFailureListener{
            Log.e("Get Firebase question","Cannot get Questions from firebase"+it.toString())
        }.await()
        return list
    }
    suspend fun addQuestionSet(question:HashMap<String,String>){
        val db=FirebaseFirestore.getInstance()
        db.collection("QuestionSet")
            .add(question)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, " Question Set DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Question Set Error adding document", e)
            }.await()
    }
    private fun addRanking(ranking:HashMap<String,String>){

        val db=FirebaseFirestore.getInstance()
        db.collection("Ranking")
            .add(ranking)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "Ranking DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Ranking Error adding document", e)
            }
    }
    fun getRanking(quizType:String,userId:String):ResultRanking?{
        val db=FirebaseFirestore.getInstance()
        var ranking:ResultRanking?=null
        db.collection("Ranking").whereEqualTo("questionCategory",quizType).whereEqualTo("userID",userId).get().addOnSuccessListener {
                result->
            for (document in result){
                ranking=document.toResultRanking()
            }
        }.addOnFailureListener{
            Log.e("Get Firebase rank",it.toString())
        }
        return ranking
    }
    fun updateRanking(ranking: HashMap<String,String>){
        val db=FirebaseFirestore.getInstance()
        if (ranking["userID"]!="Error"){
            db.collection("Ranking").whereEqualTo("questionCategory", ranking["questionCategory"]).whereEqualTo("userID",
                ranking["userID"]
            ).get().addOnSuccessListener { documents ->
                if (documents.size()!=0){
                    for (document in documents){
                        if (document.get("score").toString().toInt() < (ranking["score"]?.toInt()
                                ?: -1)
                        ){
                            db.collection("Ranking").document(document.id).update(ranking as Map<String, Any>).addOnSuccessListener {
                                Log.d("UpdateRanking","Successfully updated")
                            }.addOnFailureListener {
                                Log.w("UpdateRanking","Error on updating")
                            }
                        }
                    }
                }
                else{
                    addRanking(ranking)
                }
            }.addOnFailureListener { exception ->
                Log.w("UpdateRanking", "Error getting documents: ", exception)
            }
        }
    }
}