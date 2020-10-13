package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class student extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
    }

    public void exportData(View v){
        Toast.makeText(getApplicationContext(), "Data has been exported successfully", Toast.LENGTH_LONG).show();
    }


    public void newSchool(View v){
        Intent myIntent = new Intent(getBaseContext(), registerSchool.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void beginAssessment(View v){
        Intent myIntent = new Intent(getBaseContext(), selectAssesment.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void newStudent(View v){
        Intent myIntent = new Intent(getBaseContext(), registerStudent.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void selectFilter(View v){
        Toast.makeText(getApplicationContext(), "Selected Filter", Toast.LENGTH_LONG).show();
    }

    public void selectSort(View v){
        Toast.makeText(getApplicationContext(), "Selected Sort", Toast.LENGTH_LONG).show();
    }
}
