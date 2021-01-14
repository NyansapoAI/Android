package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.security.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TransferQueue;



public class registerStudent extends AppCompatActivity {

    // Initialize variables
    EditText firstname;
    EditText lastname;
    EditText age;
    EditText gender;
    EditText std_class;
    EditText notes;

    // Initialize database connection
    dataBaseHandler databasehelper;

    // Instructor id
    String instructor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);


        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(), home.class));
                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(registerStudent.this).toBundle());
            }
        });

        // get intent
        Intent intent = this.getIntent();
        instructor_id = intent.getStringExtra("instructor_id");
        //Toast.makeText(getApplicationContext(), instructor_id , Toast.LENGTH_SHORT).show();

        // Assign variables
        firstname = findViewById(R.id.edit_firstname);
        lastname = findViewById(R.id.edit_lastname);
        age = findViewById(R.id.edit_age);
        gender = findViewById(R.id.edit_gender);
        std_class = findViewById(R.id.edit_class);
        notes = findViewById(R.id.edit_notes);

        // Initialize DatabaseHelper
        databasehelper = new dataBaseHandler(registerStudent.this);

    }

    public void startAssessment(View v){
        Intent myIntent = new Intent(getBaseContext(), Begin_Assessment.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void goHome(View v){

        UUID uuid = UUID.randomUUID();
        // Validation for inputs needs to happen before creating student

        if(firstname.getText().toString() == "" || lastname.getText().toString() == ""
        || gender.getText().toString() == "" || notes.getText().toString() == "" || std_class.getText().toString() == "" ){
            Toast.makeText(this, "Provide all fields", Toast.LENGTH_LONG).show();
        }else{
            Student student;
            student = new Student();
            student.setAge((age.getText().toString())); // set age
            student.setFirstname(firstname.getText().toString());
            student.setLastname(lastname.getText().toString());
            student.setGender(gender.getText().toString());
            student.setNotes(notes.getText().toString());
            student.setLearning_level("UNKNOWN");// SET latter
            student.setTimestamp(new Date(System.currentTimeMillis()).toString());
            student.setLocal_id(uuid.toString());
            student.setInstructor_id(instructor_id);
            //student.setInstructor_id("5f39b701b4270100524952ed");
            student.setStd_class(std_class.getText().toString());

            //Toast.makeText(getApplicationContext(), student.getInstructor_id() , Toast.LENGTH_SHORT).show();
            // send student object to database
            try {
                //createStudent(student); // save in cloud
                postStudent(student);
                //String uuid1 =  databasehelper.addStudent(student);
                //Toast.makeText(getApplicationContext(), uuid1,Toast.LENGTH_LONG).show();

                // go to home
                //Intent myIntent = new Intent(getBaseContext(), home.class);
                //myIntent.putExtra("instructor_id", instructor_id);
                //startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            } catch (Error err){
                Toast.makeText(getApplicationContext(), "Could not insert student",Toast.LENGTH_SHORT).show();
            }

        }

    }

    public void postStudent(Student student){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://nyansapoai-api.azurewebsites.net/student/register";
        //String url = "https://localhost:3000/student/register";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                student.setCloud_id(getId(response));
                databasehelper.addStudent(student);
                //Toast.makeText(registerStudent.this,"std"+ getId(response), Toast.LENGTH_LONG).show();
                //Toast.makeText(registerStudent.this, "inst"+ student.getInstructor_id(), Toast.LENGTH_LONG).show();

                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(registerStudent.this).toBundle());
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(registerStudent.this, error.toString() , Toast.LENGTH_LONG).show();
                //databasehelper.addStudent(student);
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("instuctor_id", student.getInstructor_id());
                params.put("firstname", student.getFirstname());
                params.put("lastname", student.getLastname());
                params.put("age", student.getAge());
                params.put("gender", student.getGender());
                params.put("timestamp", student.getTimestamp());
                params.put("learning_level", student.getLearning_level());
                params.put("notes", student.getNotes());
                params.put("std_class",student.getStd_class());
                params.put("instructor_id", student.getInstructor_id());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public String getId(String response){
        String id = response.split(":")[1];
        id = id.replace("}", ""); // remove }
        id = id.replace("\"", ""); // remove "
        return id;
    }

}
