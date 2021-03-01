package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class QuestionStory extends AppCompatActivity {

    TextView story_view;
    Button back_button;

    String story_txt;
    int question_count;

    Assessment assessment;
    String instructor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_story);

        story_view = findViewById(R.id.story_view);
        back_button = findViewById(R.id.back_button);

        Intent intent = getIntent();
        story_txt = intent.getStringExtra("story");
        story_view.setText(story_txt);

        question_count = Integer.parseInt(intent.getStringExtra("question"));

        assessment = intent.getParcelableExtra("Assessment");
        instructor_id = intent.getStringExtra("instructor_id");

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (question_count){
                    case 0:{
                        Intent myIntent = new Intent(getBaseContext(), storyQuestions.class);
                        myIntent.putExtra("Assessment",assessment);
                        myIntent.putExtra("instructor_id", instructor_id);
                        myIntent.putExtra("question",Integer.toString(question_count));
                        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(QuestionStory.this).toBundle());

                    }
                    case 1: {
                        Intent myIntent = new Intent(getBaseContext(), storyQuestions.class);
                        myIntent.putExtra("Assessment",assessment);
                        myIntent.putExtra("instructor_id", instructor_id);
                        myIntent.putExtra("question",Integer.toString(question_count));
                        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(QuestionStory.this).toBundle());

                    }
                }
            }
        });


    }
}