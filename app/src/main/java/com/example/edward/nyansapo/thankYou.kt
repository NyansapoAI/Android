package com.example.edward.nyansapo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.edward.nyansapo.presentation.ui.main.MainActivity2
import com.example.edward.nyansapo.presentation.utils.assessmentDocumentSnapshot
import com.example.edward.nyansapo.presentation.utils.studentDocumentSnapshot
import com.google.firebase.firestore.SetOptions

class thankYou : AppCompatActivity() {

             private  val TAG="thankYou"

    var done_button: Button? = null
    var assessment: Assessment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thank_you)
        initProgressBar()
        done_button = findViewById(R.id.done_button)

        val intent = intent
        assessment = intent.getParcelableExtra("Assessment")
        Log.d(TAG, "onCreate: assessment:$assessment")
        done_button!!.setOnClickListener { doneBtnClicked() }

        storeAssessment()
     }

    fun doneBtnClicked() {
        val myIntent = Intent(baseContext, MainActivity2::class.java)
        startActivity(myIntent)
        finish()
    }

    fun storeAssessment() {
        Log.d(TAG, "storeAssessment: ")
        updateStudentLearningLevel(assessment)
     }

    fun updateStudentLearningLevel(assessment: Assessment?) {

        showProgress(true)
        val map2 = mapOf("learningLevel" to assessment?.learningLevel)
        studentDocumentSnapshot!!.reference.set(map2, SetOptions.merge()).addOnSuccessListener {

            Log.d(TAG, "updateStudentLearningLevel: finished updating student learning level")

            assessmentDocumentSnapshot!!.reference.set(assessment!!).addOnSuccessListener {
                showProgress(false)

                Log.d(TAG, "postAssessment: finished updating assessment")
            }
         }

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