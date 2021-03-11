package com.example.edward.nyansapo.presentation.ui.pattern

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.example.edward.nyansapo.R
import com.example.edward.nyansapo.home
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_set_pattern.*

class SetPatternActivity : AppCompatActivity() {

    val SHARED_PREF = "shared_pref"
    val KEY_PATTERN = "pattern"
    lateinit var shared_pref: SharedPreferences

    var patternString: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_pattern)
        shared_pref = getSharedPreferences(SHARED_PREF, MODE_PRIVATE)

        patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {

            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
            }

            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                patternString = PatternLockUtils.patternToString(patternLockView, pattern)


            }

            override fun onCleared() {
            }
        })



        btnSet.setOnClickListener {

            if (patternString.isBlank()){
                Toasty.error(this,"Please set a pattern").show()
            }else{

                shared_pref.edit().putString(KEY_PATTERN,patternString).commit()
                Toasty.success(this,"Success saving pattern").show()
                goToHomePage()
            }

        }
    }


    private fun goToHomePage() {
        val myIntent = Intent(baseContext, home::class.java)
        startActivity(myIntent)

    }
}