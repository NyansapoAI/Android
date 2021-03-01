package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class paragraph extends AppCompatActivity {
     MediaPlayer mediaPlayer;
     Button paragraph1;
     Button paragraph2;

    // assessment content
    Assessment_Content assessment_content;
    Assessment assessment;
    String ASSESSMENT_KEY;

    String instructor_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paragraph);
        //mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.select);
        //mediaPlayer.start();
        Intent intent = this.getIntent();


        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        paragraph1 = findViewById(R.id.paragraph1);
        paragraph2 = findViewById(R.id.paragraph2);

        assessment_content = new Assessment_Content();

        assessment = intent.getParcelableExtra("Assessment");
        instructor_id = intent.getStringExtra("instructor_id");
        ASSESSMENT_KEY = assessment.getASSESSMENT_KEY();

        String[] para = getPara(ASSESSMENT_KEY);

        paragraph1.setText(para[0]);
        paragraph2.setText(para[1]);

        paragraph1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startParagraph(v, "0" );
            }
        });

        paragraph2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startParagraph(v, "1");
            }
        });
    }

    public void startParagraph(View v, String  paragraph){
        //mediaPlayer.release();
        Intent myIntent = new Intent(getBaseContext(), paragraph_assessment.class);
        myIntent.putExtra("paragraph", paragraph);
        myIntent.putExtra("Assessment",assessment);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent,ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public String[] getPara(String key){
        switch (key){
            case "3":{
                return assessment_content.getP3();
            }
            case "4":{
                return assessment_content.getP4();
            }
            case "5":{
               return assessment_content.getP5();
            }
            case "6":{
                return assessment_content.getP6();
            }
            case "7":{
                return assessment_content.getP7();
            }
            case "8":{
                return assessment_content.getP8();
            } case "9":{
                return assessment_content.getP9();
            }
            case "10":{
                return assessment_content.getP10();
            } default: return assessment_content.getP3();
        }
    }

}
