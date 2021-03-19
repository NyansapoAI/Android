package com.example.edward.nyansapo


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.edward.nyansapo.PreAssessment
import com.example.edward.nyansapo.presentation.utils.studentDocumentSnapshot

class SelectAssessment : AppCompatActivity(), View.OnClickListener {
    var button3: Button? = null
    var button4: Button? = null
    var button5: Button? = null
    var button6: Button? = null
    var button7: Button? = null
    var button8: Button? = null
    var button9: Button? = null
    var button10: Button? = null
    var student: Student? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_assessment)
        val intent = intent
        student = studentDocumentSnapshot!!.toObject(Student::class.java)

        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { //startActivity(new Intent(getApplicationContext(), home.class));
            val intent = Intent(this@SelectAssessment, student_assessments::class.java)
             startActivity(intent)
        }


        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        button3 = findViewById(R.id.assessment3_button)
        button4 = findViewById(R.id.assessment4_button)
        button5 = findViewById(R.id.assessment5_button)
        button6 = findViewById(R.id.assessment6_button)
        button7 = findViewById(R.id.assessment7_button)
        button8 = findViewById(R.id.assessment8_button)
        button9 = findViewById(R.id.assessment9_button)
        button10 = findViewById(R.id.assessment10_button)
        button3!!.setOnClickListener(this)
        button4!!.setOnClickListener(this)
        button5!!.setOnClickListener(this)
        button6!!.setOnClickListener(this)
        button7!!.setOnClickListener(this)
        button8!!.setOnClickListener(this)
        button9!!.setOnClickListener(this)
        button10!!.setOnClickListener(this)
        button6!!.setEnabled(false)
        button7!!.setEnabled(false)
        button8!!.setEnabled(false)
        button9!!.setEnabled(false)
        button10!!.setEnabled(false)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.assessment3_button -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("ASSESSMENT_KEY", "3")
                myIntent.putExtra("studentId", student!!.id)
                startActivity(myIntent)
            }
            R.id.assessment4_button -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("studentId", student!!.id)
                myIntent.putExtra("ASSESSMENT_KEY", "4")
                startActivity(myIntent)
            }
            R.id.assessment5_button -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("studentId", student!!.id)
                myIntent.putExtra("ASSESSMENT_KEY", "5")
                startActivity(myIntent)
            }
            R.id.assessment6_button -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("studentId", student!!.id)
                myIntent.putExtra("ASSESSMENT_KEY", "6")
                startActivity(myIntent)
            }
            R.id.assessment7_button -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("studentId", student!!.id)
                myIntent.putExtra("ASSESSMENT_KEY", "7")
                startActivity(myIntent)
            }
            R.id.assessment8_button -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("studentId", student!!.id)
                myIntent.putExtra("ASSESSMENT_KEY", "8")
                startActivity(myIntent)
            }
            R.id.assessment9_button -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("studentId", student!!.id)
                myIntent.putExtra("ASSESSMENT_KEY", "9")
                startActivity(myIntent)
            }
            R.id.assessment10_button -> {
                val myIntent = Intent(baseContext, PreAssessment::class.java)
                myIntent.putExtra("studentId", student!!.id)
                myIntent.putExtra("ASSESSMENT_KEY", "10")
                startActivity(myIntent)
            }
            else -> {
            }
        }
    }
}