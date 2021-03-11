package com.example.edward.nyansapo.presentation.ui.pattern

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import com.example.edward.nyansapo.R
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_pattern.patternLockView

class PatternActivity : AppCompatActivity() {
      companion object {
              private  const val TAG="PatternActivity"
          }

    val SHARED_PREF = "shared_pref"
    val KEY_PATTERN = "pattern"
    lateinit var shared_pref: SharedPreferences

    var newPattern: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pattern)

        patternLockView.addPatternLockListener(object : PatternLockViewListener {
            override fun onStarted() {

            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
            }

            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                newPattern = PatternLockUtils.patternToString(patternLockView, pattern)

                checkIfPatternIsCorrect()


            }

            override fun onCleared() {
            }
        })


    }

    private fun checkIfPatternIsCorrect() {
        val correctPattern=shared_pref.getString(KEY_PATTERN,null)


        if (!correctPattern.equals(newPattern)){
            Toasty.error(this,"Pattern is Wrong").show()
        }else{
            Log.d(TAG, "checkIfPatternIsCorrect: correct pattern")
        }
    }
}