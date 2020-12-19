package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class studentSettings extends AppCompatActivity {

    // Initialize variables
    EditText firstname;
    EditText lastname;
    EditText age;
    EditText gender;
    EditText std_class;
    EditText notes;

    // database
    dataBaseHandler databasehelper;

    // buttons
    Button update, delete;

    // student
    Student student;
    String instructor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_settings);


        // Assign variables
        firstname = findViewById(R.id.edit_firstname);
        lastname = findViewById(R.id.edit_lastname);
        age = findViewById(R.id.edit_age);
        gender = findViewById(R.id.edit_gender);
        std_class = findViewById(R.id.edit_class);
        notes = findViewById(R.id.edit_notes);

        // buttons
        update = findViewById(R.id.std_update);
        delete = findViewById(R.id.std_delete);

        // database
        databasehelper = new dataBaseHandler(this);

        Intent intent = getIntent();
        student = intent.getParcelableExtra("student");
        instructor_id = intent.getStringExtra("instructor_id");

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(studentSettings.this, "Under Development", Toast.LENGTH_LONG).show();
                databasehelper.updateStudent(student.getCloud_id(),firstname.getText().toString(), lastname.getText().toString(), age.getText().toString(), gender.getText().toString(), notes.getText().toString(), std_class.getText().toString());
                updateStudent();
                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(studentSettings.this).toBundle());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databasehelper.deleteStudent(student.getCloud_id());
                deleteStudent(student.getCloud_id());
                //Toast.makeText(studentSettings.this, student.getCloud_id(), Toast.LENGTH_LONG).show();
                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(studentSettings.this).toBundle());

            }
        });

        // do on some thread
        populateInfo();

    }

    public void updateStudent(){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://nyansapoai-api.azurewebsites.net/student/update";

        //StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,  new com.android.volley.Response.Listener<String>() )



        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //databasehelper.deleteStudent(student.getCloud_id());
                //Toast.makeText(studentSettings.this, "Student Deleted "+ response, Toast.LENGTH_SHORT).show();
                //Intent myIntent = new Intent(getBaseContext(), home.class);
                //myIntent.putExtra("instructor_id", instructor_id); // its id for now
                //startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(studentSettings.this).toBundle());
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(studentSettings.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_id", student.getCloud_id());
                params.put("firstname", firstname.getText().toString() );
                params.put("lastname", lastname.getText().toString());
                params.put("age", age.getText().toString());
                params.put("gender", gender.getText().toString());
                params.put("std_class", std_class.getText().toString());
                params.put("notes", notes.getText().toString());
                return params;

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);


    }


    public void deleteStudent(String student_id){
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "https://nyansapoai-api.azurewebsites.net/student/delete";

            //StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,  new com.android.volley.Response.Listener<String>() )



            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //databasehelper.deleteStudent(student.getCloud_id());
                    //Toast.makeText(studentSettings.this, "Student Deleted "+ response, Toast.LENGTH_SHORT).show();
                    //Intent myIntent = new Intent(getBaseContext(), home.class);
                    //myIntent.putExtra("instructor_id", instructor_id); // its id for now
                    //startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(studentSettings.this).toBundle());
                }
            }, new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(studentSettings.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                protected Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("student_id", student_id);
                    return params;

                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);

    }





    public void populateInfo(){
        firstname.setText(student.getFirstname());
        lastname.setText(student.getLastname());
        age.setText(student.getAge());
        gender.setText(student.getGender());
        std_class.setText(student.getStd_class());
        notes.setText(student.getNotes());
    }
}