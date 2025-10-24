package com.example.comp4521_quiz_app.ranking_activity

import androidx.lifecycle.*
import com.example.comp4521_quiz_app.data_classes.Ranking
import kotlinx.coroutines.launch

class RankingViewModel : ViewModel() {
    private val TAG = "Ranking View model"
    private val rankingFirestore: RankingFirestore = RankingFirestore()
    private val _ranking = MutableLiveData<List<Ranking>>()
    val ranking: LiveData<List<Ranking>>
        get() = _ranking

    fun setRanking(category : String){
        viewModelScope.launch {
            _ranking.value = rankingFirestore.setRanking(category)
        }
    }
}