package com.nyansapo

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
import com.android.volley.*
import com.nyansapo.home
import com.nyansapo.presentation.utils.studentDocumentSnapshot
import java.util.*

class studentSettings : AppCompatActivity() {
    // Initialize variables
    var firstname: EditText? = null
    var lastname: EditText? = null
    var age: EditText? = null
    var gender: EditText? = null
    var std_class: EditText? = null
    var notes: EditText? = null


    // buttons
    var update: Button? = null
    var delete: Button? = null

    // student_activity
    var student: Student? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_settings)
        initProgressBar()
        val intent = intent
        student = studentDocumentSnapshot?.toObject(Student::class.java)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { //startActivity(new Intent(getApplicationContext(), home.class));
            val intent = Intent(this@studentSettings, student_assessments::class.java)
            startActivity(intent)
        }


        // Assign variables
        firstname = findViewById(R.id.edit_firstname)
        lastname = findViewById(R.id.edit_lastname)
        age = findViewById(R.id.edit_age)
        gender = findViewById(R.id.edit_gender)
        std_class = findViewById(R.id.edit_class)
        notes = findViewById(R.id.edit_notes)

        // buttons
        update = findViewById(R.id.std_update)
        delete = findViewById(R.id.std_delete)

        update!!.setOnClickListener(View.OnClickListener { //Toast.makeText(studentSettings.this, "Under Development", Toast.LENGTH_LONG).show();


            val student = Student()
            student.firstname=firstname!!.getText().toString()
            student.lastname=lastname!!.getText().toString()
            student.age=age!!.getText().toString()
            student.gender=gender!!.getText().toString()
            student.notes=notes!!.getText().toString()
            student.std_class=std_class!!.getText().toString()



            updateStudent(student) {
                val myIntent = Intent(baseContext, home::class.java)
                startActivity(myIntent)

            }
        })
        delete!!.setOnClickListener(View.OnClickListener {
            deleteStudent {
                //Toast.makeText(studentSettings.this, student_activity.getCloud_id(), Toast.LENGTH_LONG).show();
                val myIntent = Intent(baseContext, home::class.java)
                startActivity(myIntent)

            }
        })

        // do on some thread
        populateInfo()
    }

    fun updateStudent(student: Student, onComplete: () -> Unit) {
        showProgress(true)
        studentDocumentSnapshot?.reference?.set(student)?.addOnSuccessListener {
            showProgress(false)
            onComplete()

        }
    }

    fun deleteStudent(onComplete: () -> Unit) {
        showProgress(true)
       studentDocumentSnapshot!!.reference.delete().addOnSuccessListener {
            showProgress(false)
            onComplete()
        }

    }

    fun populateInfo() {
        firstname!!.setText(student!!.firstname)
        lastname!!.setText(student!!.lastname)
        age!!.setText(student!!.age)
        gender!!.setText(student!!.gender)
        std_class!!.setText(student!!.std_class)
        notes!!.setText(student!!.notes)
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