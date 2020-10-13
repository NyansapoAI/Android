package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SelectAssessment extends AppCompatActivity  implements View.OnClickListener{

    Button button3, button4, button5, button6, button7, button8, button9, button10;

    Student student;

    String instructor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_assessment);

        Intent intent = getIntent();
        student = intent.getParcelableExtra("student");
        instructor_id = intent.getStringExtra("instructor_id");
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();

        button3 = findViewById(R.id.assessment3_button);
        button4 = findViewById(R.id.assessment4_button);
        button5 = findViewById(R.id.assessment5_button);
        button6 = findViewById(R.id.assessment6_button);
        button7 = findViewById(R.id.assessment7_button);
        button8 = findViewById(R.id.assessment8_button);
        button9 = findViewById(R.id.assessment9_button);
        button10 = findViewById(R.id.assessment10_button);

        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        button10.setOnClickListener(this);

        //button6.setEnabled(false);
        //button7.setEnabled(false);
        button8.setEnabled(false);
        button9.setEnabled(false);
        button10.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.assessment3_button:{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","3");
                myIntent.putExtra("student_id",student.getLocal_id());
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            case R.id.assessment4_button:{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","4");
                myIntent.putExtra("student_id",student.getLocal_id());
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            case R.id.assessment5_button:{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","5");
                myIntent.putExtra("student_id",student.getLocal_id());
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            case R.id.assessment6_button:{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","6");
                myIntent.putExtra("student_id",student.getLocal_id());
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            case R.id.assessment7_button:{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","7");
                myIntent.putExtra("student_id",student.getLocal_id());
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            case R.id.assessment8_button:{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","8");
                myIntent.putExtra("student_id",student.getLocal_id());
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            case R.id.assessment9_button:{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","9");
                myIntent.putExtra("student_id",student.getLocal_id());
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            case R.id.assessment10_button:{
                Intent myIntent = new Intent(getBaseContext(), PreAssessment.class);
                myIntent.putExtra("ASSESSMENT_KEY","10");
                myIntent.putExtra("student_id",student.getLocal_id());
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            default:{

            }
        }
    }
}