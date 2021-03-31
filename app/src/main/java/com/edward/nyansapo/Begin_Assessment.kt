package com.nyansapo


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.nyansapo.MainActivity
import com.nyansapo.PreAssessment

class Begin_Assessment : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begin__assessment)
    }

    fun goHome(v: View?) {
        val myIntent = Intent(baseContext, MainActivity::class.java)
        startActivity(myIntent)
    }

    fun startPreAssessment(v: View?) {
        val preIntent = Intent(baseContext, PreAssessment::class.java)
        startActivity(preIntent)
    }
}