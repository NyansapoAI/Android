package com.example.edward.nyansapo

import android.os.Bundle
import com.example.edward.nyansapo.R
import android.content.Intent
import com.example.edward.nyansapo.student_activity
import android.app.ActivityOptions
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.registerSchool

class selectSchool : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_school)
    }

    fun openSchool(v: View?) {
        val myIntent = Intent(baseContext, student_activity::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun exportData(v: View?) {
        Toast.makeText(applicationContext, "Data has been exported successfully", Toast.LENGTH_LONG).show()
    }

    fun newSchool(v: View?) {
        val myIntent = Intent(baseContext, registerSchool::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun selectFilter(v: View?) {
        Toast.makeText(applicationContext, "Selected Filter", Toast.LENGTH_LONG).show()
    }

    fun selectSort(v: View?) {
        Toast.makeText(applicationContext, "Selected Sort", Toast.LENGTH_LONG).show()
    }
}