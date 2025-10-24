package com.example.comp4521_quiz_app.data_classes

data class Ranking(
    var rank : String ?= null,
    var userID: String ?= null,
    var userName:String ?= null,
    var userProfile: String ?= null,
    var questionCategory: String ?= null,
    var score: String ?= null,
    var numOfCorrectAns: String ?= null,
    var timeSpent: String ?= null
)