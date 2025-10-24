package com.example.comp4521_quiz_app.ranking_activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.ActivityRankingBinding
import com.google.android.material.tabs.TabLayoutMediator

class RankingActivity : AppCompatActivity() {
    companion object {
        var context: Context? = null
    }
    private val tabTitle = arrayOf("Festival","Food","Geography","History","Pop culture","Transportation")
    private lateinit var binding: ActivityRankingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this

        binding = ActivityRankingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //for Tab
        binding.viewPager2.adapter = TabAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, position ->
            tab.text = tabTitle[position]
        }.attach()

        binding.backButton.setOnClickListener {
            finish()
        }

        supportActionBar?.title = getString(R.string.ranking)
    }
}