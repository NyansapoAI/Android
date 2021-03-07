package com.example.edward.nyansapo

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_register_student.*
import java.util.*

class registerStudent : AppCompatActivity() {
    // Initialize variables
    var firstname: EditText? = null
    var lastname: EditText? = null
    var age: EditText? = null
    var gender: EditText? = null
    var std_class: EditText? = null
    var notes: EditText? = null

    // Instructor id
    var instructor_id: String? = null

    // Progress bar
    var progressBar: loading_progressBar? = null
    var network_lock = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_student)


        // toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener { //startActivity(new Intent(getApplicationContext(), home.class));
            val myIntent = Intent(baseContext, home::class.java)
            startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this@registerStudent).toBundle())
        }

        // get intent
        val intent = this.intent
        instructor_id = FirebaseUtils.instructor_id
        //Toast.makeText(getApplicationContext(), instructor_id , Toast.LENGTH_SHORT).show();

        // Assign variables
        firstname = findViewById(R.id.edit_firstname)
        lastname = findViewById(R.id.edit_lastname)
        age = findViewById(R.id.edit_age)
        gender = findViewById(R.id.edit_gender)
        std_class = findViewById(R.id.edit_class)
        notes = findViewById(R.id.edit_notes)


        // progress bar
        network_lock = 0
        progressBar = loading_progressBar(this@registerStudent)
        create_button.setOnClickListener(View.OnClickListener { view ->
            addStudentToDatabase(view)

        })
    }

    fun startAssessment(v: View?) {
        val myIntent = Intent(baseContext, Begin_Assessment::class.java)
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
    }

    fun addStudentToDatabase(v: View?) {
        progressBar!!.showDialog()
        val uuid = UUID.randomUUID()
        // Validation for inputs needs to happen before creating student_activity
        if (firstname!!.text.toString() === "" || lastname!!.text.toString() === "" || gender!!.text.toString() === "" || notes!!.text.toString() === "" || std_class!!.text.toString() === "") {
            Toasty.error(this, "Provide all fields", Toast.LENGTH_LONG).show()
        } else {
            val student: Student
            student = Student()
            student.setAge(age!!.text.toString()) // set age
            student.setFirstname(firstname!!.text.toString())
            student.setLastname(lastname!!.text.toString())
            student.setGender(gender!!.text.toString())
            student.setNotes(notes!!.text.toString())
            student.setLearning_level("UNKNOWN") // SET latter
            student.setTimestamp(Date(System.currentTimeMillis()).toString())
            student.setInstructor_id(instructor_id)
            //student_activity.setInstructor_id("5f39b701b4270100524952ed");
            student.setStd_class(std_class!!.text.toString())

            //Toast.makeText(getApplicationContext(), student_activity.getInstructor_id() , Toast.LENGTH_SHORT).show();
            // send student_activity object to database
            try {
                //createStudent(student_activity); // save in cloud
                postStudent(student)
                //String uuid1 =  databasehelper.addStudent(student_activity);
                //Toast.makeText(getApplicationContext(), uuid1,Toast.LENGTH_LONG).show();

                // go to home
                //Intent myIntent = new Intent(getBaseContext(), home.class);
                //myIntent.putExtra("instructor_id", instructor_id);
                //startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } catch (err: Error) {
                Toasty.error(applicationContext, "Could not insert student_activity", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun postStudent(student: Student) {


        FirebaseUtils.studentsCollection.add(student).addOnSuccessListener {
            Toasty.success(this, "Success adding student").show()

            val myIntent = Intent(baseContext, student_assessments::class.java)
            myIntent.putExtra("instructor_id", instructor_id)
            myIntent.putExtra("student_activity", student)
            startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this@registerStudent).toBundle())


        }.addOnFailureListener {
            Toast.makeText(this@registerStudent, it.toString(), Toast.LENGTH_LONG).show()

        }


    }}
