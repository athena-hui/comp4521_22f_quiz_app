package com.example.comp4521_quiz_app.quiz_activity

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.comp4521_quiz_app.data_classes.QuestionSet
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException
import kotlin.random.Random

class QuizQuestionViewModel(quizType:String):ViewModel() {
    private var tempQuestionSetList:List<QuestionSet> = listOf(
        QuestionSet("1"," "," "," "," "," "," ",""),
        QuestionSet("2"," "," "," "," "," "," ",""),
        QuestionSet("3"," "," "," "," "," "," ",""),
        QuestionSet("4"," "," "," "," "," "," ",""),
        QuestionSet("5"," "," "," "," "," "," ",""),
    )
    val questionSize:Int
        get()=tempQuestionSetList.size
    private var _quizScore=0
    val quizScore:Int
        get()=_quizScore
    private var _currentQuizQuestionCount=0
    val currentQuizQuestionCount:Int
        get()=_currentQuizQuestionCount
    private lateinit var _currentQuizQuestion:String
    val currentQuizQuestion:String
        get()=_currentQuizQuestion
    private lateinit var _currentQuizOptionA:String
    val currentQuizOptionA:String
        get()=_currentQuizOptionA
    private lateinit var _currentQuizOptionB:String
    val currentQuizOptionB:String
        get()=_currentQuizOptionB
    private lateinit var _currentQuizOptionC:String
    val currentQuizOptionC:String
        get()=_currentQuizOptionC
    private lateinit var _currentQuizOptionD:String
    val currentQuizOptionD:String
        get()=_currentQuizOptionD
    private lateinit var _currentQuestionAnswer:String
    val currentQuestionAnswer:String
        get()=_currentQuestionAnswer
    private var _currentSecondLeft=30
    val currentSecondLeft:Int
        get()=_currentSecondLeft
    val currentSecondObserver:MutableLiveData<Int> = MutableLiveData<Int>()
    fun getSecond():Int{
        return currentSecondObserver.value?:-1
    }
    val initObserver:MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private lateinit var _currentPhotoLink:String
    val currentPhotoLink:String
        get() = _currentPhotoLink
    private var _totalTime=0
    val totalTime:Int
        get()=_totalTime
    private var _correctAnswer=0
    val correctAnswer:Int
        get()=_correctAnswer
    val countDownTimer=object: CountDownTimer(31000,1000){
        override fun onTick(p0: Long) {
            Log.d("timeTimer",(p0.toInt()/1000).toString())
            _currentSecondLeft = p0.toInt()/1000
            currentSecondObserver.value=_currentSecondLeft
        }

        override fun onFinish() {
        }

    }
    private var completedQuestionIdList:MutableList<String> = mutableListOf()
    private lateinit var currentQuestionId:String
    init{
        initObserver.value=false
        completedQuestionIdList=mutableListOf()
        viewModelScope.async {
            val internet=withContext(Dispatchers.Default) {
                isInternetAvailable()
            }
            val db=FirebaseFirestore.getInstance()
            tempQuestionSetList = if (internet){
                FirestoreService.getQuestionSet(quizType,db)
            }else {
                FirestoreService.getOfflineQuestionSet(quizType)
            }
            if (tempQuestionSetList.size>=10){
                getNextQuestion()
            }
            initObserver.value=true
        }
    }
    private fun getNextQuestion(){
        countDownTimer.cancel()
        val nextQuestion=getRandomQuestion()
        if (completedQuestionIdList.contains(nextQuestion.documentID)){
            getNextQuestion()
        }
        else{
            completedQuestionIdList.add(nextQuestion.documentID)
            currentQuestionId=nextQuestion.documentID
            _currentQuizQuestion=nextQuestion.question
            _currentQuizOptionA=nextQuestion.option1
            _currentQuizOptionB=nextQuestion.option2
            _currentQuizOptionC=nextQuestion.option3
            _currentQuizOptionD=nextQuestion.option4
            _currentQuestionAnswer=nextQuestion.answer
            _currentPhotoLink=nextQuestion.photoLink
            _currentQuizQuestionCount++
            _currentSecondLeft=30
            countDownTimer.start()
        }
    }
    fun nextQuestion():Boolean{
        var remainSecond=30-currentSecondLeft
        if (remainSecond<0){
            remainSecond=0
        }
        _totalTime+=remainSecond
        return if (currentQuizQuestionCount<10){
            getNextQuestion()
            true
        }
        else false
    }
    fun addScore(inputAnswer:String){

        if (inputAnswer==_currentQuestionAnswer) {
            _correctAnswer++
            if (_currentSecondLeft==31){
                _currentSecondLeft=30
            }
            _quizScore += 60 + currentSecondLeft
        }
    }
    private fun getRandomQuestion():QuestionSet{
        val randomValue= Random.nextInt(tempQuestionSetList.size)
        return tempQuestionSetList[randomValue]
    }
    private fun isInternetAvailable(): Boolean {
        return try {
            val address: InetAddress = InetAddress.getByName("www.google.com")
            Log.d("Internet Check","1")
            !address.equals("")
        } catch (e: UnknownHostException) {
            Log.d("Internet Check","0")
            false
        }
    }
}

class QuizQuestionViewModelFactory(private val quizTypeImport: String) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuizQuestionViewModel(quizTypeImport) as T
    }

}