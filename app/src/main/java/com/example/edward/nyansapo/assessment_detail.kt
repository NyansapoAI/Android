package com.example.edward.nyansapo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.View
import android.view.ViewGroup.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.edward.nyansapo.presentation.utils.Constants
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class assessment_detail : AppCompatActivity() {
    var literacy_level: TextView? = null
    var para_words_wrong: TextView? = null
    var words_wrong_view: TextView? = null
    var letters_wrong_view: TextView? = null
    var question1: TextView? = null
    var question2: TextView? = null
    var back_button: Button? = null
    var delete_button: Button? = null
    lateinit var student: Student
    lateinit var assessment: Assessment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assessment_detail)

        initProgressBar()

        // find ui elements
        literacy_level = findViewById(R.id.literacy_level_view)
        para_words_wrong = findViewById(R.id.para_wrong_view1)
        words_wrong_view = findViewById(R.id.words_wrong_view1)
        letters_wrong_view = findViewById(R.id.letters_wrong_view1)
        question1 = findViewById(R.id.question1_view1)
        question2 = findViewById(R.id.question2_view1)
        back_button = findViewById(R.id.back_button)
        delete_button = findViewById(R.id.delete_button)

        // get student_activity parcelable object
        val intent = intent
        student = Constants.studentDocumentSnapshot!!.toObject(Student::class.java)!!
        assessment = Constants.assessmentDocumentSnapshot!!.toObject(Assessment::class.java)!!

        // set assessment info into ui
        literacy_level!!.setText(assessment.getLEARNING_LEVEL())
        para_words_wrong!!.setText(assessment.getPARAGRAPH_WORDS_WRONG())
        words_wrong_view!!.setText(assessment.getWORDS_WRONG())
        letters_wrong_view!!.setText(assessment.getLETTERS_WRONG())
        question1!!.setText(assessment.getSTORY_ANS_Q1())
        question2!!.setText(assessment.getSTORY_ANS_Q2())
        //Toast.makeText(this, "Q1"+ assessment.getSTORY_ANS_Q1() + " "+ assessment.getSTORY_ANS_Q2(), Toast.LENGTH_LONG).show();


        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@assessment_detail, student_assessments::class.java)
            startActivity(intent)
            /*
                //startActivity(new Intent(getApplicationContext(), home.class));
                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(assessment_detail.this).toBundle());

                 */
        }


        /*Toast.makeText(this,assessment.getLETTERS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLETTERS_CORRECT(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getWORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getWORDS_CORRECT(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getPARAGRAPH_WORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getSTORY_ANS_Q1(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getSTORY_ANS_Q2(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLEARNING_LEVEL(), Toast.LENGTH_SHORT).show();*/back_button!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@assessment_detail, student_assessments::class.java)
            startActivity(intent)
        })
        delete_button!!.setOnClickListener {
            showProgress(true)
            Constants.assessmentDocumentSnapshot!!.reference.delete().addOnSuccessListener {
                showProgress(false)
                val intent = Intent(this@assessment_detail, student_assessments::class.java)
                startActivity(intent)
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