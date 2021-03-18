package com.example.edward.nyansapo


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.edward.nyansapo.presentation.utils.Constants
import com.example.edward.nyansapo.presentation.utils.FirebaseUtils
import com.example.edward.nyansapo.presentation.utils.STUDENT_ID
import com.example.edward.nyansapo.presentation.utils.studentDocumentSnapshot
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
            startActivity(myIntent)
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
        startActivity(myIntent)
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
            student.age=age!!.text.toString()// set age
            student.firstname=firstname!!.text.toString()
            student.lastname=lastname!!.text.toString()
            student.gender=gender!!.text.toString()
            student.notes=notes!!.text.toString()
            student.learningLevel="UNKNOWN" // SET latter
            student.timestamp=Date(System.currentTimeMillis()).toString()
            student.instructor_id=instructor_id
            //student_activity.setInstructor_id("5f39b701b4270100524952ed");
            student.std_class=std_class!!.text.toString()

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


        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)

        val programId = sharedPreferences.getString(Constants.KEY_PROGRAM_ID, null)
        val groupId = sharedPreferences.getString(Constants.KEY_GROUP_ID, null)
        val campId = sharedPreferences.getString(Constants.KEY_CAMP_ID, null)
        val campPos = sharedPreferences.getInt(Constants.CAMP_POS, -1)

        if (campPos == -1) {
            Toasty.error(this, "Please First create A camp before coming to this page", Toasty.LENGTH_LONG).show()
           onBackPressed()
        }


        FirebaseUtils.addStudentsToCamp(programId, groupId, campId, student) {
            Toasty.success(this, "Success adding student").show()

            it.get().addOnSuccessListener {
                studentDocumentSnapshot = it
                val myIntent = Intent(baseContext, student_assessments::class.java)
                myIntent.putExtra(STUDENT_ID, it.id)
                myIntent.putExtra("instructor_id", instructor_id)
                myIntent.putExtra("student_activity", student)
                startActivity(myIntent)
            }

        }

    }}
