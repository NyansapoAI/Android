package com.example.edward.nyansapo

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.MainActivity

class TeacherLogin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_login)
    }

    fun startSelector(v: View?) {
        val myIntent = Intent(baseContext, selectSchool::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun goHome(v: View?) {
        val myIntent = Intent(baseContext, MainActivity::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun registerTeacher(v: View?) {
        val myIntent = Intent(baseContext, RegisterTeacher::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun recordStudent(view: View?) {}
    fun nextAssessment(view: View?) {}
    fun thankYou(view: View?) {}
    fun changeParagraph(view: View?) {}
    fun startWord(view: View?) {}
    fun questionOne(view: View?) {}
    fun submitAnswers(view: View?) {}
}