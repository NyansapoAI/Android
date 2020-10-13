package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class selectAssesment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_assesment);
    }
    
    public void exportData(View v){
        Toast.makeText(getApplicationContext(), "Data has been exported successfully", Toast.LENGTH_LONG).show();
    }

    public void newAssessment(View v){
        Intent myIntent = new Intent(getBaseContext(), Begin_Assessment.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void selectAssessment(View v){
        Intent myIntent = new Intent(getBaseContext(), viewAssessment.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void selectFilter(View v){
        Toast.makeText(getApplicationContext(), "Selected Filter", Toast.LENGTH_LONG).show();
    }

    public void selectSort(View v){
        Toast.makeText(getApplicationContext(), "Selected Sort", Toast.LENGTH_LONG).show();
    }

}
