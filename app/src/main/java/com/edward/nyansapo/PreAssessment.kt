package com.nyansapo

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
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
import com.nyansapo.presentation.utils.Constants
import com.nyansapo.presentation.utils.FirebaseUtils
import com.nyansapo.presentation.utils.GlobalData
import com.nyansapo.presentation.utils.assessmentDocumentSnapshot
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_pre_assessment.*
import java.util.concurrent.ExecutionException

class PreAssessment : AppCompatActivity(), View.OnClickListener {


    private val TAG = "PreAssessment"


    lateinit var studentId: String

    // button ui
    var next_button: Button? = null
    var record_button: Button? = null
    var read_button: Button? = null


    var button_toggle: Int? = null

    //


    // Permission
    val REQUEST_PERSMISSION_CODE = 1000

    // Assessment key
    var ASSESSMENT_KEY = "3"

    // img
    var arrow_img: ImageView? = null
    var arrow_animation_leftToRight: Animation? = null
    var arrow_animation_blink: Animation? = null
    var arrow_animation_fadeOut: Animation? = null

    var transcriptStarted = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pre_assessment)
        initProgressBar()
        //setting choosen avatar
        imageView3.setImageResource(GlobalData.avatar)

        studentId=intent.getStringExtra("studentId")
        val bundle = intent.extras
        ASSESSMENT_KEY = bundle.getString("ASSESSMENT_KEY")
        val intent = this.intent
        if (!checkPermissionFromDevice()) requestPermission()

        // assign buttons to xml components
        next_button = findViewById(R.id.next_button)
        record_button = findViewById(R.id.record_button)
        read_button = findViewById(R.id.read_button)


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


    }


    fun arrowBlink() {
        arrow_img!!.startAnimation(arrow_animation_blink)
    }

    fun arrowFadeOut() {
        arrow_img!!.startAnimation(arrow_animation_fadeOut)
    }

    fun goHome(v: View?) {
        val myIntent = Intent(baseContext, MainActivity::class.java)
        startActivity(myIntent)
    }

    fun recordStudent() {

        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(this, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            finish()
        }


        val myIntent = Intent(baseContext, paragraph::class.java)
        val assessment = Assessment() // create new assessment object
        assessment.assessmentKey = ASSESSMENT_KEY // assign proper key
        myIntent.putExtra("Assessment", assessment) //sent next activity
        startActivity(myIntent)

/*        showProgress(true)
        FirebaseUtils.addAssessmentForStudent(programId, groupId, campId, studentId, assessment) {

            it.get().addOnSuccessListener {
                showProgress(false)
                assessmentDocumentSnapshot = it


            }

        }*/


    }

    var drawable: Drawable? = null
    fun Func(v: View?) {

        Log.d(TAG, "Func: started recording")



        if (!transcriptStarted) {
            drawable = read_button!!.background
            val newDrawable = drawable!!.getConstantState().newDrawable().mutate()
            val lightblue = Color.parseColor("#82b6ff") //light blue
            val lightbrown = Color.parseColor("#FFFF00") // bright yellow
            newDrawable.colorFilter = PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY)
            read_button!!.background = newDrawable
            read_button!!.setTextColor(lightbrown)

            SpeechAsync().execute()
            transcriptStarted = true
        }
    }

    override fun onClick(v: View) {


        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(this, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
            finish()
        }




        when (v.id) {
            R.id.read_button ->                 //arrow_img.setVisibility(View.INVISIBLE);
                Func(v)
            R.id.record_button -> Func(v)
            R.id.next_button -> {

                //Toast.makeText(this, instructor_id +" pressed", Toast.LENGTH_LONG).show();
                val myIntent = Intent(baseContext, paragraph::class.java)
                val assessment = Assessment() // create new assessment object
                assessment.assessmentKey = ASSESSMENT_KEY // assign proper key
                myIntent.putExtra("Assessment", assessment) //sent next activity
                //Toast.makeText(this, assessment.toString() +"  "+ assessment.getSTUDENT_ID(), Toast.LENGTH_SHORT).show();
                startActivity(myIntent)


       /*         showProgress(true)
                FirebaseUtils.addAssessmentForStudent(programId, groupId, campId, studentId, assessment) {

                    it.get().addOnSuccessListener {
                        showProgress(false)
                        assessmentDocumentSnapshot = it


                    }

                }
*/

            }
            else -> throw IllegalStateException("Unexpected value")
        }
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

    inner class SpeechAsync : AsyncTask<Void, String?, String?>() {
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
        }



        override fun onCancelled(s: String?) {
            super.onCancelled(s)
            reco!!.close()
        }

        override fun onPostExecute(s: String?) {
            super.onPostExecute(s)

            read_button!!.background = drawable
            read_button!!.setTextColor(Color.BLACK)

            transcriptStarted = false

            if (s.equals("canceled", ignoreCase = true)) {
                Toast.makeText(this@PreAssessment, "Internet Connection Failed", Toast.LENGTH_LONG).show()
            } else if (s.equals("no match", ignoreCase = true)) {
                Toast.makeText(this@PreAssessment, "Try Again", Toast.LENGTH_LONG).show()
            } else {
                val err_txt = SpeechRecognition.compareTranscript("I Live in Kenya", s)
                val count = SpeechRecognition.countError(err_txt)

                recordStudent()

            }
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