package com.example.edward.nyansapo;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class assessment_detail extends AppCompatActivity {

    TextView literacy_level, para_words_wrong, words_wrong_view, letters_wrong_view, question1, question2;
    Button back_button, delete_button;

    Student student;
    Assessment assessment;
    String instructor_id;

    dataBaseHandler dataBaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_detail);

        // find ui elements
        literacy_level = findViewById(R.id.literacy_level_view);
        para_words_wrong = findViewById(R.id.para_wrong_view1);
        words_wrong_view = findViewById(R.id.words_wrong_view1);
        letters_wrong_view = findViewById(R.id.letters_wrong_view1);
        question1 = findViewById(R.id.question1_view1);
        question2 = findViewById(R.id.question2_view1);
        back_button = findViewById(R.id.back_button);
        delete_button = findViewById(R.id.delete_button);

        // get student_activity parcelable object
        Intent intent = getIntent();
        student = intent.getParcelableExtra("student_activity");
        assessment = intent.getParcelableExtra("assessment");
        instructor_id = intent.getStringExtra("instructor_id");

        // set assessment info into ui
        literacy_level.setText(assessment.getLEARNING_LEVEL());
        para_words_wrong.setText(assessment.getPARAGRAPH_WORDS_WRONG());
        words_wrong_view.setText(assessment.getWORDS_WRONG());
        letters_wrong_view.setText(assessment.getLETTERS_WRONG());
        question1.setText(assessment.getSTORY_ANS_Q1());
        question2.setText(assessment.getSTORY_ANS_Q2());
        //Toast.makeText(this, "Q1"+ assessment.getSTORY_ANS_Q1() + " "+ assessment.getSTORY_ANS_Q2(), Toast.LENGTH_LONG).show();

        dataBaseHandler = new dataBaseHandler(this);


        // toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(assessment_detail.this, student_assessments.class);
                intent.putExtra("instructor_id", instructor_id);
                intent.putExtra("student_activity", student);
                startActivity(intent);
                /*
                //startActivity(new Intent(getApplicationContext(), home.class));
                Intent myIntent = new Intent(getBaseContext(), home.class);
                myIntent.putExtra("instructor_id", instructor_id); // its id for now
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(assessment_detail.this).toBundle());

                 */
            }
        });


        /*Toast.makeText(this,assessment.getLETTERS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLETTERS_CORRECT(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getWORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getWORDS_CORRECT(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getPARAGRAPH_WORDS_WRONG(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getSTORY_ANS_Q1(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getSTORY_ANS_Q2(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this,assessment.getLEARNING_LEVEL(), Toast.LENGTH_SHORT).show();*/

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(assessment_detail.this, student_assessments.class);
                intent.putExtra("instructor_id", instructor_id);
                intent.putExtra("student_activity", student);
                startActivity(intent);

            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseHandler.deleteAssessment(assessment.getLOCAL_ID());
                Intent intent = new Intent(assessment_detail.this, student_assessments.class);
                intent.putExtra("instructor_id", instructor_id);
                intent.putExtra("student_activity", student);
                startActivity(intent);

            }
        });

    }
}