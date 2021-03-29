package com.example.edward.nyansapo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class student_activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)
    }

    fun exportData(v: View?) {
        Toast.makeText(applicationContext, "Data has been exported successfully", Toast.LENGTH_LONG).show()
    }

    fun newSchool(v: View?) {
        val myIntent = Intent(baseContext, registerSchool::class.java)
        startActivity(myIntent)
    }

    fun beginAssessment(v: View?) {
        val myIntent = Intent(baseContext, selectAssesment::class.java)
        startActivity(myIntent)
    }

    fun newStudent(v: View?) {
        val myIntent = Intent(baseContext, AddStudentFragment::class.java)
        startActivity(myIntent)
    }

    fun selectFilter(v: View?) {
        Toast.makeText(applicationContext, "Selected Filter", Toast.LENGTH_LONG).show()
    }

    fun selectSort(v: View?) {
        Toast.makeText(applicationContext, "Selected Sort", Toast.LENGTH_LONG).show()
    }
}