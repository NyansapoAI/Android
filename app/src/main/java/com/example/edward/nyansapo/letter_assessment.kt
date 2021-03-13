package com.example.edward.nyansapo


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.View
import android.view.ViewGroup.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.presentation.utils.Constants
import com.google.firebase.firestore.SetOptions
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class letter_assessment : AppCompatActivity() {
    var mediaPlayer: MediaPlayer? = null
    var letters: String? = null
    lateinit var letter: Array<String>

    // intialize
    var error_count = 0
    var letter_count = 0
    var letters_tried = 0
    var speechAsync: SpeechAsync? = null

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
    var change_button: Button? = null

    // media
    var mediaRecorder: MediaRecorder? = null
    var filename = "/dev/null"

    // progress bar
    var progressBar: ProgressBar? = null

    /// Control variables or code locks
    var mediaStarted = false
    var transcriptStarted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_letter_assessment)
        initProgressBar()
        //mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.letter);
        //mediaPlayer.start();

        // will replace later
        Toast.makeText(this, "Click on the Record Button to read or click on change to change the prompt", Toast.LENGTH_LONG).show()
        val intent = intent
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment")
        ASSESSMENT_KEY = assessment!!.assessmentKey
        assessment_content = Assessment_Content()
        letter = getLetters(ASSESSMENT_KEY)

        //letters = "a d f g k i l m o v q r s t b j";
        //letter = letters.split(" ");

        //ui components
        record_button = findViewById(R.id.record_button)
        change_button = findViewById(R.id.change_button)
        assessment_card = findViewById(R.id.assessment_card)
        error_count = 0
        letter_count = 0
        letters_tried = 0

        // progressbar
        progressBar = findViewById(R.id.progressBar2)
        progressBar!!.setMax(15000)
        progressBar!!.setProgress(0)

        // assign first word
        assessment_card!!.setText(letter[0].trim { it <= ' ' })

        // on click listeners
        assessment_card!!.setOnClickListener(View.OnClickListener { v -> recordStudent(v) })
        change_button!!.setOnClickListener(View.OnClickListener {
            //changeLetter();
        })
        record_button!!.setOnClickListener(View.OnClickListener { v -> recordStudent(v) })
    }

    fun changeLetter() {
        if (letters_tried > 4) {
            goToThankYou()
        } else if (letter_count < letter.size - 1) {
            letter_count += 1 // increment sentence count
            letters_tried += 1
            assessment_card!!.text = letter[letter_count].trim { it <= ' ' }
        } else {
            letter_count = 0 // dont know why
        }
    }

    fun startRecording() {
        mediaRecorder = MediaRecorder()
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        mediaRecorder!!.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB)
        //this.mediaRecorder.setOutputFile(this.file.getAbsolutePath());
        mediaRecorder!!.setOutputFile(filename)
        try {
            mediaRecorder!!.prepare()
        } catch (e: IOException) {
            Log.e("Prepare", "prepare() failed")
        }
        mediaStarted = try {
            mediaRecorder!!.start()
            true
        } catch (ex: Exception) {
            //Toast.makeText(PreAssessment.this, "No feedback", Toast.LENGTH_LONG).show();
            false
        }
        //Toast.makeText(PreAssessment.this, "Started Recording", Toast.LENGTH_SHORT).show();
    }

    fun soundDb(ampl: Double): Double {
        return 20 * Math.log10(amplitudeEMA / ampl)
    }

    val amplitude: Double
        get() = if (mediaRecorder != null) mediaRecorder!!.maxAmplitude.toDouble() else 0.toDouble()
    val amplitudeEMA: Double
        get() {
            val amp = amplitude
            mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA
            return mEMA
        }

    fun changeLetter1() {
        if (letter_count < letter.size - 1) {
            letter_count += 1 // increment sentence count
            assessment_card!!.text = letter[letter_count].trim { it <= ' ' }
        } else {
            letter_count = 0 // dont know why
        }
    }

    var drawable: Drawable? = null
    fun recordStudent(v: View?) {
        // mediaPlayer.release();
        if (!transcriptStarted) {
            drawable = assessment_card!!.background
            val newDrawable = drawable!!.getConstantState().newDrawable().mutate()
            //read_button.setBackgroundColor(Color.BLUE);
            //int lightblue = Color.parseColor("#82b6ff"); light blue
            val lightblue = Color.parseColor("#8B4513")

            //int lightbrown = Color.parseColor("#eecab1"); // light brown
            val lightbrown = Color.parseColor("#7ab121") // Green
            //int lightbrown = Color.parseColor("#FFFF00"); // bright yellow
            newDrawable.colorFilter = PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY)
            assessment_card!!.background = newDrawable
            assessment_card!!.setTextColor(lightbrown)
            val speechAsync: SpeechAsync = SpeechAsync()
            speechAsync.execute(v)
            transcriptStarted = true
        }
        //SpeechAsync speechAsync = new SpeechAsync();
        //speechAsync.execute(v);
        startRecording()
        val exec = ScheduledThreadPoolExecutor(1)
        exec.scheduleAtFixedRate({ // code to execute repeatedly
            val num = amplitudeEMA
            progressBar!!.progress = num.toInt()
        }, 0, 100, TimeUnit.MILLISECONDS)
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
            config!!.setEndpointId(endpoint)
            assessment_card = findViewById(R.id.assessment_card)
            expected_txt = assessment_card!!.getText().toString()
            //assessment_card.setEnabled(false);
            //record_button.setEnabled(false);
        }


        override fun onCancelled(s: String?) {
            super.onCancelled(s)
            reco!!.close()
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            assessment_card!!.background = drawable
            assessment_card!!.setTextColor(Color.BLACK)
            if (mediaStarted) {
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
                progressBar!!.progress = 0
                mediaStarted = false
            }
            transcriptStarted = false

            //progressBar.setBackgroundColor(2);
            if (s.equals("canceled", ignoreCase = true)) {
                Toast.makeText(this@letter_assessment, "Internet Connection Failed", Toast.LENGTH_LONG).show()
            } else if (s.equals("no match", ignoreCase = true)) {
                Toast.makeText(this@letter_assessment, "Try Again", Toast.LENGTH_LONG).show()
            } else {
                val error_txt = SpeechRecognition.compareTranscript(expected_txt, s)
                if (SpeechRecognition.countError(error_txt) == 0) {
                    letters_correct += expected_txt!!.trim { it <= ' ' } + ","
                } else {
                    letters_wrong += error_txt.trim { it <= ' ' } + ","
                }
                error_count += SpeechRecognition.countError(error_txt)
                //Toast.makeText(view.getContext(), "transcript: \'"+ s +"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), "expected: \'"+expected_txt+"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), error_txt , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), Integer.toString(error_count) , Toast.LENGTH_LONG).show();
                /*if (error_count > 2) { // if error less than 3 move to story level
                    //goToThankYou();
                } else if (letter_count > 3 && error_count < 2) { // got to thank you page if error is less than 2
                    //goToThankYou();
                } else */if (s !== "no match") changeLetter()
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
                    return result.text
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
        Constants.assessmentDocumentSnapshot!!.reference.set(map, SetOptions.merge()).addOnSuccessListener {
            //saving current learning level of student
            val map2 = mapOf("learningLevel" to "LETTER")
            Constants.studentDocumentSnapshot!!.reference.set(map2, SetOptions.merge()).addOnSuccessListener {
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