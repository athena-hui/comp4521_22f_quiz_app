package com.example.comp4521_quiz_app.ranking_activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 6
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> RankingFragment.newInstance("Festival")       //Festival
            1 -> RankingFragment.newInstance("Food")            //Food
            2 -> RankingFragment.newInstance("Geography")       //Geography
            3 -> RankingFragment.newInstance("History")         //History
            4 -> RankingFragment.newInstance("Pop culture")     //Pop culture
            5 -> RankingFragment.newInstance("Transportation")  //Transportation
            else -> RankingFragment.newInstance("Festival")
        }
    }

}