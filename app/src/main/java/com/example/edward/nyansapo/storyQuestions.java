package com.example.edward.nyansapo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognitionResult;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class storyQuestions extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    Button question_button, submit_button, story_button, record_button;
    TextView answer_view, story_view;
    String story_txt;
    int question_count = 0;
    String[] questions;


    // assessment content
    Assessment_Content assessment_content;
    Assessment assessment;
    String ASSESSMENT_KEY;

    // story score
    int story_score;
    NyansapoNLP nyansapoNLP;
    String instructor_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_questions);

        question_button = findViewById(R.id.question_button);
        story_button = findViewById(R.id.story_button);
        submit_button = findViewById(R.id.submit_button);
        answer_view = findViewById(R.id.answer_view);
        record_button = findViewById(R.id.record_button);
        //story_view = findViewById(R.id.story_view);

        Intent intent = getIntent();
        instructor_id = intent.getStringExtra("instructor_id");
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();
        assessment = intent.getParcelableExtra("Assessment");
        ASSESSMENT_KEY = assessment.getASSESSMENT_KEY();

        assessment_content = new Assessment_Content();
        questions = getQuestions(ASSESSMENT_KEY);

        story_score = 0;
        nyansapoNLP =  new NyansapoNLP();


        question_count = Integer.parseInt(intent.getStringExtra("question"));
        question_button.setText(questions[question_count]);
        //mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.questions);
        //mediaPlayer.start();

        story_txt = getStory(ASSESSMENT_KEY);
        //story_view.setText(story_txt);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(v);
            }
        });

        question_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recordStudent(v);
                Toast.makeText(storyQuestions.this, "Click on the mic icon to answer question", Toast.LENGTH_LONG).show();
            }
        });

        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordStudent(v);
            }
        });
        story_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //recordStudent(v);
                Intent myIntent = new Intent(getBaseContext(), QuestionStory.class);
                myIntent.putExtra("Assessment",assessment);
                myIntent.putExtra("instructor_id", instructor_id);
                myIntent.putExtra("story", story_txt);
                myIntent.putExtra("question",Integer.toString(question_count));
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(storyQuestions.this).toBundle());
            }
        });
    }

    public void submitAnswer(View v){
        switch (question_count){
            case 0: {
                question_count++;
                question_button.setText(questions[question_count]);
                assessment.setSTORY_ANS_Q1(answer_view.getText().toString());
                //Toast.makeText(this, answer_view.getText().toString(), Toast.LENGTH_LONG).show();

                answer_view.setText("");
                break;
            }
            case 1 : {
                //Toast.makeText(this, answer_view.getText().toString(), Toast.LENGTH_LONG).show();
                assessment.setSTORY_ANS_Q2(answer_view.getText().toString());
                if(checkAns(assessment) > 0){ // one or all is correct
                    assessment.setLEARNING_LEVEL("ABOVE");
                }else{
                    assessment.setLEARNING_LEVEL("STORY");
                }
                Intent myIntent = new Intent(getBaseContext(), thankYou.class);
                myIntent.putExtra("Assessment",assessment);
                myIntent.putExtra("instructor_id", instructor_id);
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                break;
            }
            default: {

            }
        }
    }

    public int checkAns(Assessment assessment){
        story_score =   nyansapoNLP.evaluateAnswer(assessment.getSTORY_ANS_Q1(), Integer.parseInt(assessment.getASSESSMENT_KEY()), 0) +
        nyansapoNLP.evaluateAnswer(assessment.getSTORY_ANS_Q2(), Integer.parseInt(assessment.getASSESSMENT_KEY()), 1);

        //Toast.makeText(this, Integer.toString(story_score), Toast.LENGTH_LONG).show();

        if(story_score > 110){
            return 1;
        }else {
            return 0;
        }
    }

    public void recordStudent(View v){
        SpeechAsync speechAsync = new SpeechAsync();
        speechAsync.execute(v);
    }

    private class SpeechAsync extends AsyncTask<View,String,String > {

        // Replace below with your own subscription key
        String speechSubscriptionKey = "1c58abdab5d74d5fa41ec8b0b4a62367";

        // Replace with your own subscription key and region identifier from here: https://aka.ms/speech/sdkregion
        String serviceRegion = "eastus";

        String endpoint = "275310be-2c21-4131-9609-22733b4e0c04";

        SpeechConfig config;

        SpeechRecognizer reco;

        View view;

        String expected_txt;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            config = SpeechConfig.fromSubscription(speechSubscriptionKey, serviceRegion);
            config.setEndpointId(endpoint);
            question_button.setEnabled(false);
            record_button.setEnabled(false);
        }

        @Override
        protected String doInBackground(View... views) {
            try{
                view = views[0];

                reco = new SpeechRecognizer(config);

                SpeechRecognitionResult result = null;

                Future<SpeechRecognitionResult> task = reco.recognizeOnceAsync();
                assert(task != null);

                // Note: this will block the UI thread, so eventually, you want to
                //        register for the event (see full samples)
                try {
                    result = task.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                assert(result != null);

                if(result.getReason() == ResultReason.RecognizedSpeech){
                    return result.getText();
                }else if(result.getReason() == ResultReason.NoMatch){
                    return "no match";
                }else if(result.getReason() == ResultReason.Canceled){
                    return "canceled";
                }

            }catch (Error err){
                return " Error"+ err.getMessage();
            }
            return null;
        }


        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
            reco.close();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.equalsIgnoreCase("canceled")){
                Toast.makeText(storyQuestions.this,"Internet Connection Failed", Toast.LENGTH_LONG).show();
            }else if (s.equalsIgnoreCase("no match")){
                Toast.makeText(storyQuestions.this,"Try Again", Toast.LENGTH_LONG).show();
            }else {
                answer_view.setText(s);
            }
            question_button.setEnabled(true);
            record_button.setEnabled(true);
            reco.close();
        }
    }

    public String getStory(String key){
        switch (key){
            case "3":{
                return assessment_content.getS3();
            }
            case "4":{
                return assessment_content.getS4();
            }
            case "5":{
                return assessment_content.getS5();
            }
            case "6":{
                return assessment_content.getS6();
            }
            case "7":{
                return assessment_content.getS7();
            }
            case "8":{
                return assessment_content.getS8();
            } case "9":{
                return assessment_content.getS9();
            }
            case "10":{
                return assessment_content.getS10();
            } default: return assessment_content.getS3();
        }
    }


    public String[] getQuestions(String key){
        switch (key){
            case "3":{
                return assessment_content.getQ3();
            }
            case "4":{
                return assessment_content.getQ4();
            }
            case "5":{
                return assessment_content.getQ5();
            }
            case "6":{
                return assessment_content.getQ6();
            }
            case "7":{
                return assessment_content.getQ7();
            }
            case "8":{
                return assessment_content.getQ8();
            } case "9":{
                return assessment_content.getQ9();
            }
            case "10":{
                return assessment_content.getQ10();
            } default: return assessment_content.getQ3();
        }
    }
}
