package com.example.comp4521_quiz_app.quiz_activity

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.comp4521_quiz_app.R
import com.example.comp4521_quiz_app.databinding.FragmentQuizDashboardBinding


val typeList= listOf("Geography","Transportation","Pop culture","History","Food","Festival")
val typePhotoList=listOf(R.drawable.ic_geography,R.drawable.ic_transportation,R.drawable.ic_popculture,R.drawable.ic_history,R.drawable.ic_food,R.drawable.ic_festival)
lateinit var recentList: MutableList<Int>

class QuizDashboardFragment : Fragment() {
    private lateinit var recentRecyclerView: RecyclerView
    private lateinit var quizzesRecyclerView: RecyclerView
    private lateinit var addQuestionButton: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding=DataBindingUtil.inflate<FragmentQuizDashboardBinding>(inflater,R.layout.fragment_quiz_dashboard,container,false)
        val view = binding.root
        recentRecyclerView=binding.RecentRecyclerView
        quizzesRecyclerView=binding.QuizzesRecyclerView
        addQuestionButton=binding.AddQuestionButton
        val recentManager= GridLayoutManager(activity,1, GridLayoutManager.HORIZONTAL, false)
        recentRecyclerView.layoutManager=recentManager
        val quizzesManager= GridLayoutManager(activity,2, GridLayoutManager.VERTICAL, false)
        quizzesRecyclerView.layoutManager=quizzesManager
        val quizzesAdapter= activity?.let { QuizTypeAdapter(view, it.applicationContext) }
        quizzesRecyclerView.adapter=quizzesAdapter
        quizzesRecyclerView.setHasFixedSize(true)
        addQuestionButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.action_quizDashboardFragment_to_addQuestionFragment)
        }

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        recentList= mutableListOf()
        for (i in 0..3){
            if (sharedPref != null) {
                recentList.add(sharedPref.getInt("recent$i",-1))
            }
        }
        if (recentList[0]==-1){
            recentRecyclerView.visibility=View.INVISIBLE
            binding.NoRecentTextField.visibility=View.VISIBLE
        }
        else{
            recentRecyclerView.visibility=View.VISIBLE
            binding.NoRecentTextField.visibility=View.INVISIBLE
            val listIterator= recentList.iterator()
            while (listIterator.hasNext()){
                val i=listIterator.next()
                if (i==-1) {
                    listIterator.remove()
                }
            }
            val quizzesTypeAdapter= activity?.let { RecentQuizTypeAdapter(view, it.applicationContext) }
            recentRecyclerView.adapter=quizzesTypeAdapter
            recentRecyclerView.setHasFixedSize(true)
        }
        // Inflate the layout for this fragment
        return binding.root
    }
    class QuizTypeHolder(v: View): RecyclerView.ViewHolder(v){
        var imageView: ImageView =v.findViewById(R.id.quiz_image)
        var textView: TextView=v.findViewById(R.id.quiz_string)
        var cardView: CardView=v.findViewById(R.id.cardView)
    }
    class QuizTypeAdapter(val view: View, val context: Context): RecyclerView.Adapter<QuizTypeHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizTypeHolder {
            return QuizTypeHolder(LayoutInflater.from(parent.context).inflate(R.layout.quiz_type,parent,false))
        }

        override fun onBindViewHolder(holder: QuizTypeHolder, position: Int) {
            holder.textView.text= typeList[position]
            holder.cardView.setOnClickListener {
                //Snackbar.make(view, typeList[position], Snackbar.LENGTH_LONG).show();
                val action =
                    QuizDashboardFragmentDirections.actionQuizDashboardFragmentToStartQuizFragment(
                        typeList[position], position
                    )
                view.findNavController().navigate(action)
            }
            holder.imageView.setImageDrawable(view.context.getDrawable(typePhotoList[position]))
        }

        override fun getItemCount(): Int {
            return typeList.size
        }
    }
    class RecentQuizTypeAdapter(val view: View, val context: Context): RecyclerView.Adapter<QuizTypeHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizTypeHolder {
            return QuizTypeHolder(LayoutInflater.from(parent.context).inflate(R.layout.quiz_type,parent,false))
        }

        override fun onBindViewHolder(holder: QuizTypeHolder, position: Int) {
            holder.textView.text= typeList[recentList[position]]
            holder.cardView.setOnClickListener {
                //Snackbar.make(view, typeList[recentList[position]], Snackbar.LENGTH_LONG).show();
                val action =
                    QuizDashboardFragmentDirections.actionQuizDashboardFragmentToStartQuizFragment(
                        typeList[recentList[position]], recentList[position]
                    )
                view.findNavController().navigate(action)
            }
            holder.imageView.setImageDrawable(view.context.getDrawable(typePhotoList[recentList[position]]))
        }

        override fun getItemCount(): Int {
            return recentList.size
        }
    }
}