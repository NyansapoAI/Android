package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Begin_Assessment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin__assessment);
    }

    public void goHome(View v){
        Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void startPreAssessment(View v){
        Intent preIntent = new Intent(getBaseContext(), PreAssessment.class);
        startActivity(preIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }
}
