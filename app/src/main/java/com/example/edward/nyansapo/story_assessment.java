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

public class story_assessment extends AppCompatActivity {
    MediaPlayer mediaPlayer;
    Button record_student, next_button, story_view, back_button;
    //TextView story_view;
    String story;
    String[] sentences;
    int sentence_count;


    // assessment content
    Assessment_Content assessment_content;
    Assessment assessment;
    String ASSESSMENT_KEY;

    String instructor_id;


    // story reading evaluation
    String story_words_wrong = "";
    int error_count;
    int tries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_assessment);

        //mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.story);
        //mediaPlayer.start();


        Intent intent = getIntent();

        instructor_id = intent.getStringExtra("instructor_id");
        //Toast.makeText(this,instructor_id, Toast.LENGTH_LONG ).show();

        assessment = intent.getParcelableExtra("Assessment");
        ASSESSMENT_KEY = assessment.getASSESSMENT_KEY();

        assessment_content = new Assessment_Content();
        story = getStory(ASSESSMENT_KEY);

        back_button = findViewById(R.id.back_button);
        next_button = findViewById(R.id.next_button);
        story_view = findViewById(R.id.story_view);




        //story = "One day the wind chased the sun away. It told the sun to go to another sky. The sun did not go. The next morning, the wind ran after the sun. The sun fell down and started crying. That is how it began to rain. We clapped for Juma.\\n\\n One day the wind chased the sun away. It told the sun to go to another sky. The sun did not go. The next morning, the wind ran after the sun. The sun fell down and started crying. That is how it began to rain. We clapped for Juma.";
        sentences = story.split("[.]");
        sentence_count = 0;
        tries = 0;
        //story_view.setText(story);
        story_view.setText(sentences[sentence_count]);
        back_button.setEnabled(false);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nextParagraph(v);
                Toast.makeText(story_assessment.this, "Click on the text to read", Toast.LENGTH_LONG).show();
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backParagraph(v);
            }
        });

        story_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeechAsync speechAsync = new SpeechAsync();
                speechAsync.execute(v);
                /*
                Intent myIntent = new Intent(getBaseContext(), storyQuestions.class);
                myIntent.putExtra("Assessment", assessment);
                myIntent.putExtra("instructor_id", instructor_id);
                myIntent.putExtra("question", "0");
                startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(story_assessment.this).toBundle());*/
            }
        });

        // story reading evaluation code
        error_count = 0;

    }

    public void backParagraph(View v){
        if(sentence_count == 0){
            back_button.setEnabled(false);
        }else{
            sentence_count --;
            story_view.setText(sentences[sentence_count]);
            if(sentence_count == 0) back_button.setEnabled(false);
        }
    }


    public void nextParagraph(View v){
        tries = 0;
        if(sentence_count < sentences.length -1){
            //back_button.setEnabled(true);
            sentence_count +=1; // increment sentence count
            story_view.setText(sentences[sentence_count].trim());
        }else{
            Intent myIntent = new Intent(getBaseContext(), storyQuestions.class);
            myIntent.putExtra("Assessment", assessment);
            myIntent.putExtra("instructor_id", instructor_id);
            myIntent.putExtra("question", "0");
            startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        }
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
            story_view = findViewById(R.id.story_view);
            expected_txt = story_view.getText().toString();
            story_view.setEnabled(false);
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
                return " Error"+ err.getMessage().toString();
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
            story_view.setEnabled(true);
            if (s.equalsIgnoreCase("canceled")) {
                Toast.makeText(story_assessment.this, "Internet Connection Failed", Toast.LENGTH_LONG).show();
            } else if (s.equalsIgnoreCase("no match")) {
                Toast.makeText(story_assessment.this, "Try Again", Toast.LENGTH_LONG).show();
            } else {
                String error_txt = SpeechRecognition.compareTranscript(expected_txt, s);
                story_words_wrong += error_txt;
                //error_count += SpeechRecognition.countError(error_txt);
                //Toast.makeText(view.getContext(), "transcript: \'"+ s +"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), "expected: \'"+expected_txt+"\'" , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), error_txt , Toast.LENGTH_LONG).show();
                //Toast.makeText(view.getContext(), Integer.toString(error_count) , Toast.LENGTH_LONG).show();
                if (error_count > 10) { // if error less than 10 move to story level
                    goToThankYou();
                } else if (s != "no match") {

                    if( SpeechRecognition.countError(error_txt) >3 || s.split(" ").length < 2 ){
                        if( tries < 1){
                            tries ++; // incremnent tries
                            Toast.makeText(view.getContext(), "Try Again!" , Toast.LENGTH_LONG).show();
                        }else{
                            error_count += SpeechRecognition.countError(error_txt);
                            nextParagraph(view);
                        }

                    }else{
                        error_count += SpeechRecognition.countError(error_txt);
                        nextParagraph(view);;
                    }
                }
            }
            reco.close();
        }


    }

    public void goToThankYou() { // take to thank you page and grade as paragraph student
        Intent myIntent = new Intent(getBaseContext(), thankYou.class);
        assessment.setLEARNING_LEVEL("PARAGRAPH");
        myIntent.putExtra("Assessment", assessment);
        myIntent.putExtra("instructor_id", instructor_id);
        startActivity(myIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
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
}
