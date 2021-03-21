package com.example.edward.nyansapo


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.ViewGroup.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.presentation.utils.assessmentDocumentSnapshot
import com.example.edward.nyansapo.presentation.utils.studentDocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import kotlinx.android.synthetic.main.activity_index.*
import java.util.concurrent.ExecutionException

class letter_assessment : AppCompatActivity() {

    private val TAG = "letter_assessment"

    lateinit var letterList: Array<String>

    // intialize
    var error_count = 0
    var letter_count = 0
    var letters_tried = 0

    // assessment content
    var assessment_content: Assessment_Content? = null
    var assessment: Assessment? = null
    var ASSESSMENT_KEY: String? = null

    // letters wrong
    var letters_wrong = ""
    var letters_correct = ""

    // UI
    var record_button: Button? = null
    var assessment_card: Button? = null


    var transcriptStarted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter_assessment)
        initProgressBar()
        Toast.makeText(this, "Click on the Record Button to read or click on change to change the prompt", Toast.LENGTH_LONG).show()
        val intent = intent
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment")
        ASSESSMENT_KEY = assessment!!.assessmentKey
        assessment_content = Assessment_Content()
        letterList = getLetters(ASSESSMENT_KEY).map { it.trim().toLowerCase() }.toTypedArray()


        //ui components
        record_button = findViewById(R.id.record_button)
        assessment_card = findViewById(R.id.assessment_card)
        error_count = 0
        letter_count = 0
        letters_tried = 0


        // assign first word
        assessment_card!!.setText(letterList[0].trim { it <= ' ' })

        // on click listeners
        assessment_card!!.setOnClickListener { recordStudent() }

        record_button!!.setOnClickListener { recordStudent() }
    }

    fun changeLetter() {
        Log.d(TAG, "changeLetter: changing letter")

        Log.d(TAG, "changeLetter: error_count:$error_count")

        Log.d(TAG, "changeLetter: letter_correct: $letters_correct")
        Log.d(TAG, "changeLetter: letter_wrong: $letters_wrong")
        if (letters_tried > 4) {
            Log.d(TAG, "changeLetter: letters tried greater than 4")
            goToThankYou()
        } else if (letter_count < letterList.size - 1) {
            letter_count += 1 // increment sentence count
            letters_tried += 1
            assessment_card!!.text = letterList[letter_count]
        } else {
            letter_count = 0 // dont know why
        }
        Log.d(TAG, "changeLetter: current letter:$letters_tried :total number of letters :${letterList.size - 1}")
    }



    var drawable: Drawable? = null
    fun recordStudent() {
        if (!transcriptStarted) {
            drawable = assessment_card!!.background

            val lightblue = Color.parseColor("#8B4513")

            val lightbrown = Color.parseColor("#7ab121") // Green
            assessment_card!!.setBackgroundColor(lightblue)
            assessment_card!!.setTextColor(lightbrown)

            SpeechAsync().execute()
            transcriptStarted = true
        }

    }

    inner class SpeechAsync : AsyncTask<Void, String?, String?>() {
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
            assessment_card = findViewById(R.id.assessment_card)
            expected_txt = assessment_card!!.getText().toString().toLowerCase().trim()
            //assessment_card.setEnabled(false);
            //record_button.setEnabled(false);
        }


        override fun onCancelled(s: String?) {
            super.onCancelled(s)
            reco!!.close()
        }

        override fun onPostExecute(textFromServer: String?) {
            super.onPostExecute(textFromServer)
            Log.d(TAG, "onPostExecute: textFromServer:$textFromServer")
            assessment_card!!.background = drawable
            assessment_card!!.setTextColor(Color.BLACK)

            transcriptStarted = false

             if (textFromServer.equals("canceled", ignoreCase = true)) {
                Toast.makeText(this@letter_assessment, "Internet Connection Failed", Toast.LENGTH_LONG).show()
            } else if (textFromServer.equals("no match", ignoreCase = true)) {
                Toast.makeText(this@letter_assessment, "Try Again Please", Toast.LENGTH_LONG).show()
            } else {
                val textFromServerFormatted = textFromServer!!.replace(".", "")
                Log.d(TAG, "onPostExecute: textFromServerFormatted by removing dots: $textFromServerFormatted")
                if (expected_txt.equals(textFromServerFormatted)) {
                    Log.d(TAG, "onPostExecute: letter is correct expected text: $expected_txt found: $textFromServerFormatted")
                    letters_correct += expected_txt + ","
                    Log.d(TAG, "onPostExecute: letters_correct: $letters_correct")
                } else {
                    Log.d(TAG, "onPostExecute: letter is wrong expected text: $expected_txt found: $textFromServerFormatted")
                    letters_wrong += expected_txt + ","
                    Log.d(TAG, "onPostExecute: letters_wrong :$letters_wrong")
                    error_count += 1
                }
                changeLetter()
            }
            reco!!.close()
        }

        override fun doInBackground(vararg p0: Void): String? {
            try {
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
                    return result.text.toLowerCase().trim()
                } else if (result.reason == ResultReason.NoMatch) {
                    return "no match"
                } else if (result.reason == ResultReason.Canceled) {
                    return "canceled"
                }
            } catch (err: Error) {
                return " Error" + err.message.toString()
            }
            return null
        }
    }


    private fun goToThankYou() {


        val map = mapOf("letterCorrect" to letters_correct, "lettersWrong" to letters_wrong, "learningLevel" to "LETTER")
        showProgress(true)
        assessmentDocumentSnapshot!!.reference.set(map, SetOptions.merge()).addOnSuccessListener {
            //saving current learning level of student
            val map2 = mapOf("learningLevel" to "LETTER")
            studentDocumentSnapshot!!.reference.set(map2, SetOptions.merge()).addOnSuccessListener {
                showProgress(false)


                val myIntent = Intent(baseContext, thankYou::class.java)
                assessment!!.letterCorrect = letters_correct
                assessment!!.lettersWrong = letters_wrong
                assessment!!.learningLevel = "LETTER"
                myIntent.putExtra("Assessment", assessment)
                startActivity(myIntent)
            }

        }


    }

    fun getLetters(key: String?): Array<String> {
        return when (key) {
            "3" -> {
                Assessment_Content.getL3()
            }
            "4" -> {
                Assessment_Content.getL4()
            }
            "5" -> {
                Assessment_Content.getL5()
            }
            "6" -> {
                Assessment_Content.getL6()
            }
            "7" -> {
                Assessment_Content.getL7()
            }
            "8" -> {
                Assessment_Content.getL8()
            }
            "9" -> {
                Assessment_Content.getL9()
            }
            "10" -> {
                Assessment_Content.getL10()
            }
            else -> Assessment_Content.getL3()
        }
    }

    companion object {
        //audio stuff
        private var mEMA = 0.0
        private const val EMA_FILTER = 0.6
    }

    /////////////////////PROGRESS_BAR////////////////////////////
    lateinit var dialog: AlertDialog

    private fun showProgress(show: Boolean) {

        if (show) {
            dialog.show()

        } else {
            dialog.dismiss()

        }

    }

    private fun initProgressBar() {

        dialog = setProgressDialog(this, "Loading..")
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
    }

    fun setProgressDialog(context: Context, message: String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }

    //end progressbar
}