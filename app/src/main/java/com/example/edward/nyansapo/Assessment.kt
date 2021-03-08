package com.example.edward.nyansapo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data  class Assessment (
    // "Edward Idun Amoah"
        var STUDENT_ID: String,
        var ASSESSMENT_KEY: String,
        var LETTERS_CORRECT: String,
        var LETTERS_WRONG: String = "",
        var WORDS_CORRECT: String = "",
        var WORDS_WRONG: String = "",
        var PARAGRAPH_WORDS_WRONG: String = "",

    //public String STORY_WORDS_WRONG = "";
        var STORY_ANS_Q1: String = "",
        var STORY_ANS_Q2: String = "",
        var LEARNING_LEVEL: String = "",
        var TIMESTAMP: String? = null,
        var LOCAL_ID: String = "",
        var CLOUD_ID: String = ""
):Parcelable{
    constructor():this("","","","","","","","","","","","","",)
}