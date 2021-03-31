package com.nyansapo

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.nyansapo.presentation.utils.FirebaseUtils
import java.util.*

class RegisterTeacher : AppCompatActivity() {
    // declare views
    var firstname: EditText? = null
    var lastname: EditText? = null
    var email: EditText? = null
    var password1: EditText? = null
    var password2: EditText? = null

    // progress_bar
    var network_lock = 0
    lateinit var register: Button
    var progressBar: loading_progressBar? = null

    // Control vall
    var success = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_teacher)
        initProgressBar()
        // Initialize views
        firstname = findViewById(R.id.firstname)
        lastname = findViewById(R.id.lastname)
        email = findViewById(R.id.email)
        password1 = findViewById(R.id.password1)
        password2 = findViewById(R.id.password2)
        register = findViewById(R.id.register_teacher)

        // progress bar
        network_lock = 0
        progressBar = com.nyansapo.loading_progressBar(this@RegisterTeacher)
        register.setOnClickListener(View.OnClickListener { view ->
            if (network_lock == 0) {
                startSelector(view)
            } else {
                Toast.makeText(this@RegisterTeacher, "Registering Instructor Wait ...", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun startSelector(v: View?) {
        val instructor: Instructor = Instructor()

        // populate instructor object
        instructor.setEmail(email!!.text.toString())
        instructor.setFirstname(firstname!!.text.toString())
        instructor.setLastname(lastname!!.text.toString())

        postInstructor(instructor)
    }

    fun postInstructor(instructor: Instructor) {

        showProgress(true)
        FirebaseUtils.saveInstructor(instructor) {
            showProgress(false)
            val myIntent = Intent(baseContext, home::class.java)
            startActivity(myIntent)
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