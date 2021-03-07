package com.example.edward.nyansapo

import android.app.ActivityOptions
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.SyncStateContract
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.paragraph_assessment
import com.example.edward.nyansapo.presentation.utils.Constants

class paragraph : AppCompatActivity() {
    var mediaPlayer: MediaPlayer? = null
    var paragraph1: Button? = null
    var paragraph2: Button? = null

    // assessment content
    var assessment_content: Assessment_Content? = null
 lateinit   var assessment: Assessment
    var ASSESSMENT_KEY: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paragraph)
        //mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.select);
        //mediaPlayer.start();
        val intent = this.intent


        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        paragraph1 = findViewById(R.id.paragraph1)
        paragraph2 = findViewById(R.id.paragraph2)
        assessment_content = Assessment_Content()
        assessment =Constants.assessmentDocumentSnapshot!!.toObject(Assessment::class.java)!!
        ASSESSMENT_KEY = assessment.getASSESSMENT_KEY()
        val para = getPara(ASSESSMENT_KEY)
        paragraph1!!.setText(para[0])
        paragraph2!!.setText(para[1])
        paragraph1!!.setOnClickListener(View.OnClickListener { v -> startParagraph(v, "0") })
        paragraph2!!.setOnClickListener(View.OnClickListener { v -> startParagraph(v, "1") })

    }

    fun startParagraph(v: View?, paragraph: String?) {
        //mediaPlayer.release();
        val myIntent = Intent(baseContext, paragraph_assessment::class.java)
        myIntent.putExtra("Assessment", assessment)
       startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun getPara(key: String?): Array<String> {
        return when (key) {
            "3" -> {
                Assessment_Content.getP3()
            }
            "4" -> {
                Assessment_Content.getP4()
            }
            "5" -> {
                Assessment_Content.getP5()
            }
            "6" -> {
                Assessment_Content.getP6()
            }
            "7" -> {
                Assessment_Content.getP7()
            }
            "8" -> {
                Assessment_Content.getP8()
            }
            "9" -> {
                Assessment_Content.getP9()
            }
            "10" -> {
                Assessment_Content.getP10()
            }
            else -> Assessment_Content.getP3()
        }
    }
}