package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TeacherLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);
    }

    public void startSelector(View v){
        Intent myIntent = new Intent(getBaseContext(), selectSchool.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void goHome(View v){
        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void registerTeacher(View v){
        Intent myIntent = new Intent(getBaseContext(), RegisterTeacher.class);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void recordStudent(View view) {
    }

    public void nextAssessment(View view) {
    }

    public void thankYou(View view) {
    }

    public void changeParagraph(View view) {
    }

    public void startWord(View view) {
    }

    public void questionOne(View view) {
    }

    public void submitAnswers(View view) {
    }
}
