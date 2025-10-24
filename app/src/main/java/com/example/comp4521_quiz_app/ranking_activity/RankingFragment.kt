package com.example.comp4521_quiz_app.ranking_activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comp4521_quiz_app.R

class RankingFragment : Fragment(R.layout.fragment_ranking) {
    val TAG: String = "Ranking Fragment:"
    private lateinit var adapter: RankingAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var sharedPref: SharedPreferences
    private lateinit var localUserID: String
    private val rankingViewModel: RankingViewModel by activityViewModels()
    private lateinit var thisCategory: String
    companion object{
        @JvmStatic
        fun newInstance(category: String) = RankingFragment().apply {
            arguments = Bundle().apply {
                putString("category", category)
            }
        }
    }

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        val args = arguments
        thisCategory = args!!.getString("category").toString()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.ranking_list)
        emptyTextView = view.findViewById(R.id.empty_rank)
        progressBar = view.findViewById(R.id.progress_bar)

        progressBar.visibility = View.VISIBLE

        sharedPref = activity?.getSharedPreferences(getString(R.string.General), Context.MODE_PRIVATE)!!
        localUserID = sharedPref?.getString(getString(R.string.user_id), "Error")!!

        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        adapter = RankingAdapter(requireActivity(), localUserID)
        recyclerView.adapter = adapter

        rankingViewModel.setRanking(thisCategory)
        rankingViewModel.ranking.observe(viewLifecycleOwner) {
            if (it[0].questionCategory == thisCategory) {
                if(it[0].userID != null) {
                    //update UI if the ranking is not empty
                    adapter.setRankingList(it)
                    adapter.notifyDataSetChanged()

                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                    emptyTextView.visibility = View.GONE
                }else{
                    //show msg if the ranking is empty
                    progressBar.visibility = View.GONE
                    recyclerView.visibility = View.GONE
                    emptyTextView.visibility = View.VISIBLE
                }
            }
        }
    }
}