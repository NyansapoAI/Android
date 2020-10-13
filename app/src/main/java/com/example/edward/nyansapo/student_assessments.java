package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class student_assessments extends AppCompatActivity implements AssessmentCustomViewAdapter.OnAssessmentListener {

    dataBaseHandler databaseHandler;
    ArrayList arrayList;
    ArrayAdapter arrayAdapter;

    ArrayList<Assessment> assessments;
    Student student;

    FloatingActionButton btAdd;
    Button cumulative_progress, delete_button, back_button;

    String instructor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_assessments);

        // get student
        Intent intent = getIntent();
        student = intent.getParcelableExtra("student");
        instructor_id = intent.getStringExtra("instructor_id");

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        btAdd = findViewById(R.id.bt_add);
        cumulative_progress = findViewById(R.id.cumulative_progress);
        delete_button = findViewById(R.id.delete_button);
        back_button = findViewById(R.id.back_button);

        // Initialize DatabaseHelper
        databaseHandler = new dataBaseHandler(this);

        btAdd = findViewById(R.id.bt_add);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAssessment(v);
            }
        });

        cumulative_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(student_assessments.this, studentDetails.class);
                intent.putExtra("instructor_id", instructor_id);
                intent.putExtra("student",student);;
                startActivity(intent);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(student_assessments.this).toBundle());
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseHandler.deleteStudent(student.getLocal_id());
                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(student_assessments.this).toBundle());
                //Toast.makeText(student_assessments.this, "Deleted",Toast.LENGTH_SHORT).show();
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
        assessments =databaseHandler.getAllStudentAssessment(student.getLocal_id());
    }

    private void addAssessment(View v) {
        Intent intent = new Intent(student_assessments.this, SelectAssessment.class);
        intent.putExtra("instructor_id", instructor_id);
        intent.putExtra("student_id",student.getLocal_id());
        intent.putExtra("student", student);
        //intent.putExtra("assessment", assessments.get(position));
        startActivity(intent);
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
}