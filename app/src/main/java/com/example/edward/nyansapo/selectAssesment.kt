package com.example.edward.nyansapo

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class selectAssesment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_assesment)
    }

    fun exportData(v: View?) {
        Toast.makeText(applicationContext, "Data has been exported successfully", Toast.LENGTH_LONG).show()
    }

    fun newAssessment(v: View?) {
        val myIntent = Intent(baseContext, Begin_Assessment::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun selectAssessment(v: View?) {
        val myIntent = Intent(baseContext, viewAssessment::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun selectFilter(v: View?) {
        Toast.makeText(applicationContext, "Selected Filter", Toast.LENGTH_LONG).show()
    }

    fun selectSort(v: View?) {
        Toast.makeText(applicationContext, "Selected Sort", Toast.LENGTH_LONG).show()
    }
}