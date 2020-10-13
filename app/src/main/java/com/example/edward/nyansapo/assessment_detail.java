package com.example.edward.nyansapo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        // get student parcelable object
        Intent intent = getIntent();
        student = intent.getParcelableExtra("student");
        assessment = intent.getParcelableExtra("assessment");
        instructor_id = intent.getStringExtra("instructor_id");

        // set assessment info into ui
        literacy_level.setText(assessment.getLEARNING_LEVEL());
        para_words_wrong.setText(assessment.getPARAGRAPH_WORDS_WRONG());
        words_wrong_view.setText(assessment.getWORDS_WRONG());
        letters_wrong_view.setText(assessment.getLETTERS_WRONG());
        question1.setText(assessment.getSTORY_ANS_Q1());
        question2.setText(assessment.getSTORY_ANS_Q2());

        dataBaseHandler = new dataBaseHandler(this);

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
                intent.putExtra("student", student);
                startActivity(intent);

            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataBaseHandler.deleteAssessment(assessment.getLOCAL_ID());
                Intent intent = new Intent(assessment_detail.this, student_assessments.class);
                intent.putExtra("instructor_id", instructor_id);
                intent.putExtra("student", student);
                startActivity(intent);

            }
        });

    }
}