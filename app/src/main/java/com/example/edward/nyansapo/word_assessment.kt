package com.example.edward.nyansapo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View
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
import java.util.concurrent.ExecutionException

class word_assessment : AppCompatActivity() {

    private val TAG = "word_assessment"

    var words: String? = null
    lateinit var word: Array<String>
    var error_count = 0
    var word_count = 0
    var words_tried = 0
    var speechAsync: SpeechAsync? = null

    // assessment content
    var assessment_content: Assessment_Content? = null
    var assessment: Assessment? = null
    lateinit var ASSESSMENT_KEY: String

    // words wrong
    var words_wrong = ""
    var words_correct = ""

    // UI
    var record_button: Button? = null
    var assessment_card: Button? = null
    var change_button: Button? = null

    var filename = "/dev/null"

    // progress bar
    var progressBar: ProgressBar? = null

    /// Control variables or code locks
    var mediaStarted = false
    var transcriptStarted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_assessment)
        initProgressBar()
        Toast.makeText(this, "Click on the Record Button to read or click on change to change the prompt", Toast.LENGTH_LONG).show()

        // will replace later
        val intent = intent
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment")
        ASSESSMENT_KEY = assessment!!.assessmentKey
        assessment_content = Assessment_Content()
        word = getWords(ASSESSMENT_KEY)

        //words = "Table Pen Child Fish Lion Man Tree Door";
        //word = words.split(" ");

        //ui components
        record_button = findViewById(R.id.record_button)
        change_button = findViewById(R.id.change_button)
        assessment_card = findViewById(R.id.assessment_card)

        // progressbar
        progressBar = findViewById(R.id.progressBar2)
        progressBar!!.max = 15000
        progressBar!!.progress = 0

        // intialize
        error_count = 0
        word_count = 0
        words_tried = 0

        // assign first word
        assessment_card!!.text = word[0].trim { it <= ' ' }

        // on click listeners
        assessment_card!!.setOnClickListener(View.OnClickListener { v -> recordStudent(v) })
        change_button!!.setOnClickListener(View.OnClickListener {
            //changeWord1();
            //Toast.makeText(word_assessment.this, "Read ")
        })
        record_button!!.setOnClickListener(View.OnClickListener { v -> recordStudent(v) })
    }

    var drawable: Drawable? = null
    fun recordStudent(v: View?) {
        if (!transcriptStarted) {
            drawable = assessment_card!!.background
            val newDrawable = drawable!!.constantState.newDrawable().mutate()
            //read_button.setBackgroundColor(Color.BLUE);
            val lightblue = Color.parseColor("#82b6ff") //light blue
            //int lightblue = Color.parseColor("#8B4513");
            val lightbrown = Color.parseColor("#eecab1") // light brown
            //int lightbrown = Color.parseColor("#7ab121"); // Green
            //int lightbrown = Color.parseColor("#FFFF00"); // bright yellow
            newDrawable.colorFilter = PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY)
            assessment_card!!.background = newDrawable
            assessment_card!!.setTextColor(lightbrown)
            val speechAsync: SpeechAsync = SpeechAsync()
            speechAsync.execute(v)
            transcriptStarted = true
        }

    }



    fun changeWord() {
        progressBar!!.progress = 0
        if (words_tried > 4) { // if 6 has been tried
            if (error_count < 2) {
                goToThankYou()
            } else {
                goToLetter()
            }
        } else if (word_count < word.size - 1) {
            word_count += 1 // increment sentence count
            words_tried += 1
            assessment_card!!.text = word[word_count].trim { it <= ' ' }
        } else {
            word_count = 0 // dont know why
        }
    }

    fun changeWord1() {
        if (word_count < word.size - 1) {
            word_count += 1 // increment sentence count
            assessment_card!!.text = word[word_count].trim { it <= ' ' }
        } else {
            word_count = 0 // dont know why
        }
    }

    fun startLetter(v: View?) {
        //mediaPlayer.release();
        val myIntent = Intent(baseContext, letter_assessment::class.java)
        startActivity(myIntent)
    }

    inner class SpeechAsync : AsyncTask<View?, String?, String?>() {
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
            config!!.endpointId = endpoint
            assessment_card = findViewById(R.id.assessment_card)
            expected_txt = assessment_card!!.text.toString()
            //assessment_card.setEnabled(false);
            //record_button.setEnabled(false);
        }


        override fun onCancelled(s: String?) {
            super.onCancelled(s)
            reco!!.close()
        }

        override fun onPostExecute(textFromServer: String?) {
            super.onPostExecute(textFromServer)
            assessment_card!!.background = drawable
            assessment_card!!.setTextColor(Color.BLACK)
            if (mediaStarted) {
                mediaStarted = false
            }
            transcriptStarted = false
            if (textFromServer.equals("canceled", ignoreCase = true)) {
                Toast.makeText(this@word_assessment, "Internet Connection Failed", Toast.LENGTH_LONG).show()
            } else if (textFromServer.equals("no match", ignoreCase = true)) {
                Toast.makeText(this@word_assessment, "Try Again Please", Toast.LENGTH_LONG).show()
            } else {
                var textFromServerFormatted = textFromServer!!.replace(".", "")

                if (expected_txt.equals(textFromServerFormatted)) {
                    Log.d(TAG, "onPostExecute: word is correct expected: $expected_txt text from server: $textFromServerFormatted")
                    words_correct += expected_txt!!.trim() + ","
                    Log.d(TAG, "onPostExecute: correct words: $words_correct")

                } else {
                    words_wrong += expected_txt!!.trim() + ","
                    Log.d(TAG, "onPostExecute: word is wrong expected: $expected_txt error text: $textFromServerFormatted")
                    Log.d(TAG, "onPostExecute: wrong words: $words_wrong")

                    error_count += 1
                }
                changeWord()
            }
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


    fun goToLetter() {
        val myIntent = Intent(baseContext, letter_assessment::class.java)

        val map = mapOf("wordsWrong" to words_wrong, "wordsCorrect" to words_correct)
        showProgress(true)
        assessmentDocumentSnapshot!!.reference.set(map, SetOptions.merge()).addOnSuccessListener {
            showProgress(false)



            assessment!!.wordsWrong = words_wrong
            assessment!!.wordsCorrect = words_correct
            myIntent.putExtra("Assessment", assessment)
            startActivity(myIntent)
        }

    }

    fun goToThankYou() {
        showProgress(true)

        val map = mapOf("wordsWrong" to words_wrong, "wordsCorrect" to words_correct, "learningLevel" to "WORD")

        assessmentDocumentSnapshot!!.reference.set(map, SetOptions.merge()).addOnSuccessListener {

            //updating student learning level
            val map2 = mapOf("learningLevel" to "WORD")
            studentDocumentSnapshot!!.reference.set(map2, SetOptions.merge()).addOnSuccessListener {
                showProgress(false)
                val myIntent = Intent(baseContext, thankYou::class.java)
                assessment!!.wordsWrong = words_wrong
                assessment!!.wordsCorrect = words_correct
                assessment!!.learningLevel = "WORD"
                myIntent.putExtra("Assessment", assessment)
                startActivity(myIntent)

            }

        }

    }

    fun getWords(key: String?): Array<String> {
        return when (key) {
            "3" -> {
                Assessment_Content.getW3()
            }
            "4" -> {
                Assessment_Content.getW4()
            }
            "5" -> {
                Assessment_Content.getW5()
            }
            "6" -> {
                Assessment_Content.getW6()
            }
            "7" -> {
                Assessment_Content.getW7()
            }
            "8" -> {
                Assessment_Content.getW8()
            }
            "9" -> {
                Assessment_Content.getW9()
            }
            "10" -> {
                Assessment_Content.getW10()
            }
            else -> Assessment_Content.getW3()
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