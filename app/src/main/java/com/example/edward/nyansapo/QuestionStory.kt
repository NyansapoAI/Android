package com.example.edward.nyansapo

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuestionStory : AppCompatActivity() {
    var story_view: TextView? = null
    var back_button: Button? = null
    var story_txt: String? = null
    var question_count = 0
  lateinit  var assessment: Assessment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_story)
        story_view = findViewById(R.id.story_view)
        back_button = findViewById(R.id.back_button)
        val intent = intent
        story_txt = intent.getStringExtra("story")
        story_view!!.setText(story_txt)
        question_count = intent.getStringExtra("question").toInt()
        assessment = intent.getParcelableExtra("Assessment")
        back_button!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                when (question_count) {
                    0 -> {
                        run {
                            val myIntent = Intent(baseContext, storyQuestions::class.java)
                            myIntent.putExtra("Assessment", assessment)
                            myIntent.putExtra("question", Integer.toString(question_count))
                            startActivity(myIntent)
                        }
                        run {
                            val myIntent = Intent(baseContext, storyQuestions::class.java)
                            myIntent.putExtra("Assessment", assessment)
                             myIntent.putExtra("question", Integer.toString(question_count))
                            startActivity(myIntent)
                        }
                    }
                    1 -> {
                        val myIntent = Intent(baseContext, storyQuestions::class.java)
                        myIntent.putExtra("Assessment", assessment)
                         myIntent.putExtra("question", Integer.toString(question_count))
                        startActivity(myIntent)
                    }
                }
            }
        })
    }
}