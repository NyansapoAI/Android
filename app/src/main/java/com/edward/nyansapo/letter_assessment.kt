package com.nyansapo


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.media.MediaRecorder
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.view.ViewGroup.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nyansapo.presentation.utils.GlobalData
import com.microsoft.cognitiveservices.speech.ResultReason
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult
import com.microsoft.cognitiveservices.speech.SpeechRecognizer
import kotlinx.android.synthetic.main.activity_index.*
import kotlinx.android.synthetic.main.activity_letter_assessment.*
import kotlinx.android.synthetic.main.activity_pre_assessment.*
import java.io.File
import java.util.*
import java.util.concurrent.ExecutionException

class letter_assessment : AppCompatActivity() {

    private val TAG = "letter_assessment"

    private val RC_PERMISSION=8
    lateinit var file: File
    lateinit var recorder: MediaRecorder


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

        //setting choosen avatar
        imageView4.setImageResource(GlobalData.avatar)
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
        assessment_card!!.setText(letterList[0])

        // on click listeners
        assessment_card!!.setOnClickListener {
            recordStudent()
        }

        record_button!!.setOnClickListener { checkIfWeHavePermissions() }
    }

    private fun checkIfWeHavePermissions() {
        Log.d(TAG, "checkIfWeHavePermissions: ")

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.RECORD_AUDIO), RC_PERMISSION)
            Log.d(TAG, "checkIfWeHavePermissions: permission not available")
        } else {
            Log.d(TAG, "checkIfWeHavePermissions: permissions available")
            recordStudent()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: ")

        if (requestCode == RC_PERMISSION && grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: permission granted")

            recordStudent()
        } else {
            Log.d(TAG, "onRequestPermissionsResult: permission denied")
        }


    }
    fun changeLetter() {
        Log.d(TAG, "changeLetter: changing letter")
        Log.d(TAG, "changeLetter: error_count:$error_count")
        stopVoiceRecording()

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
            val newDrawable = drawable!!.getConstantState().newDrawable().mutate()

            val lightblue = Color.parseColor("#8B4513")
            val lightbrown = Color.parseColor("#7ab121") // Green

            newDrawable.colorFilter = PorterDuffColorFilter(lightblue, PorterDuff.Mode.MULTIPLY)

            assessment_card!!.background = newDrawable
            assessment_card!!.setTextColor(lightbrown)

            SpeechAsync().execute()
            transcriptStarted = true
        }

    }
    private fun startVoiceRecording() {
        Log.d(TAG, "startVoiceRecording: Environment.getexternalStorageDirectory():${Environment.getExternalStorageDirectory()}")
        recorder = MediaRecorder()
        val status = Environment.getExternalStorageState();
        if (status.equals("mounted")) {
            Log.d(TAG, "startVoiceRecording: sd card mounted")
            val timeStamp = Calendar.getInstance().time.time
            val directory = File(Environment.getExternalStorageDirectory().absolutePath + "/nyansapo_recording/letters")
            directory.mkdirs()
            file = File(directory, "$timeStamp.wav")
            file.createNewFile()

            Log.d(TAG, "startVoiceRecording: file path:${file.absolutePath}")
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(file.absolutePath)
            recorder.prepare()
            recorder.start()
        } else {
            Log.d(TAG, "startVoiceRecording: sd card not mounted")

        }

    }

    private fun stopVoiceRecording() {
        Log.d(TAG, "stopRecordingVoice: letter_count:$letter_count")
        recorder.stop()
        recorder.release()


        when (letter_count) {
            0 -> {
                Log.d(TAG, "stopRecordingVoice:chooser letter_count:0")

                GlobalData.assessmentRecording.letter0 = file.absolutePath
            }
            1 -> {
                Log.d(TAG, "stopRecordingVoice:chooser letter_count:1")

                GlobalData.assessmentRecording.letter1 = file.absolutePath
            }
            2 -> {
                Log.d(TAG, "stopRecordingVoice:chooser letter_count:2")

                GlobalData.assessmentRecording.letter2 = file.absolutePath
            }
            3 -> {
                Log.d(TAG, "stopRecordingVoice:chooser letter_count:3")

                GlobalData.assessmentRecording.letter3 = file.absolutePath
            }
            4 -> {
                Log.d(TAG, "stopRecordingVoice:chooser letter_count:4")

                GlobalData.assessmentRecording.letter4 = file.absolutePath
            }
            5 -> {
                Log.d(TAG, "stopRecordingVoice:chooser letter_count:5")

                GlobalData.assessmentRecording.letter5 = file.absolutePath
            }
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
            startVoiceRecording()
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

        val myIntent = Intent(baseContext, thankYou::class.java)
                assessment!!.letterCorrect = letters_correct
                assessment!!.lettersWrong = letters_wrong
                assessment!!.learningLevel = "LETTER"
                myIntent.putExtra("Assessment", assessment)
                startActivity(myIntent)
                finish()

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