package com.example.edward.nyansapo

import android.Manifest
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.view.ViewGroup.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.edward.nyansapo.MainActivity
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit

class PreAssessment : AppCompatActivity(), View.OnClickListener {
    // button ui
    var next_button: Button? = null
    var record_button: Button? = null
    var read_button: Button? = null

    // Audio Recording Settings
    var outputFile: String? = null
    var myAudioRecorder: MediaRecorder? = null

    //
    var button_toggle: Int? = null

    //
    var mediaRecorder: MediaRecorder? = null
    var mediaPlayer: MediaPlayer? = null

    // Permission
    val REQUEST_PERSMISSION_CODE = 1000
    var filename = "/dev/null"

    // Asyn stuff
    var speechAsync: SpeechAsync? = null

    // Assessment key
    var ASSESSMENT_KEY = "3"

    // img
    var arrow_img: ImageView? = null
    var arrow_animation_leftToRight: Animation? = null
    var arrow_animation_blink: Animation? = null
    var arrow_animation_fadeOut: Animation? = null

    // progress bar
    var progressBar: ProgressBar? = null

    /// Control variables or code locks
    var mediaStarted = false
    var transcriptStarted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_assessment)
        initProgressBar()
        val bundle = intent.extras
        ASSESSMENT_KEY = bundle.getString("ASSESSMENT_KEY")
        val intent = this.intent
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();

        //Toast.makeText(getApplicationContext(), "Click on the record button and read!", Toast.LENGTH_LONG).show();

        // Request Runtime Permission
        if (!checkPermissionFromDevice()) requestPermission()

        // assign buttons to xml components
        next_button = findViewById(R.id.next_button)
        record_button = findViewById(R.id.record_button)
        read_button = findViewById(R.id.read_button)


        // progressbar
        progressBar = findViewById(R.id.progressBar2)

        // set onclick listeners
        next_button!!.setOnClickListener(this)
        record_button!!.setOnClickListener(this)
        read_button!!.setOnClickListener(this)

        // button toggle
        button_toggle = 1 // 1 will record if button is clicked and -1 will stop if button is clicked

        // Animation stuff
        arrow_img = findViewById(R.id.arrow_img)
        arrow_animation_leftToRight = AnimationUtils.loadAnimation(this, R.anim.lefttoright)
        arrow_animation_leftToRight!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                arrowBlink()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        arrow_img!!.startAnimation(arrow_animation_leftToRight)
        arrow_animation_blink = AnimationUtils.loadAnimation(this, R.anim.blink_anim)
        arrow_animation_blink!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                arrowFadeOut()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        arrow_animation_fadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout)


        //progressBar
        progressBar!!.setMax(15000)
        progressBar!!.setProgress(0)
    }

    fun arrowBlink() {
        arrow_img!!.startAnimation(arrow_animation_blink)
    }

    fun arrowFadeOut() {
        arrow_img!!.startAnimation(arrow_animation_fadeOut)
    }

    fun goHome(v: View?) {
        val myIntent = Intent(baseContext, MainActivity::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun recordStudent(v: View?) {
        val myIntent = Intent(baseContext, paragraph::class.java)
        val assessment = Assessment() // create new assessment object
        assessment.assessmenT_KEY = ASSESSMENT_KEY // assign proper key

        showProgress(true)
        FirebaseUtils.assessmentsCollection.add(assessment).addOnSuccessListener {

            it.get().addOnSuccessListener {
                showProgress(false)
                Constants.assessmentDocumentSnapshot = it

                myIntent.putExtra("Assessment", assessment) //sent next activity
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

            }

        }


    }

    var txt: String? = null
    var drawable: Drawable? = null
    fun Func(v: View?) {
        startRecording()
        val exec = ScheduledThreadPoolExecutor(1)
        exec.scheduleAtFixedRate({ // code to execute repeatedly
            val num = amplitudeEMA
            progressBar!!.progress = num.toInt()
        }, 0, 100, TimeUnit.MILLISECONDS)
        if (!transcriptStarted) {
            drawable = read_button!!.background
            val newDrawable = drawable!!.getConstantState().newDrawable().mutate()
            //read_button.setBackgroundColor(Color.BLUE);
            val lightblue = Color.parseColor("#82b6ff") //light blue
            //int lightblue = Color.parseColor("#8B4513");

            //int lightbrown = Color.parseColor("#eecab1"); // light brown
            //int lightbrown = Color.parseColor("#7ab121"); // Green
            val lightbrown = Color.parseColor("#FFFF00") // bright yellow
            newDrawable.colorFilter = PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY)
            read_button!!.background = newDrawable
            read_button!!.setTextColor(lightbrown)
            val speechAsync: SpeechAsync = SpeechAsync()
            speechAsync.execute(v)
            transcriptStarted = true
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.read_button ->                 //arrow_img.setVisibility(View.INVISIBLE);
                Func(v)
            R.id.record_button -> Func(v)
            R.id.next_button -> {

                //Toast.makeText(this, instructor_id +" pressed", Toast.LENGTH_LONG).show();
                val myIntent = Intent(baseContext, paragraph::class.java)
                val assessment = Assessment() // create new assessment object
                assessment.assessmenT_KEY = ASSESSMENT_KEY // assign proper key


                showProgress(true)
                FirebaseUtils.assessmentsCollection.add(assessment).addOnSuccessListener {

                    it.get().addOnSuccessListener {
                        showProgress(false)
                        Constants.assessmentDocumentSnapshot = it

                        myIntent.putExtra("Assessment", assessment) //sent next activity
                         //Toast.makeText(this, assessment.toString() +"  "+ assessment.getSTUDENT_ID(), Toast.LENGTH_SHORT).show();
                        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())

                    }

                }


                   }
            else -> throw IllegalStateException("Unexpected value")
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

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO), REQUEST_PERSMISSION_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        when (requestCode) {
            REQUEST_PERSMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show() else Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionFromDevice(): Boolean {
        val write_external_storage_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
        val internet_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
        return write_external_storage_result == PackageManager.PERMISSION_GRANTED && record_audio_result == PackageManager.PERMISSION_GRANTED && internet_permission == PackageManager.PERMISSION_GRANTED
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
        override fun onPreExecute() {
            //read_button.setEnabled(false);
            //record_button.setEnabled(false);
            super.onPreExecute()
            config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion)
            config!!.setEndpointId(endpoint)
        }



        override fun onCancelled(s: String?) {
            super.onCancelled(s)
            reco!!.close()
        }

        override fun onPostExecute(s: String?) {
            read_button!!.background = drawable
            read_button!!.setTextColor(Color.BLACK)
            if (mediaStarted) {
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
                progressBar!!.progress = 0
                mediaStarted = false
            }
            transcriptStarted = false

            //read_button.setEnabled(true);
            //record_button.setEnabled(true);
            super.onPostExecute(s)
            if (s.equals("canceled", ignoreCase = true)) {
                Toast.makeText(this@PreAssessment, "Internet Connection Failed", Toast.LENGTH_LONG).show()
            } else if (s.equals("no match", ignoreCase = true)) {
                Toast.makeText(this@PreAssessment, "Try Again", Toast.LENGTH_LONG).show()
            } else {
                val err_txt = SpeechRecognition.compareTranscript("I Live in Kenya", s)
                val count = SpeechRecognition.countError(err_txt)
                if (count < 2) {
                    recordStudent(view)
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
            return null  }
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

              llParam = LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                      LayoutParams.WRAP_CONTENT)
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