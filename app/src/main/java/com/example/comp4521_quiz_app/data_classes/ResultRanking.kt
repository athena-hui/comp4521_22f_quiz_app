import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

data class ResultRanking (
    var userID: String,
    var questionCategory: String,
    var score: String,
    var numOfCorrectAns: String,
    var timeSpent: String){
    companion object{
        fun DocumentSnapshot.toResultRanking():ResultRanking?{
            return try {
                val userId=getString("userID")!!
                val questionCategory=getString("questionCategory")!!
                val score=getString("score")!!
                val numOfCorrectAns=getString("numOfCorrectAns")!!
                val timeSpent=getString("timeSpent")!!
                ResultRanking(userId,questionCategory,score,numOfCorrectAns,timeSpent)
            }catch (e:Exception){
                Log.e("ConvertRankingError","Error in Result Ranking Conversion")
                null
            }
        }
    }
}