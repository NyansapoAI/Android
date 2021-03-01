package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import androidx.appcompat.app.AppCompatActivity;

public class Interface_selecter extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface_selecter);
    }
/*
    public void startTeacherInterface(View v){
        Intent myIntent = new Intent(getBaseContext(), Begin_Assessment.class);
        startActivity(myIntent);
    }
*/
    public void startStudentInterface(View v){
        Intent myIntent = new Intent(getBaseContext(), registerStudent.class);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void goHome(View v){
        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

}
