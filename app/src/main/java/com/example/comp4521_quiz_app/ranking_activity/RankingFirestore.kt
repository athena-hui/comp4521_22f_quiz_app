package com.example.comp4521_quiz_app.ranking_activity

import android.util.Log
import com.example.comp4521_quiz_app.data_classes.Ranking
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.InetAddress

class RankingFirestore() {
    private val TAG = "Ranking Firestore"

    private var db : FirebaseFirestore = Firebase.firestore
    var imageRef: StorageReference = Firebase.storage.reference
    val context = RankingActivity.context

    suspend fun setRanking(category : String): List<Ranking> {
        var rank = 1
        var same = 0
        var last: Double = -1.0
        val ranking: MutableList<Ranking> = mutableListOf()

        val firestoreResult = db.collection("Ranking")
            .whereEqualTo("questionCategory", category)
            .orderBy("score", Query.Direction.DESCENDING)
            .get().await()

        for (document in firestoreResult) {
            //determine the ranking of record
            if (last == -1.0) {
                last = document.data["score"].toString().toDouble()
            } else {
                if (last != document.data["score"].toString().toDouble()) {
                    if (same == 0) {
                        rank += 1
                    } else {
                        rank += same + 1
                        same = 0
                    }
                    last = document.data["score"].toString().toDouble()
                } else {
                    same += 1
                }
            }

            //all the ranking to list
            val userNameResult = getUserName(document.data["userID"].toString())
            if(!userNameResult.isNullOrEmpty()) {
                val createRank = Ranking(
                    rank = rank.toString(),
                    userID = document.data["userID"].toString(),
                    userName = userNameResult,
                    userProfile = getUserProfileLink(imageRef, document.data["userID"].toString()),
                    questionCategory = document.data["questionCategory"].toString(),
                    score = document.data["score"].toString(),
                    numOfCorrectAns = document.data["numOfCorrectAns"].toString(),
                    timeSpent = document.data["timeSpent"].toString()
                )
                ranking.add(createRank)
            }
        }

        //if the ranking is empty, add a ranking with category only
        if(ranking.isEmpty()) {
            val emptyRank = Ranking(questionCategory = category)
            ranking.add(emptyRank)
        }
        return ranking.toList()
    }

    //get user name by userID
    private suspend fun getUserName(userID : String): String{
        var userName = ""
        val userInfo = db.collection("users")
            .document(userID)
            .get().await()
        if(userInfo !=null) {
            userName = userInfo.data?.get("User Name").toString()
        }
        return userName
    }

    //get the downloadURL of user profile pic by userID if internet available
    private suspend fun getUserProfileLink(imageRef: StorageReference, userID: String): String {
        val internet = withContext(Dispatchers.Default) {
            isInternetAvailable()
        }

        return if (internet) {
            imageRef.child("profile pictures/$userID.bmp").downloadUrl.await().toString()
        }else{
            ""
        }
    }

    //check if internet available
    private fun isInternetAvailable():Boolean {
        return try {
            val ipAdd = InetAddress.getByName("google.com")
            !ipAdd.equals("")
        } catch (e: Exception) {
            Log.i("***","Network exception: $e")
            false
        }
    }
}