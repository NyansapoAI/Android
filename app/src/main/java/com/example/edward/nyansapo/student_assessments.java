package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class student_assessments extends AppCompatActivity implements AssessmentCustomViewAdapter.OnAssessmentListener,  SelectAssessmentModal.AssessmentModalListener {

    dataBaseHandler databaseHandler;
    ArrayList arrayList;
    ArrayAdapter arrayAdapter;

    ArrayList<Assessment> assessments;
    Student student;

    FloatingActionButton btAdd;


    String instructor_id;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings: {
                Intent myIntent = new Intent(getBaseContext(), studentSettings.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                myIntent.putExtra("student",student);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(student_assessments.this).toBundle());
                return true;
            }
            case R.id.add_assessment: {
                addAssessment();
                return true;
            }
            case R.id.analytics: {
                Intent intent = new Intent(student_assessments.this, studentDetails.class);
                intent.putExtra("instructor_id", instructor_id);
                intent.putExtra("student",student);
                startActivity(intent);
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assessments);

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
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(student_assessments.this).toBundle());
            }
        });

        // get student
        Intent intent = getIntent();
        student = intent.getParcelableExtra("student");
        instructor_id = intent.getStringExtra("instructor_id");

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        btAdd = findViewById(R.id.bt_add);


        // Initialize DatabaseHelper
        databaseHandler = new dataBaseHandler(this);

        btAdd = findViewById(R.id.bt_add);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAssessment();
            }
        });


        assessments = new ArrayList<Assessment>();
        getAssessments(); // populate students ArrayList
        //Toast.makeText(this,assessments.toString(), Toast.LENGTH_LONG).show();
        AssessmentCustomViewAdapter assessmentCustomViewAdapter = new AssessmentCustomViewAdapter(this, assessments, this::OnAssessmentClick);
        recyclerView.setAdapter(assessmentCustomViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /*Assessment assessment =  assessments.get(0);
        Toast.makeText(this,assessment.getLETTERS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLETTERS_CORRECT(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getWORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getWORDS_CORRECT(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getPARAGRAPH_WORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getSTORY_ANS_Q1(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getSTORY_ANS_Q2(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLEARNING_LEVEL(), Toast.LENGTH_SHORT).show();*/

    }

    private void getAssessments() {
        assessments =databaseHandler.getAllStudentAssessment(student.getCloud_id());
        //Toast.makeText(this, student.getLocal_id(), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, student.getCloud_id(), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, student.toString(), Toast.LENGTH_LONG).show();
        //Toast.makeText(this, Integer.toString(assessments.size()), Toast.LENGTH_LONG).show();

    }

    private void addAssessment() {
        /*
        Intent intent = new Intent(student_assessments.this, SelectAssessment.class);
        intent.putExtra("instructor_id", instructor_id);
        intent.putExtra("student_id",student.getLocal_id());
        intent.putExtra("student", student);
        //intent.putExtra("assessment", assessments.get(position));
        startActivity(intent);

         */

        SelectAssessmentModal selectAssessmentModal = new SelectAssessmentModal();
        selectAssessmentModal.show(getSupportFragmentManager(),"Select Assessment Modal");
    }

    @Override
    public void OnAssessmentClick(int position) {
        //students.get(position);
        Intent intent = new Intent(student_assessments.this, assessment_detail.class);
        intent.putExtra("instructor_id", instructor_id);
        intent.putExtra("student_id",student.getLocal_id());
        intent.putExtra("student", student);
        intent.putExtra("assessment", assessments.get(position));
        startActivity(intent);
    }

    public void deleteStudent(){

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "https://nyansapoai-api.azurewebsites.net/student/";
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //dataBaseHandler.updateStudentLevel(assessment.getSTUDENT_ID(), assessment.getLEARNING_LEVEL());
                Toast.makeText(student_assessments.this, "Student Deleted",Toast.LENGTH_LONG).show();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //dataBaseHandler.updateStudentLevel(assessment.getSTUDENT_ID(), assessment.getLEARNING_LEVEL());
            }
        }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("student_id", student.getCloud_id());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    @Override
    public void onButtonClicked(String text) {

        //Toast.makeText(this,text, Toast.LENGTH_SHORT).show();

        switch (text){
            case "assessment_3":{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","3");
                myIntent.putExtra("student_id",student.getCloud_id());
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            case "assessment_4":{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","4");
                myIntent.putExtra("student_id",student.getCloud_id());
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            case "assessment_5":{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","5");
                myIntent.putExtra("student_id",student.getCloud_id());
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            default:{

            }
        }

    }
}