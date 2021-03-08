package com.example.edward.nyansapo

import android.app.ActivityOptions
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

class story_assessment : AppCompatActivity() {
    var mediaPlayer: MediaPlayer? = null
    var record_student: Button? = null
    var next_button: Button? = null
    var story_view: Button? = null
    var back_button: Button? = null

    //TextView story_view;
    var story: String? = null
    lateinit var sentences: Array<String>
    var sentence_count = 0

    // assessment content
    var assessment_content: Assessment_Content? = null
    var assessment: Assessment? = null
    var ASSESSMENT_KEY: String? = null

    // story reading evaluation
    var story_words_wrong = ""
    var error_count = 0
    var tries = 0

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
        setContentView(R.layout.activity_story_assessment)
        initProgressBar()
        //mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.story);
        //mediaPlayer.start();
        val intent = intent
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment")
        ASSESSMENT_KEY = assessment!!.ASSESSMENT_KEY
        assessment_content = Assessment_Content()
        story = getStory(ASSESSMENT_KEY)
        back_button = findViewById(R.id.back_button)
        next_button = findViewById(R.id.next_button)
        story_view = findViewById(R.id.story_view)

        // progressbar
        progressBar = findViewById(R.id.progressBar2)
        progressBar!!.setMax(15000)
        progressBar!!.setProgress(0)


        //story = "One day the wind chased the sun away. It told the sun to go to another sky. The sun did not go. The next morning, the wind ran after the sun. The sun fell down and started crying. That is how it began to rain. We clapped for Juma.\\n\\n One day the wind chased the sun away. It told the sun to go to another sky. The sun did not go. The next morning, the wind ran after the sun. The sun fell down and started crying. That is how it began to rain. We clapped for Juma.";
        sentences = story!!.split("[.]".toRegex()).toTypedArray()
        sentence_count = 0
        tries = 0
        //story_view.setText(story);
        story_view!!.setText(sentences[sentence_count])
        back_button!!.setEnabled(false)
        next_button!!.setOnClickListener(View.OnClickListener { v ->
            nextParagraph(v)
            //Toast.makeText(story_assessment.this, "Click on the text to read", Toast.LENGTH_LONG).show();
        })
        back_button!!.setOnClickListener(View.OnClickListener { v -> backParagraph(v) })
        story_view!!.setOnClickListener(View.OnClickListener { v -> recordStudent(v) })

        // story reading evaluation code
        error_count = 0
    }

    var drawable: Drawable? = null
    fun recordStudent(v: View?) {
        if (!transcriptStarted) {
            drawable = story_view!!.background
            val newDrawable = drawable!!.getConstantState().newDrawable().mutate()
            //read_button.setBackgroundColor(Color.BLUE);
            val lightblue = Color.parseColor("#82b6ff") //light blue
            //int lightblue = Color.parseColor("#8B4513");
            val lightbrown = Color.parseColor("#eecab1") // light brown
            //int lightbrown = Color.parseColor("#7ab121"); // Green
            //int lightbrown = Color.parseColor("#FFFF00"); // bright yellow
            newDrawable.colorFilter = PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY)
            story_view!!.background = newDrawable
            story_view!!.setTextColor(lightbrown)
            val speechAsync: SpeechAsync = SpeechAsync()
            speechAsync.execute(v)
            transcriptStarted = true
        }
        startRecording()
        val exec = ScheduledThreadPoolExecutor(1)
        exec.scheduleAtFixedRate({ // code to execute repeatedly
            val num = amplitudeEMA
            progressBar!!.progress = num.toInt()
        }, 0, 100, TimeUnit.MILLISECONDS)
    }

    fun backParagraph(v: View?) {
        if (sentence_count == 0) {
            back_button!!.isEnabled = false
        } else {
            sentence_count--
            story_view!!.text = sentences[sentence_count]
            if (sentence_count == 0) back_button!!.isEnabled = false
        }
    }

    fun nextParagraph(v: View?) {
        tries = 0
        progressBar!!.progress = 0
        if (sentence_count < sentences.size - 1) {
            //back_button.setEnabled(true);
            sentence_count += 1 // increment sentence count
            story_view!!.text = sentences[sentence_count].trim { it <= ' ' }
        } else {
            //assessment.setSTORY_WORDS_WRONG(story_words_wrong); // set story wrong words
            val temp = assessment!!.PARAGRAPH_WORDS_WRONG


            val map = mapOf("PARAGRAPH_WORDS_WRONG " to temp + story_words_wrong)

            showProgress(true)
            Constants.assessmentDocumentSnapshot!!.reference.set(map, SetOptions.merge()).addOnSuccessListener {
                showProgress(false)

                assessment!!.PARAGRAPH_WORDS_WRONG = temp + story_words_wrong
                val myIntent = Intent(baseContext, storyQuestions::class.java)
                myIntent.putExtra("Assessment", assessment)
                myIntent.putExtra("question", "0")
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())


            }

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
            story_view = findViewById(R.id.story_view)
            expected_txt = story_view!!.getText().toString()
            //story_view.setEnabled(false);
        }


        override fun onCancelled(s: String?) {
            super.onCancelled(s)
            reco!!.close()
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)
            story_view!!.background = drawable
            story_view!!.setTextColor(Color.BLACK)

            //story_view.setEnabled(true);
            if (mediaStarted) {
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
                progressBar!!.progress = 0
                mediaStarted = false
            }
            transcriptStarted = false
            if (s.equals("canceled", ignoreCase = true)) {
                Toast.makeText(this@story_assessment, "Internet Connection Failed", Toast.LENGTH_LONG).show()
            } else if (s.equals("no match", ignoreCase = true)) {
                Toast.makeText(this@story_assessment, "Try Again", Toast.LENGTH_LONG).show()
            } else {
                val error_txt = SpeechRecognition.compareTranscript(expected_txt, s)
                //error_count += SpeechRecognition.countError(error_txt);
                //Toast.makeText(view.getContext(), "transcript: \'"+ s +"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), "expected: \'"+expected_txt+"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), error_txt , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), Integer.toString(error_count) , Toast.LENGTH_LONG).show();
                if (error_count > 12) { // if error less than 8 move to story level
                    goToThankYou()
                } else if (s !== "no match") {
                    if (SpeechRecognition.countError(error_txt) > 3 || s!!.split(" ".toRegex()).toTypedArray().size < 2) {
                        if (tries < 1) {
                            tries++ // incremnent tries
                            Toast.makeText(view!!.context, "Try Again!", Toast.LENGTH_LONG).show()
                        } else {
                            //story_words_wrong += error_txt.trim() + ",";
                            if (SpeechRecognition.countError(error_txt) != 0) { // if no erro
                                //words_correct += ","+expected_txt.trim();
                                story_words_wrong += error_txt.trim { it <= ' ' } + ","
                            }
                            error_count += SpeechRecognition.countError(error_txt)
                            nextParagraph(view)
                        }
                    } else {
                        //story_words_wrong += error_txt.trim() + ",";
                        if (SpeechRecognition.countError(error_txt) != 0) { // if no erro
                            //words_correct += ","+expected_txt.trim();
                            story_words_wrong += error_txt.trim { it <= ' ' } + ","
                        }
                        error_count += SpeechRecognition.countError(error_txt)
                        nextParagraph(view)
                    }
                }
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


    fun goToThankYou() { // take to thank you page and grade as paragraph student_activity
        val temp = assessment!!.PARAGRAPH_WORDS_WRONG

        val map = mapOf("LEARNING_LEVEL " to "PARAGRAPH","PARAGRAPH_WORDS_WRONG" to temp + story_words_wrong)

        showProgress(true)
        Constants.assessmentDocumentSnapshot!!.reference.set(map, SetOptions.merge()).addOnSuccessListener {
            showProgress(false)
            val myIntent = Intent(baseContext, thankYou::class.java)
            assessment!!.LEARNING_LEVEL = "PARAGRAPH"
           assessment!!.PARAGRAPH_WORDS_WRONG = temp + story_words_wrong
            //assessment.setSTORY_WORDS_WRONG(story_words_wrong);
            myIntent.putExtra("Assessment", assessment)
            startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())


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