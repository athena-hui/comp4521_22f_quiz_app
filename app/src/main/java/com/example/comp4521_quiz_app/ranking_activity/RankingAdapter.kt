package com.example.comp4521_quiz_app.ranking_activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.data_classes.Ranking

class RankingAdapter(private val context: Context, private val localUserID: String):
    RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {
    val TAG : String = "Adapter"
    var ranking: List<Ranking> = emptyList()

    fun setRankingList(newRankingList: List<Ranking>){
        this.ranking = newRankingList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_ranking_detail, parent, false)
        return RankingViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return ranking.size
    }

    override fun onBindViewHolder(viewHolder: RankingViewHolder, position: Int) {
        val currentItem = ranking[position]
        viewHolder.userRank.text = currentItem.rank
        viewHolder.userName.text = currentItem.userName
        viewHolder.userCorrectNum.text = "Correct Ans: ${currentItem.numOfCorrectAns}"
        viewHolder.userTimeSpent.text = "Time Spent: ${currentItem.timeSpent} s"
        viewHolder.userScore.text = currentItem.score

        //only load all profile pic if internet is available
        //else only load the local profile pic of the current user
        if (currentItem.userProfile != ""){
            Glide.with(context)
                .load(currentItem.userProfile)
                .placeholder(R.drawable.ranking_user)
                .error(R.drawable.ranking_user)
                .into(viewHolder.userProfile)
        }else{
            if(currentItem.userID.toString() == localUserID) {
                Glide.with(context)
                    .load(loadProfilePicFromInternalStorage(currentItem.userID.toString()))
                    .placeholder(R.drawable.ranking_user)
                    .error(R.drawable.ranking_user)
                    .into(viewHolder.userProfile)
            }else{
                Glide.with(context)
                    .load(R.drawable.ranking_user)
                    .into(viewHolder.userProfile)
            }
        }
    }

    class RankingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userRank: TextView = itemView.findViewById(R.id.userRanking)
        val userProfile: ImageView = itemView.findViewById(R.id.userProfile)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userCorrectNum: TextView = itemView.findViewById(R.id.userCorrectNum)
        val userTimeSpent: TextView = itemView.findViewById(R.id.userTimeSpent)
        val userScore: TextView = itemView.findViewById(R.id.userScore)
    }

    private fun loadProfilePicFromInternalStorage(
        userId: String
    ): Bitmap? {
        var bmp:Bitmap ?= null
        if(context != null) {
            val profilePic = context.filesDir.listFiles { _, name ->
                name.contains(userId)
            }
            val profilePicByte = profilePic[0].readBytes()
            bmp = BitmapFactory.decodeByteArray(profilePicByte, 0, profilePicByte.size)

        }
        return bmp
    }

}