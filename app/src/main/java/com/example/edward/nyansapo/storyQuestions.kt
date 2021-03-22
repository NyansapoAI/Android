package com.example.edward.nyansapo


import android.content.Intent
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.presentation.utils.answerQ1
import com.example.edward.nyansapo.presentation.utils.answerQ2
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import java.util.concurrent.ExecutionException

class storyQuestions : AppCompatActivity() {
    private val TAG = "storyQuestions"
    private val Q_1: String = "q1"
    private val Q_2: String = "q2"
    var mediaPlayer: MediaPlayer? = null
    var question_button: Button? = null
    var submit_button: Button? = null
    var story_button: Button? = null
    var record_button: Button? = null
    var answer_view: TextView? = null
    var storyString: String? = null
    var question_count = 0
    lateinit var questionsList: Array<String>


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
        assessment = intent.getParcelableExtra("Assessment")
        ASSESSMENT_KEY = assessment.assessmentKey
        Log.d(TAG, "onCreate: assessmentKey:$ASSESSMENT_KEY")
        assessment_content = Assessment_Content()
        questionsList = getQuestions(ASSESSMENT_KEY)
        story_score = 0
        nyansapoNLP = NyansapoNLP()
        question_count = intent.getStringExtra("question").toInt()
        question_button!!.setText(questionsList[question_count])

        storyString = getStory(ASSESSMENT_KEY)
        submit_button!!.setOnClickListener { submitAnswer() }
        question_button!!.setOnClickListener {
            Toast.makeText(this@storyQuestions, "Click on the mic icon to answer question", Toast.LENGTH_LONG).show()
        }
        record_button!!.setOnClickListener { recordStudent() }
        story_button!!.setOnClickListener {
            Log.d(TAG, "onCreate: going to question story")

            val myIntent = Intent(baseContext, QuestionStory::class.java)
            myIntent.putExtra("Assessment", assessment)
            myIntent.putExtra("story", storyString)
            myIntent.putExtra("question", Integer.toString(question_count))
            startActivity(myIntent)
        }
    }

    private fun saveStudentAnswer() {
        Log.d(TAG, "saveStudentAnswer: saving students answer")

        when (question_count) {
            0 -> {
                answerQ1 = answer_view?.text.toString()

            }
            1 -> {
                answerQ2 = answer_view?.text.toString()

            }
        }

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
        saveStudentAnswer()

    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")

        setDefaultValuesForAnswers()

    }

    private fun setDefaultValuesForAnswers() {
        Log.d(TAG, "setDefaultValuesForAnswers: ")
        when (question_count) {
            0 -> {
                answer_view?.text = answerQ1

            }
            1 -> {
                answer_view?.text = answerQ2

            }
        }

    }

    fun submitAnswer() {
        when (question_count) {
            0 -> {
                question_count++
                question_button!!.text = questionsList[question_count]
                assessment!!.storyAnswerQ1 = answer_view!!.text.toString()
                answer_view!!.text = ""
            }
            1 -> {

                assessment!!.storyAnswerQ2 = answer_view!!.text.toString()
                if (checkAns(assessment) > 0) { // one or all is correct
                    assessment!!.learningLevel = "ABOVE"
                    Log.d(TAG, "submitAnswer: ABOVE")
                } else {
                    Log.d(TAG, "submitAnswer: STORY")
                    assessment!!.learningLevel = "STORY"
                }
                val myIntent = Intent(baseContext, thankYou::class.java)
                myIntent.putExtra("Assessment", assessment)
                startActivity(myIntent)
            }
            else -> {
            }
        }
    }

    fun checkAns(assessment: Assessment?): Int {
        Log.d(TAG, "checkAns: ")
        Log.d(TAG, "checkAns: asssessmentKey:${assessment!!.assessmentKey}")


        val score1 = nyansapoNLP!!.evaluateAnswer(assessment!!.storyAnswerQ1, assessment.assessmentKey.toInt(), 0)
        val score2 = nyansapoNLP!!.evaluateAnswer(assessment.storyAnswerQ2, assessment.assessmentKey.toInt(), 1)
        story_score = score1 + score2
        Log.d(TAG, "checkAns:score1:$score1 ")
        Log.d(TAG, "checkAns:score2:$score2 ")
        Log.d(TAG, "checkAns:story_score:$story_score ")


        return if (story_score > 110) {
            1
        } else {
            0
        }
    }

    fun recordStudent() {

        SpeechAsync().execute()
    }

    inner class SpeechAsync : AsyncTask<Void?, String?, String?>() {
        // Replace below with your own subscription key
        var speechSubscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367"

        // Replace with your own subscription key and region identifier from here: https://aka.ms/speech/sdkregion
        var serviceRegion = "eastus"
        var endpoint = "275310be-2c21-4131-9609-22733b4e0c04"
        var config: SpeechConfig? = null
        var reco: SpeechRecognizer? = null
        var view: View? = null
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

        override fun onPostExecute(textFromServer: String?) {
            super.onPostExecute(textFromServer)
            Log.d(TAG, "onPostExecute: textFromServer:$textFromServer")

            if (textFromServer.equals("canceled", ignoreCase = true)) {
                Toast.makeText(this@storyQuestions, "Internet Connection Failed", Toast.LENGTH_LONG).show()
            } else if (textFromServer.equals("no match", ignoreCase = true)) {
                Toast.makeText(this@storyQuestions, "Try Again", Toast.LENGTH_LONG).show()
            } else {
                answer_view!!.text = textFromServer
            }
            question_button!!.isEnabled = true
            record_button!!.isEnabled = true
            reco!!.close()
        }


        override fun doInBackground(vararg p0: Void?): String? {
            try {

                reco = SpeechRecognizer(config)
                var result: SpeechRecognitionResult? = null
                val task = reco!!.recognizeOnceAsync()!!

                try {
                    result = task.get()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
                assert(result != null)
                if (result!!.reason == ResultReason.RecognizedSpeech) {
                    return result.text.toLowerCase().trim()
                } else if (result.reason == ResultReason.NoMatch) {
                    return "no match"
                } else if (result.reason == ResultReason.Canceled) {
                    return "canceled"
                }
            } catch (err: Error) {
                return " Error" + err.message
            }
            return null
        }
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