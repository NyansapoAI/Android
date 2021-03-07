package com.example.edward.nyansapo

import android.app.ActivityOptions
import android.content.Intent
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import java.util.concurrent.ExecutionException

class storyQuestions : AppCompatActivity() {
    var mediaPlayer: MediaPlayer? = null
    var question_button: Button? = null
    var submit_button: Button? = null
    var story_button: Button? = null
    var record_button: Button? = null
    var answer_view: TextView? = null
    var story_view: TextView? = null
    var story_txt: String? = null
    var question_count = 0
  lateinit  var questions: Array<String>

    // assessment content
    var assessment_content: Assessment_Content? = null
  lateinit  var assessment: Assessment
    var ASSESSMENT_KEY: String? = null

    // story score
    var story_score = 0
    var nyansapoNLP: NyansapoNLP? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_questions)
        question_button = findViewById(R.id.question_button)
        story_button = findViewById(R.id.story_button)
        submit_button = findViewById(R.id.submit_button)
        answer_view = findViewById(R.id.answer_view)
        record_button = findViewById(R.id.record_button)
        //story_view = findViewById(R.id.story_view);
        val intent = intent
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment")
        ASSESSMENT_KEY = assessment.getASSESSMENT_KEY()
        assessment_content = Assessment_Content()
        questions = getQuestions(ASSESSMENT_KEY)
        story_score = 0
        nyansapoNLP = NyansapoNLP()
        question_count = intent.getStringExtra("question").toInt()
        question_button!!.setText(questions[question_count])
        //mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.questions);
        //mediaPlayer.start();

        story_txt = getStory(ASSESSMENT_KEY)
        //story_view.setText(story_txt);
        submit_button!!.setOnClickListener(View.OnClickListener { v -> submitAnswer(v) })
        question_button!!.setOnClickListener(View.OnClickListener { //recordStudent(v);
            Toast.makeText(this@storyQuestions, "Click on the mic icon to answer question", Toast.LENGTH_LONG).show()
        })
        record_button!!.setOnClickListener(View.OnClickListener { v -> recordStudent(v) })
        story_button!!.setOnClickListener(View.OnClickListener { //recordStudent(v);
            val myIntent = Intent(baseContext, QuestionStory::class.java)
            myIntent.putExtra("Assessment", assessment)
           myIntent.putExtra("story", story_txt)
            myIntent.putExtra("question", Integer.toString(question_count))
            startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this@storyQuestions).toBundle())
        })
    }

    fun submitAnswer(v: View?) {
        when (question_count) {
            0 -> {
                question_count++
                question_button!!.text = questions[question_count]
                assessment!!.storY_ANS_Q1 = answer_view!!.text.toString()
                //Toast.makeText(this, answer_view.getText().toString(), Toast.LENGTH_LONG).show();
                answer_view!!.text = ""
            }
            1 -> {

                //Toast.makeText(this, answer_view.getText().toString(), Toast.LENGTH_LONG).show();
                assessment!!.storY_ANS_Q2 = answer_view!!.text.toString()
                if (checkAns(assessment) > 0) { // one or all is correct
                    assessment!!.learninG_LEVEL = "ABOVE"
                } else {
                    assessment!!.learninG_LEVEL = "STORY"
                }
                val myIntent = Intent(baseContext, thankYou::class.java)
                myIntent.putExtra("Assessment", assessment)
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
            }
            else -> {
            }
        }
    }

    fun checkAns(assessment: Assessment?): Int {
        story_score = nyansapoNLP!!.evaluateAnswer(assessment!!.storY_ANS_Q1, assessment.assessmenT_KEY.toInt(), 0) +
                nyansapoNLP!!.evaluateAnswer(assessment.storY_ANS_Q2, assessment.assessmenT_KEY.toInt(), 1)

        //Toast.makeText(this, Integer.toString(story_score), Toast.LENGTH_LONG).show();
        return if (story_score > 110) {
            1
        } else {
            0
        }
    }

    fun recordStudent(v: View?) {
        val speechAsync: SpeechAsync = SpeechAsync()
        speechAsync.execute(v)
    }

    private inner class SpeechAsync : AsyncTask<View?, String?, String?>() {
        // Replace below with your own subscription key
        var speechSubscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367"

        // Replace with your own subscription key and region identifier from here: https://aka.ms/speech/sdkregion
        var serviceRegion = "eastus"
        var endpoint = "275310be-2c21-4131-9609-22733b4e0c04"
        var config: SpeechConfig? = null
        var reco: SpeechRecognizer? = null
        var view: View? = null
        var expected_txt: String? = null
        override fun onPreExecute() {
            super.onPreExecute()
            config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion)
            config!!.setEndpointId(endpoint)
            question_button!!.isEnabled = false
            record_button!!.isEnabled = false
        }



        override fun onCancelled(s: String?) {
            super.onCancelled(s)
            reco!!.close()
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            if (s.equals("canceled", ignoreCase = true)) {
                Toast.makeText(this@storyQuestions, "Internet Connection Failed", Toast.LENGTH_LONG).show()
            } else if (s.equals("no match", ignoreCase = true)) {
                Toast.makeText(this@storyQuestions, "Try Again", Toast.LENGTH_LONG).show()
            } else {
                answer_view!!.text = s
            }
            question_button!!.isEnabled = true
            record_button!!.isEnabled = true
            reco!!.close()
        }

        override fun doInBackground(vararg p0: View?): String? {
            try {
                view = p0[0]
                reco = SpeechRecognizer(config)
                var result: SpeechRecognitionResult? = null
                val task = reco!!.recognizeOnceAsync()!!

                // Note: this will block the UI thread, so eventually, you want to
                //        register for the event (see full samples)
                try {
                    result = task.get()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                assert(result != null)
                if (result!!.reason == ResultReason.RecognizedSpeech) {
                    return result.text
                } else if (result.reason == ResultReason.NoMatch) {
                    return "no match"
                } else if (result.reason == ResultReason.Canceled) {
                    return "canceled"
                }
            } catch (err: Error) {
                return " Error" + err.message
            }
            return null  }
    }



    fun getStory(key: String?): String {
        return when (key) {
            "3" -> {
                Assessment_Content.getS3()
            }
            "4" -> {
                Assessment_Content.getS4()
            }
            "5" -> {
                Assessment_Content.getS5()
            }
            "6" -> {
                Assessment_Content.getS6()
            }
            "7" -> {
                Assessment_Content.getS7()
            }
            "8" -> {
                Assessment_Content.getS8()
            }
            "9" -> {
                Assessment_Content.getS9()
            }
            "10" -> {
                Assessment_Content.getS10()
            }
            else -> Assessment_Content.getS3()
        }
    }

    fun getQuestions(key: String?): Array<String> {
        return when (key) {
            "3" -> {
                Assessment_Content.getQ3()
            }
            "4" -> {
                Assessment_Content.getQ4()
            }
            "5" -> {
                Assessment_Content.getQ5()
            }
            "6" -> {
                Assessment_Content.getQ6()
            }
            "7" -> {
                Assessment_Content.getQ7()
            }
            "8" -> {
                Assessment_Content.getQ8()
            }
            "9" -> {
                Assessment_Content.getQ9()
            }
            "10" -> {
                Assessment_Content.getQ10()
            }
            else -> Assessment_Content.getQ3()
        }
    }
}