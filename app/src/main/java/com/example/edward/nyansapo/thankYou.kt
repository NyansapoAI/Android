package com.example.edward.nyansapo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

        //Intent intent = getIntent();
        //assessment = intent.getParcelableExtra("Assessment");
        val intent = intent
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment")
        /*
        Toast.makeText(this, assessment.getPARAGRAPH_WORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, assessment.getSTORY_ANS_Q1(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, assessment.getSTORY_ANS_Q2(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLEARNING_LEVEL(),Toast.LENGTH_SHORT).show();*/
        storeAssessment() // will do on another thread
        done_button!!.setOnClickListener { v -> nextAssessment(v) }
    }

    fun nextAssessment(w: View?) {
        val myIntent = Intent(baseContext, home::class.java)
        //Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(myIntent)
    }

    fun storeAssessment() {
        //dataBaseHandler.addAssessment(assessment);

        // first update in cloud and if successful update locally
        //dataBaseHandler.addAssessment(assessment);
        postAssessment(assessment)
        updateStudentLearningLevel(assessment)
        //dataBaseHandler.updateStudentLevel(assessment.getSTUDENT_ID(), assessment.getLEARNING_LEVEL());
    }

    fun updateStudentLearningLevel(assessment: Assessment?) {

        showProgress(true)
        val map2 = mapOf("learningLevel" to assessment?.learningLevel)
        studentDocumentSnapshot!!.reference.set(map2, SetOptions.merge()).addOnSuccessListener {
            showProgress(false)

            Log.d(TAG, "updateStudentLearningLevel: finished updating student learning level")
        }

    }

    fun postAssessment(assessment: Assessment?) {
        showProgress(true)
     assessmentDocumentSnapshot!!.reference.set(assessment!!).addOnSuccessListener {
            showProgress(false)
         Log.d(TAG, "postAssessment: finished updating assessment")
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